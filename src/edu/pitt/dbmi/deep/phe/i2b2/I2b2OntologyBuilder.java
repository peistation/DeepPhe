package edu.pitt.dbmi.deep.phe.i2b2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

/**
 *
 * @author kjm84
 */
public class I2b2OntologyBuilder {

	public static final String CONST_TOP_LEVEL_ENTRY = "http://slidetutor.upmc.edu/deepPhe/BreastCancer.owl#Breast_Cancer_TNM_Finding_v7";

	private String ontologyPath;

	final TreeSet<PartialPath> partialPaths = new TreeSet<PartialPath>();
	final HashMap<String, PartialPath> partialPathMap = new HashMap<String, PartialPath>();

	public void execute() throws OWLOntologyCreationException, IOException,
			ClassNotFoundException, SQLException {
		partialPaths.addAll(extractOntologyPartialPaths());
		displayPaths(partialPaths);
		for (PartialPath partialPath : partialPaths) {
			partialPathMap.put(partialPath.getBaseCode(), partialPath);
		}
	}

	private TreeSet<PartialPath> extractOntologyPartialPaths()
			throws OWLOntologyCreationException, IOException {

		final TreeSet<PartialPath> partialPaths = new TreeSet<PartialPath>();

		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		// OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
		OWLOntology o = loadDeepPheOntology(m);
		OWLReasonerFactory reasonerFactory;
		reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(o);
		OWLDataFactory fac = m.getOWLDataFactory();
		OWLClass elementConcept = fac.getOWLClass(IRI
				.create(CONST_TOP_LEVEL_ENTRY));

		final Queue<PartialPath> partialPathQueue = new LinkedList<PartialPath>();
		NodeSet<OWLClass> subClses = reasoner.getSubClasses(elementConcept,
				true);
		for (Node<OWLClass> subCls : subClses) {
			PartialPath path = new PartialPath();
			path.setReasoner(reasoner);
			path.setCls(subCls.getRepresentativeElement());
			path.setLevel(1);
			partialPathQueue.add(path);
		}

		while (true) {
			PartialPath path;
			path = partialPathQueue.poll();
			if (path == null) {
				break;
			} else {
				partialPathQueue.addAll(path.expand());
			}
			partialPaths.add(path);
		}

		PartialPath topLevel = new PartialPath();
		topLevel.setPath("\\DEEPPHE");
		topLevel.setLevel(0);
		topLevel.setLeaf(false);
		partialPaths.add(topLevel);

		return partialPaths;
	}

	private OWLOntology loadDeepPheOntology(OWLOntologyManager manager)
			throws IOException, OWLOntologyCreationException {
		File f = new File(ontologyPath);
		String fText = FileUtils.readFileToString(f, "UTF-8");
		OWLOntology o = manager
				.loadOntologyFromOntologyDocument(new StringDocumentSource(
						fText));
		return o;
	}

	private void displayPaths(TreeSet<PartialPath> paths) {
		/*paths.stream().forEach((path) -> {
			System.out.println(path);
		});*/
		for(PartialPath p: paths){
			System.out.println(p);
		}
	}

	public void setOntologyPath(String ontologyPath) {
		this.ontologyPath = ontologyPath;

	}

	public HashMap<String, PartialPath> getPartialPathMap() {
		return partialPathMap;
	}

}
