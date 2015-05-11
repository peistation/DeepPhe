package edu.pitt.dbmi.deep.phe.i2b2;

//import static org.semanticweb.owlapi.search.Searcher.annotations;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class OwlOntologyInspector {

	public static final String CONST_DEEPPHE_ONT_PATH = "C:\\Users\\kjm84\\Desktop\\DeepPheOntologies\\BreastCancer.owl";

	public static void main(String[] args) {
		OwlOntologyInspector inspector = new OwlOntologyInspector();
		try {
			inspector.conductTrials();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void conductTrials() throws OWLOntologyCreationException,
			OWLOntologyStorageException, IOException {
		// examineDeepPheOntology();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = loadDeepPheOntology(m);
//		System.out.println("\n\nTrace Carboplatin super classes...");
//		traceSuperClses(
//				o,
//				IRI.create("http://slidetutor.upmc.edu/deepPhe/BreastCancer.owl#Carboplatin"));
//		System.out.println("\n\nTrace Medication sub classes...");
//		traceSubClses(
//				o,
//				IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Medication"));
//		System.out.println("\n\nTrace I_03074 sub classes...");
//		traceSubClses(
//				o,
//				IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#I_03074"));
//		System.out.println("\n\nTrace Entity sub classes...");
//		traceSubClses(
//				o,
//				IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Entity"));
//		System.out.println("\n\nTrace Patient super classes...");
//		traceSubClses(
//				o,
//				IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Patient"));
		traceSubClses(
		o,
		IRI.create("http://slidetutor.upmc.edu/deepPhe/BreastCancer.owl#Breast_Cancer_TNM_Finding_v7"));
	
	}

	private OWLOntology loadDeepPheOntology(OWLOntologyManager manager)
			throws IOException, OWLOntologyCreationException {
		File f = new File(CONST_DEEPPHE_ONT_PATH);
		String fText = FileUtils.readFileToString(f, "UTF-8");
		OWLOntology o = manager
				.loadOntologyFromOntologyDocument(new StringDocumentSource(
						fText));
		return o;
	}

	private void traceSubClses(OWLOntology o, IRI iri) throws IOException,
			OWLOntologyCreationException {
		OWLReasonerFactory reasonerFactory;
		reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(o);
		OWLDataFactory fac = o.getOWLOntologyManager().getOWLDataFactory();
		OWLClass tgtCls = fac.getOWLClass(iri);
		Queue<OWLClass> clsQueue = new LinkedList<>();
		clsQueue.add(tgtCls);
		while (true) {
			OWLClass cls = clsQueue.poll();
			if (cls == null) {
				break;
			} else {
				System.out.println("Class: " + cls.getIRI().toString());
				System.out.println("rdfs:Label " + labelFor(o, cls));
				System.out.println(("prefCui " + prefCuiFor(o, cls)));
				/*reasoner.getSubClasses(cls, true).getFlattened().stream()
						.forEach((subCls) -> {
							clsQueue.add(subCls);
						});*/
				for(OWLClass subCls: reasoner.getSubClasses(cls, true).getFlattened()){
					clsQueue.add(subCls);
				}
			}
		}
	}

	private void traceSuperClses(OWLOntology o, IRI iri) throws IOException,
			OWLOntologyCreationException {
		OWLReasonerFactory reasonerFactory;
		reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(o);
		OWLDataFactory fac = o.getOWLOntologyManager().getOWLDataFactory();
		OWLClass tgtCls = fac.getOWLClass(iri);
		Queue<OWLClass> clsQueue = new LinkedList<OWLClass>();
		clsQueue.add(tgtCls);
		while (true) {
			OWLClass cls = clsQueue.poll();
			if (cls == null) {
				break;
			} else {
				System.out.println("Class: " + cls.getIRI().toString());
				System.out.println("rdfs:Label " + labelFor(o, cls));
				/*reasoner.getSuperClasses(cls, true).getFlattened().stream()
						.forEach((superCls) -> {
							clsQueue.add(superCls);
						});*/
				for(OWLClass superCls: reasoner.getSuperClasses(cls, true).getFlattened()){
					clsQueue.add(superCls);
				}
			}
		}
	}
	
	private String prefCuiFor(OWLOntology ontology, OWLClass cls) {
		/*
		 * Use a visitor to extract label annotations
		 */
		PreferredCuiExtractor pce = new PreferredCuiExtractor();
		for (OWLAnnotation anno : annotations(ontology
				.getAnnotationAssertionAxioms(cls.getIRI()))) {
			anno.accept(pce);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		if (pce.getResult() != null) {
			return pce.getResult();
		} else {
			return cls.getIRI().toString();
		}
	}

	private String labelFor(OWLOntology ontology, OWLClass cls) {
		/*
		 * Use a visitor to extract label annotations
		 */
		LabelExtractor le = new LabelExtractor();
		for (OWLAnnotation anno : annotations(ontology
				.getAnnotationAssertionAxioms(cls.getIRI()))) {
			anno.accept(le);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		if (le.getResult() != null) {
			return le.getResult();
		} else {
			return cls.getIRI().toString();
		}
	}
	private Set<OWLAnnotation> annotations(Set<OWLAnnotationAssertionAxiom> axioms){
    	Set<OWLAnnotation> aa = new LinkedHashSet<OWLAnnotation>();
    	for(OWLAnnotationAssertionAxiom a: axioms){
    		aa.add(a.getAnnotation());
    	}
    	return aa;
    }
}
