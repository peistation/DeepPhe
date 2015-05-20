package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public class AnnotationTabPanel extends JPanel implements TreeSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel summarizableChooserPanel;
	private JPanel summarizableViewerPanel;
	
	private JTextPane summarizableTextPane = new JTextPane();
	
	private PatientListReader patientListReader;
	private List<Patient> patients;
	
	private SummarizableTree summarizableTree = new SummarizableTree();
	
	public AnnotationTabPanel() {
	}
	
	public void build() {
		String file  = SummarizationGui.PROJECT_LOCATION+File.separator+"summarization/src/main/resources/summarization/raw";//"src\\main\\resources\\summarization\\raw";
		final File encountersDirectory = new File(file);
		patientListReader.setInputDirectoryPath(encountersDirectory.getAbsolutePath());
		patientListReader.setPatients(patients);
		patientListReader.execute();
		
		setLayout(new BorderLayout());
		summarizableChooserPanel = createReportExplorer();
		summarizableViewerPanel = createReportViewerPanel();
		add(summarizableChooserPanel, BorderLayout.WEST);
		add(summarizableViewerPanel, BorderLayout.CENTER);	
	}
	
	public void reBuild() {
		summarizableChooserPanel.remove(0);
		summarizableTree = new SummarizableTree();
		summarizableTree.setPatients(patients);
		summarizableTree.setTreeSelectionListener(this);
		summarizableTree.build();
		summarizableChooserPanel.add(summarizableTree.getScrollableTree());
		summarizableTextPane.setText("");
		
	}
	
	private JPanel createReportExplorer() {
		summarizableChooserPanel = new JPanel(new GridLayout(1,1));
		
		Border border = BorderFactory.createTitledBorder("Summarizables");
		summarizableChooserPanel.setBorder(border);
		summarizableChooserPanel.setPreferredSize(new Dimension(200,400));
		
		summarizableTree.setPatients(patients);
		summarizableTree.setTreeSelectionListener(this);
		summarizableTree.build();
		summarizableChooserPanel.add(summarizableTree.getScrollableTree());
		
		return summarizableChooserPanel;

	}

	private JPanel createReportViewerPanel() {
		summarizableViewerPanel = new JPanel();
		Border border = BorderFactory.createTitledBorder("Extracted Information");
		summarizableViewerPanel.setBorder(border);
		summarizableViewerPanel.setLayout(new GridLayout(1,1));
		summarizableViewerPanel.add(new JScrollPane(summarizableTextPane));
		return summarizableViewerPanel;
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) summarizableTree.getTree().getLastSelectedPathComponent();
		if (selectedNode == null) {
			;
		}
		else if (selectedNode.getUserObject() == null) {
			;
		}
		else if (selectedNode.getUserObject() instanceof Patient) {
			Patient patient = (Patient) selectedNode.getUserObject();
			summarizableTextPane.setText(patient.fetchInfo());
			summarizableTextPane.setCaretPosition(0);
		}
		else if (selectedNode.getUserObject() instanceof Encounter) {
			Encounter encounter = (Encounter) selectedNode.getUserObject();
			summarizableTextPane.setText(encounter.fetchInfo());
			summarizableTextPane.setCaretPosition(0);
		}
	}

	public SummarizableTree getEncounterTree() {
		return summarizableTree;
	}

	public void setEncounterTree(SummarizableTree encounterTree) {
		this.summarizableTree = encounterTree;
	}

	public PatientListReader getPatientListReader() {
		return patientListReader;
	}

	public void setPatientListReader(PatientListReader patientListReader) {
		this.patientListReader = patientListReader;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}



}
