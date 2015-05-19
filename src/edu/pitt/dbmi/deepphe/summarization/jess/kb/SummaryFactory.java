package edu.pitt.dbmi.deepphe.summarization.jess.kb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Quantity;

import edu.pitt.dbmi.deep.phe.model.*;
import edu.pitt.dbmi.deep.phe.model.Cancer.Tumor;
import edu.pitt.dbmi.deep.phe.util.OntologyUtils;
import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;

/**
 * Go back-and-forth between Summary and FHIR objects
 * @author tseytlin
 *
 */
public class SummaryFactory {
	/**
	 * 
	 * @param code
	 * @param s
	 */
	private static void setSummaryFromCode(CodeableConcept code, Summary s){
		String cui = Utils.getConceptCode(code);
		String name = Utils.getConceptName(code);
		s.setCode(cui);
		s.setBaseCode(cui);
		s.setPreferredTerm(name);
	}
	
	public static Patient getPatient( edu.pitt.dbmi.deep.phe.model.Patient p){
		Patient patient = new Patient();
		//TODO: nothing to fill for now
		return patient;
	}
	
	
	/**
	 * create an Summary encounter object from FHIR Report
	 * @param report
	 * @return
	 */
	public static Encounter getEncounter(Report r){
		// create an encounter representing the document in question
		Encounter encounter = new Encounter();
		encounter.setContent(r.getTextSimple());
		encounter.setKind(r.getType().getTextSimple());
		encounter.setUri(r.getIdentifierSimple());
					
		// add all report elements to an encounter
		for(Element e: r.getReportElements()){
			// convert each element
			Summary summary = getSummary(e);
			if(summary == null)
				continue;
			
			summary.setSummarizableId(encounter.getId());
			encounter.addSummary(summary);
			
			// if Dx, add Stage as well (if available)
			if(e instanceof edu.pitt.dbmi.deep.phe.model.Diagnosis){
				for(Summary s: getStageSummary(((edu.pitt.dbmi.deep.phe.model.Diagnosis)e).getStage())){
					s.setSummarizableId(encounter.getId());
					encounter.addSummary(s);
				}
			}
		}
		return encounter;
	}

	/**
	 * convert Stage object to summary
	 * @param st
	 * @return
	 */
	public static List<Summary> getStageSummary(Stage st){
		List<Summary> list = new ArrayList<Summary>();
		if(st != null){
			TnmTgrade tnmTgrade = new TnmTgrade();
			setSummaryFromCode(st.getPrimaryTumorStageCode(),tnmTgrade);
			tnmTgrade.setGroupIndex(1);
			tnmTgrade.setProvidingDepartment("Clinical");
			tnmTgrade.setUnitOfMeasure("NA");
			tnmTgrade.setValue(st.getPrimaryTumorStage());
			list.add(tnmTgrade);
			
			TnmNgrade tnmNgrade = new TnmNgrade();
			setSummaryFromCode(st.getRegionalLymphNodeStageCode(),tnmNgrade);
			tnmNgrade.setGroupIndex(1);
			tnmNgrade.setProvidingDepartment("Clinical");
			tnmNgrade.setUnitOfMeasure("NA");
			tnmNgrade.setValue(st.getRegionalLymphNodeStage());
			list.add(tnmNgrade);
			
			TnmMgrade tnmMgrade = new TnmMgrade();
			setSummaryFromCode(st.getDistantMetastasisStageCode(), tnmMgrade);
			tnmMgrade.setGroupIndex(1);
			tnmMgrade.setProvidingDepartment("Clinical");
			tnmMgrade.setUnitOfMeasure("NA");
			tnmMgrade.setValue(st.getDistantMetastasisStage());
			list.add(tnmMgrade);
		}
		return list;
	}
	
	/**
	 * create Summary element from a FHIR Element
	 * @param e
	 * @return
	 */
	public static Summary getSummary(Element e) {
		CodeableConcept code = null;
		Summary summary = null;
		if(e instanceof edu.pitt.dbmi.deep.phe.model.Diagnosis){
			code = ((edu.pitt.dbmi.deep.phe.model.Diagnosis)e).getCode();
			summary = new Diagnosis();
		}else if(e instanceof Observation){
			Observation o = (Observation) e;
			code = o.getName();
			String name = code.getTextSimple();
			if(name.toLowerCase().contains("estrogen receptor"))
				summary = new Er();
			else if(name.toLowerCase().contains("progesterone receptor"))
				summary = new Pr();
			else if(name.toLowerCase().contains("her2"))
				summary = new Her2();
			else if(name.toLowerCase().contains("tumor size")){
				summary = new TumorSize();
				Quantity q = (Quantity) o.getValue();
				((TumorSize)summary).setGreatestDimension(q.getValueSimple().doubleValue());
				((TumorSize)summary).setUnitOfMeasure(q.getUnitsSimple());
			}
			summary.setValue(o.getValueSimple());
		}
		// setup common parameters
		if(summary != null && code != null){
			setSummaryFromCode(code,summary);
		}
		return summary;
	}
	
	/**
	 * create a stage object from a list of summaries
	 * @param list
	 * @return
	 */
	public static Stage getStage(List<Summary> list){
		return null;
	}
	
	
	/**
	 * get FHIR cancer phenotype object from summary patient object
	 * @param patient
	 * @return
	 */
	public static Cancer getCancerPhenotype(Patient patient){
		IOntology ont = ResourceFactory.getInstance().getOntology();
		OntologyUtils ou = ResourceFactory.getInstance().getOntologyUtils();
		
		//TODO: need to infer primary Dx and tumors from summary phenotype
		// I have to hard-code this for NOW :(
		Cancer cancer = new Cancer();
		cancer.initialize(ont.getClass("Breast_Carcinoma"));
		Stage stage = getStage(patient.getSummaries());
		if(stage != null){
			cancer.setStage(stage);
		}
		
		// add tumor
		Tumor tumor = cancer.addTumor();
		tumor.setType(cancer.getCode());
		
		for(Summary summary : patient.getSummaries()){
			IClass cls = ou.getClass(summary.getCode());
			if(cls != null){
				Condition.ConditionEvidenceComponent factor = null;
				
				// classify the evidence into several factories
				if(cls.hasSuperClass(ont.getClass(Utils.PHENOTYPIC_FACTOR))){
					factor = tumor.addPhenotypicFactors();
				}else if(cls.hasSuperClass(ont.getClass(Utils.GENOMIC_FACTOR))){
					factor = tumor.addGenomicFactors();
				}else if(cls.hasSuperClass(ont.getClass(Utils.TREATMENT_FACTOR))){
					factor = tumor.addTreatmentFactors();
				}else{
					factor = tumor.addRelatedFactors();
				}
				
				if(factor != null){
					factor.setCode(Utils.getCodeableConcept(cls));
				}
			}
		}
		
		return cancer;
	}
	
}
