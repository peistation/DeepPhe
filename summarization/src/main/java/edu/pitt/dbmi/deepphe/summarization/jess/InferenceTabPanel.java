package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import jess.Rete;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.jess.treeview.JessDefFunctionUserObj;
import edu.pitt.dbmi.deepphe.summarization.jess.treeview.JessDefRuleUserObj;
import edu.pitt.dbmi.deepphe.summarization.jess.treeview.JessDefTemplateUserObj;
import edu.pitt.dbmi.deepphe.summarization.jess.treeview.JessTree;

public class InferenceTabPanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	private List<Patient> patients = new ArrayList<>();

	private JessTree jessTree = new JessTree();
	private PatientKnowledgeExtractor knowledgeExtractor;
	private JessTextInputer jessInputer;
	private JessTextOutputer jessOutputer;
	private Rete engine;

	public InferenceTabPanel() {
	}

	public void build() {
		setLayout(new BorderLayout());
		add(jessInputer, BorderLayout.CENTER);
		add(createJessExplorer(), BorderLayout.EAST);
		add(jessOutputer, BorderLayout.SOUTH);
	}

	private Component createJessExplorer() {
		JPanel jessExplorerPanel = new JPanel(new GridLayout(1,1));	
		jessTree.setEngine(engine);
		jessTree.setPatients(patients);
		jessTree.setTreeSelectionListener(this);	
		jessTree.build();
		jessTree.getTree().setPreferredSize(new Dimension(400, 900));
		jessExplorerPanel.add(jessTree.getScrollableTree());
		return jessExplorerPanel;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jessTree
				.getTree().getLastSelectedPathComponent();
		if (selectedNode == null) {
			;
		}
		else if (selectedNode.getUserObject() == null) {
			;
		}
		else if (selectedNode.getUserObject() instanceof JessDefTemplateUserObj) {
			JessDefTemplateUserObj jessDefTemplateUserObj = (JessDefTemplateUserObj) selectedNode.getUserObject();
			System.out.println("Got a template." + jessDefTemplateUserObj.toString());
		}
		else if (selectedNode.getUserObject() instanceof JessDefRuleUserObj) {
			JessDefRuleUserObj jessDefRuleUserObj = (JessDefRuleUserObj) selectedNode.getUserObject();
			System.out.println("Got a rule." + jessDefRuleUserObj.toString());
			jessInputer.appendText(jessDefRuleUserObj.getContent());
		}
		else if (selectedNode.getUserObject() instanceof JessDefFunctionUserObj) {
			JessDefFunctionUserObj jessDefFunctionUserObj = (JessDefFunctionUserObj) selectedNode.getUserObject();
			System.out.println("Got a function." + jessDefFunctionUserObj.toString());
			jessInputer.appendText(jessDefFunctionUserObj.getContent());
		}
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public PatientKnowledgeExtractor getKnowledgeExtractor() {
		return knowledgeExtractor;
	}

	public void setKnowledgeExtractor(PatientKnowledgeExtractor knowledgeExtractor) {
		this.knowledgeExtractor = knowledgeExtractor;
	}

	public JessTextInputer getJessInputer() {
		return jessInputer;
	}

	public void setJessInputer(JessTextInputer jessInputer) {
		this.jessInputer = jessInputer;
	}

	public JessTextOutputer getJessOutputer() {
		return jessOutputer;
	}

	public void setJessOutputer(JessTextOutputer jessOutputer) {
		this.jessOutputer = jessOutputer;
	}

	public Rete getEngine() {
		return engine;
	}

	public void setEngine(Rete engine) {
		this.engine = engine;
	}

}
