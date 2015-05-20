package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.commons.lang.StringUtils;

import edu.pitt.dbmi.deepphe.summarization.jess.kb.Encounter;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

/**
 * JTree basic tutorial and example
 * 
 * @author wwww.codejava.net
 */

public class SummarizableTree {

	private JTree tree;
	private List<Patient> patients;
	private TreeSelectionListener treeSelectionListener;

	public SummarizableTree() {
	}

	public void build() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		Iterator<Patient> patientIterator = patients.iterator();
		while (patientIterator.hasNext()) {
			Patient kbPatient = patientIterator.next();
			String nodeName = "Patient"
					+ StringUtils.leftPad(kbPatient.getId() + "", 4, "0");
			DefaultMutableTreeNode patientNode = new DefaultMutableTreeNode(
					nodeName);
			patientNode.setUserObject(kbPatient);
			root.add(patientNode);
			Iterator<Encounter> encounterIterator = kbPatient.getEncounters()
					.iterator();
			while (encounterIterator.hasNext()) {
				Encounter kbEncounter = encounterIterator.next();
				nodeName = "Encounter"
						+ StringUtils.leftPad(kbPatient.getId() + "", 4, "0");
				DefaultMutableTreeNode encounterNode = new DefaultMutableTreeNode(
						nodeName);
				encounterNode.setUserObject(kbEncounter);
				patientNode.add(encounterNode);
			}
		}
		tree = new JTree(root);

		EncounterTreeCellRenderer renderer = new EncounterTreeCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);

		tree.getSelectionModel()
				.addTreeSelectionListener(treeSelectionListener);
	}
	
	public JTree getTree() {
		return tree;
	}

	public JScrollPane getScrollableTree() {
		return new JScrollPane(tree);
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public TreeSelectionListener getTreeSelectionListener() {
		return treeSelectionListener;
	}

	public void setTreeSelectionListener(
			TreeSelectionListener treeSelectionListener) {
		this.treeSelectionListener = treeSelectionListener;
	}

	class EncounterTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		private ImageIcon malePatientIcon = new ImageIcon(
				SummarizableTree.class.getResource("/images/16/280-user_0.gif"));
		private ImageIcon femalePatientIcon = new ImageIcon(
				SummarizableTree.class
						.getResource("/images/16/289-user_woman.gif"));
		private ImageIcon encounterIcon = new ImageIcon(
				SummarizableTree.class.getResource("/images/16/002.png"));

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean exp, boolean leaf, int row,
				boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() instanceof Patient) {
				Patient patient = (Patient) node.getUserObject();
				if (patient.getGender().equals("Male")) {
					setOpenIcon(malePatientIcon);
					setClosedIcon(malePatientIcon);
					setLeafIcon(malePatientIcon);
				} else {
					setOpenIcon(femalePatientIcon);
					setClosedIcon(femalePatientIcon);
					setLeafIcon(femalePatientIcon);
				}
			} else if (node.getUserObject() instanceof Encounter) {
				setOpenIcon(encounterIcon);
				setClosedIcon(encounterIcon);
				setLeafIcon(encounterIcon);
			}
			return super.getTreeCellRendererComponent(tree, value, sel, exp, leaf,
					row, hasFocus);
		}
	}
}