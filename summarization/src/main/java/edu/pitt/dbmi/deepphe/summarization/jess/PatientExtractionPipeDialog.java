package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.pitt.dbmi.deepphe.i2b2.I2B2DataDataWriter;
import edu.pitt.dbmi.deepphe.i2b2.I2b2OntologyBuilder;
import edu.pitt.dbmi.deepphe.i2b2.PartialPath;
import edu.pitt.dbmi.deepphe.i2b2.Utilities;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Summary;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.I2b2DataDataSourceManager;

public class PatientExtractionPipeDialog extends JDialog implements Runnable,
		ActionListener {

	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport pcs;
	private String message;
	private TreeSet<PartialPath> partialPathTreeSet;
	private HashMap<String, PartialPath> partialPathMap;

	private JTextPane messageText;
	private JScrollPane paneScrollPane;
	private PatientKnowledgeExtractor patientKnowledgeExtractor;
	private List<Patient> patients;
	
	private AnnotationTabPanel annotationTabPanel;

	private JButton confirmationButton = new JButton("Ok");

	public PatientExtractionPipeDialog(Frame parent) {
		super(parent, "DeepPhe Patient Summary Extractor", false);
		messageText = new JTextPane();
		paneScrollPane = new JScrollPane(messageText);
		getContentPane().add(paneScrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(confirmationButton);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		confirmationButton.setEnabled(false);
		confirmationButton.setActionCommand("closeDialog");
		confirmationButton.addActionListener(this);

		pack();
		setSize(new Dimension(400, 200));
		setResizable(false);
		setLocationRelativeTo(parent);
		pcs = new PropertyChangeSupport(this);
	}

	@Override
	public void run() {
		setMessage("Begin Processing");
		sliceOntology();
		clearOldSummaryData();
		extractEncounterKnowledge();
		extractPatientKnowledge();
		annotationTabPanel.reBuild();
		ontologizeAndActiveSummaries();
		replaceI2b2Data();
		setMessage("Finished Processing");
		confirmationButton.setEnabled(true);
	}

	private void replaceI2b2Data() {
		try {
			final I2b2DataDataSourceManager i2b2DataDataSourceManager = new I2b2DataDataSourceManager();
			setMessage("Initialized a connecton to I2B2");
			final I2B2DataDataWriter i2b2DataDataWriter = new I2B2DataDataWriter();
			i2b2DataDataWriter.setDataSourceMgr(i2b2DataDataSourceManager);
			i2b2DataDataWriter.setSourceSystemCd("DEEPPHE2");
			setMessage("Clean out old data.");
			i2b2DataDataWriter.cleanOldRecords();
			i2b2DataDataWriter.setPatients(patients);
			setMessage("Push new Patient and Encounter data to i2b2.");
			i2b2DataDataWriter.execute();
			i2b2DataDataSourceManager.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void ontologizeAndActiveSummaries() {
		for (Patient patient : patients) {
			for (Summary summary : patient.getSummaries()) {
				ontologizeSummary(summary);
			}
			for (Encounter encounter : patient.getEncounters()) {
				for (Summary summary : encounter.getSummaries()) {
					ontologizeSummary(summary);
				}
			}
		}
		setMessage("Coupled ontology to summary knowledge inference objects.");

	}

	private void ontologizeSummary(Summary summary) {
		String code = "umls:" + summary.getCode();
		PartialPath pathForCode = partialPathMap.get(code);
		if (pathForCode != null) {
			summary.setBaseCode(pathForCode.getBaseCode());
			summary.setPath(pathForCode.getPath());
			summary.setNameChar(Utilities.extractCname(pathForCode));
			summary.setIsActive(1);
		} else {
			System.err.println("Failed to activate summary: " + summary);
			summary.setIsActive(0);
		}
	}

	private void extractPatientKnowledge() {
		patientKnowledgeExtractor.setPatients(patients);
		patientKnowledgeExtractor.execute();
		setMessage("Performed Patient level summary inferences and queued results.");

	}

	private void extractEncounterKnowledge() {
		EncounterKnowledgeExtractor encounterKnowledgeExtractor = EncounterKnowlegeExractorFactory
				.getEncounterKnowledgeExtractor();
		for (Patient patient : patients) {
			encounterKnowledgeExtractor.setPatient(patient);
			encounterKnowledgeExtractor.execute();
		}
		setMessage("Extracted encounter level knowledge.");
	}

	private void clearOldSummaryData() {
		for (Patient patient : patients) {
			patient.clearSummaries();
			for (Encounter encounter : patient.getEncounters()) {
				encounter.clearSummaries();
			}
		}
		setMessage("Cleared any previous inference output.");
	}


	private void sliceOntology() {
		try {
			if (partialPathTreeSet.isEmpty()) {
				setMessage("Begin Ontology Slicing");
				final I2b2OntologyBuilder i2b2OntologyBuilder = new I2b2OntologyBuilder();
				i2b2OntologyBuilder
						.setOntologyPath("/home/tseytlin/Work/DeepPhe/ontologies/breastCancer.owl"); //"..\\ontologies\\breastCancer.owl"
				i2b2OntologyBuilder.setSourceSystemCode("DEEPPHE2");
				final List<String> topLevelClses = new ArrayList<String>();
				topLevelClses
						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Receptor_Status");
				topLevelClses
						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Breast_Carcinoma_by_Gene_Expression_Profile");
				topLevelClses
						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Generic_TNM_Finding");
//				topLevelClses
//						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Generic_Primary_Tumor_TNM_Finding");
//				topLevelClses
//						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Generic_Regional_Lymph_Nodes_TNM_Finding");
				topLevelClses
						.add("http://dbmi.pitt.edu/deepphe/ontologies/breastCancer.owl#Tumor_Size");

				i2b2OntologyBuilder.setTopLevelClses(topLevelClses);
				i2b2OntologyBuilder.setPartialPathTreeSet(partialPathTreeSet);
				i2b2OntologyBuilder.setPartialPathMap(partialPathMap);
				setMessage("Slicing Ontology "
						+ i2b2OntologyBuilder.getOntologyPath());
				i2b2OntologyBuilder.execute();
				setMessage("Done Slicing Ontology "
						+ i2b2OntologyBuilder.getOntologyPath());
				setMessage("Done Ontology Slicing");
			}
		} catch (OWLOntologyCreationException | ClassNotFoundException
				| IOException | SQLException e) {
			e.printStackTrace();
		}

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		appendText(newMessage);

		pcs.firePropertyChange("message", oldMessage, newMessage);
	}

	public void appendText(String text) {
		try {
			StyledDocument doc = messageText.getStyledDocument();
			SimpleAttributeSet plainText = new SimpleAttributeSet();
			StyleConstants.setForeground(plainText, Color.BLACK);
			StyleConstants.setBackground(plainText, Color.WHITE);
			StyleConstants.setBold(plainText, true);
			doc.insertString(doc.getLength(), text, plainText);
			doc.insertString(doc.getLength(), "\n", plainText);
			messageText.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirmationButton) {
			confirmationButton.setEnabled(false);
			setMessage("Finished");
		}

	}

	public TreeSet<PartialPath> getPartialPathTreeSet() {
		return partialPathTreeSet;
	}

	public void setPartialPathTreeSet(TreeSet<PartialPath> partialPathTreeSet) {
		this.partialPathTreeSet = partialPathTreeSet;
	}

	public HashMap<String, PartialPath> getPartialPathMap() {
		return partialPathMap;
	}

	public void setPartialPathMap(HashMap<String, PartialPath> partialPathMap) {
		this.partialPathMap = partialPathMap;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener("message", listener);
	}

	public PatientKnowledgeExtractor getKnowledgeExtractor() {
		return patientKnowledgeExtractor;
	}

	public void setKnowledgeExtractor(
			PatientKnowledgeExtractor knowledgeExtractor) {
		this.patientKnowledgeExtractor = knowledgeExtractor;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;

	}

	public AnnotationTabPanel getAnnotationTabPanel() {
		return annotationTabPanel;
	}

	public void setAnnotationTabPanel(AnnotationTabPanel annotationTabPanel) {
		this.annotationTabPanel = annotationTabPanel;
	}

}
