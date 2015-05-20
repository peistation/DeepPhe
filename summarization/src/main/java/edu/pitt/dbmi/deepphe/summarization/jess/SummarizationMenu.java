package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class SummarizationMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileMenuSummarizeAllPatients = new JMenuItem("Summarize All Patients");
	private JMenuItem fileMenuKbLoadSinglePatient = new JMenuItem("Load Single Patient To KB");
	private JMenuItem fileMenuEncounterExtract = new JMenuItem("Extract Encounters");
	private JMenuItem fileMenuExitItem = new JMenuItem("Exit");
	private JMenu jessMenu = new JMenu("Jess");
	private JMenu jessContollerMenu = new JMenu("Controls");
	private JMenuItem jessControllerMenuResetItem = new JMenuItem("Reset");
	private JMenuItem jessControllerMenuEvalItem = new JMenuItem("Eval");
	private JMenuItem jessControllerMenuRunItem = new JMenuItem("Run");
	private JMenu jessDisplayMenu = new JMenu("Display");
	private JMenuItem displayMenuDefTemplatesItem = new JMenuItem("Templates");
	private JMenuItem displayMenuFactsItem = new JMenuItem("Facts");
	private JMenu i2b2Menu = new JMenu("I2B2");
	private JMenu i2b2OntologyMenu = new JMenu("Ontology");
	private JMenuItem i2b2OntologyCleanItem = new JMenuItem("Ontology Clean");
	private JMenu i2b2PatientDataMenu = new JMenu("Patient Data");
	private JMenuItem i2b2PatientCleanItem = new JMenuItem("Patient Clean");
	
	private ActionListener actionListener;

	public SummarizationMenu() {
		add(fileMenu);
		add(jessMenu);
		add(i2b2Menu);
	
		fileMenu.add(fileMenuSummarizeAllPatients);
		fileMenu.add(fileMenuKbLoadSinglePatient);
		fileMenu.add(fileMenuEncounterExtract);
		fileMenu.add(fileMenuExitItem);
		
		jessMenu.add(jessContollerMenu);
		jessContollerMenu.add(jessControllerMenuResetItem);
		jessContollerMenu.add(jessControllerMenuEvalItem);
		jessContollerMenu.add(jessControllerMenuRunItem);
		jessMenu.addSeparator();
		jessMenu.add(jessDisplayMenu);
		jessDisplayMenu.add(displayMenuDefTemplatesItem);
		jessDisplayMenu.add(displayMenuFactsItem);

		i2b2Menu.add(i2b2OntologyMenu);
		i2b2Menu.addSeparator();
		i2b2Menu.add(i2b2PatientDataMenu);
		i2b2OntologyMenu.add(i2b2OntologyCleanItem);
		i2b2PatientDataMenu.add(i2b2PatientCleanItem);
	}
	
	public void injectActionListener() {
		fileMenuSummarizeAllPatients.addActionListener(actionListener);
		fileMenuKbLoadSinglePatient.addActionListener(actionListener);
		fileMenuEncounterExtract.addActionListener(actionListener);
		fileMenuExitItem.addActionListener(actionListener);
		jessControllerMenuResetItem.addActionListener(actionListener);
		jessControllerMenuEvalItem.addActionListener(actionListener);
		jessControllerMenuRunItem.addActionListener(actionListener);
		displayMenuDefTemplatesItem.addActionListener(actionListener);
		displayMenuFactsItem.addActionListener(actionListener);
		i2b2OntologyCleanItem.addActionListener(actionListener);
		i2b2PatientCleanItem.addActionListener(actionListener);
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
	
	

}
