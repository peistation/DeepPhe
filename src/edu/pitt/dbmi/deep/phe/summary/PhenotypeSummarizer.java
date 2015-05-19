package edu.pitt.dbmi.deep.phe.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Quantity;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceReference;

import edu.pitt.dbmi.deep.phe.model.Cancer;
import edu.pitt.dbmi.deep.phe.model.Diagnosis;
import edu.pitt.dbmi.deep.phe.model.Element;
import edu.pitt.dbmi.deep.phe.model.Observation;
import edu.pitt.dbmi.deep.phe.model.Patient;
import edu.pitt.dbmi.deep.phe.model.Report;
import edu.pitt.dbmi.deep.phe.model.ResourceFactory;
import edu.pitt.dbmi.deep.phe.model.Stage;
import edu.pitt.dbmi.deep.phe.model.Utils;
import edu.pitt.dbmi.deep.phe.model.Cancer.Tumor;
import edu.pitt.dbmi.deep.phe.util.OntologyUtils;
import edu.pitt.dbmi.deep.phe.util.TextUtils;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;

public class PhenotypeSummarizer {

	/**
	 * aggregate encounter level data and put it into Cancer phenotype object
	 * @param reports
	 * @return
	 */
	public Cancer getCancerPhenotype(List<Report> reports){
		IOntology ont = ResourceFactory.getInstance().getOntology();
		
		IClass primaryDx = getPrimaryDiagnosis(reports);
		Stage stage = getFinalStage(reports);
		Patient patient = getPatient(reports);
		
		// I have to hard-code this for NOW :(
		Cancer cancer = new Cancer();
		cancer.initialize(primaryDx);
		if(stage != null){
			cancer.setStage(stage);
		}
		cancer.setSubjectTarget(patient);
		cancer.setSubject(Utils.getResourceReference(patient));
	
		// go over encounter level data
		Tumor tumor = cancer.addTumor();
		for(Report report: reports){
			// set type for a tumor
			for(Diagnosis dx: report.getDiagnoses()){
				IClass dxCls = Utils.getConceptClass(dx.getCode());
				if(dxCls != null && dxCls.hasSuperClass(primaryDx)){
					//for now, just set this Dx as the tupy of tumor
					if(tumor.getType() == null){
						tumor.setType(dx.getCode());
					}
					if(tumor.getLocation().isEmpty()){
						for(Condition.ConditionLocationComponent location: dx.getLocation()){
							tumor.getLocation().add(location);
						}
					}
				}
			}
			for(Element e : report.getReportElements()){
				IClass cls = e.getConceptClass();
				if(cls != null){
					Condition.ConditionEvidenceComponent factor = null;
					
					// skip report elements that we don't care for
					if(e instanceof Diagnosis)
						continue;
					
					// classify the evidence into several factories
					if(cls.hasSuperClass(ont.getClass(Utils.PHENOTYPIC_FACTOR))){
						factor = tumor.addPhenotypicFactors();
					}else if(cls.hasSuperClass(ont.getClass(Utils.GENOMIC_FACTOR))){
						factor = tumor.addGenomicFactors();
					}else if(cls.hasSuperClass(ont.getClass(Utils.TREATMENT_FACTOR))){
						factor = tumor.addTreatmentFactors();
					}
					//else{
					//	factor = tumor.addRelatedFactors();
					//}
					
					if(factor != null){
						CodeableConcept code = Utils.getCodeableConcept(cls);
						if(e instanceof Observation && ((Observation) e).getValue() instanceof Quantity){
							code.setTextSimple(code.getTextSimple()+": "+((Observation) e).getValueSimple());
						}
						factor.setCode(code);
						factor.getDetailTarget().add((Resource) e);
						Utils.getResourceReference(factor.addDetail(),e);
					}
				}
			}
			
		}
		return cancer;
		
	}
	
	private Patient getPatient(List<Report> reports) {
		Patient p = null;
		for(Report report: reports){
			if(report.getSubjectTarget() != null)
				return (Patient) report.getSubjectTarget();
		}
		return p;
	}

	private Stage getFinalStage(List<Report> reports) {
		Stage stage = null;
		for(Report report: reports){
			for(Diagnosis dx: report.getDiagnoses()){
				if(dx.getStage() != null)
					stage = dx.getStage();
			}
		}
		return stage;
	}

	private IClass getPrimaryDiagnosis(List<Report> reports) {
		IClass primaryDx = null;
		for(Report report: reports){
			// set type for a tumor
			for(Diagnosis dx: report.getDiagnoses()){
				IClass dxCls = Utils.getConceptClass(dx.getCode());
				if(primaryDx == null)
					primaryDx = dxCls;
				else if(primaryDx.hasSuperClass(dxCls))
					primaryDx = dxCls;
			}
		}
		return primaryDx;
	}

	public static void main(String [] args) throws Exception{
		File project = new File("/home/tseytlin/Work/DeepPhe/");
		File ontology = new File(project,"ontologies/breastCancer.owl");//breastCAEx.owl
		File sample = new File(project,"data/sample");
		File out = new File(sample,"fhir");
		File types = new File(project,"data/desc/TypeSystem.xml");
		
		// load ontology
		System.out.println("loading ontology .."+ontology.getName());
		IOntology ont = OOntology.loadOntology(ontology);
		DocumentSummarizer summarizer = new DocumentSummarizer(ont);
		
		File [] docs = new File(sample,"xmi").listFiles();
		Arrays.sort(docs);
		// process reports
		List<Report> reports = new ArrayList<Report>();
		for(File file: docs){
			System.out.println("reading XMI file .."+file.getName());
			JCas cas = summarizer.loadCAS(file,types);
			System.out.println("generating report summary ..");
			Report report = summarizer.process(cas);
			report.setTitleSimple(TextUtils.stripSuffix(file.getName()));
			//report.save(new File(out,"CT"));
			System.out.println(report.getSummary());
			reports.add(report);
		}
		
		// create a phenotype object form a set of reports
		System.out.println("generating phenotype summary ..\n");
		PhenotypeSummarizer phenotypeSummarizer =  new PhenotypeSummarizer();
		Cancer phenotype = phenotypeSummarizer.getCancerPhenotype(reports);
		phenotype.save(new File(out,"Phenotype"));
		System.out.println(phenotype.getSummary());
		
	}
}
