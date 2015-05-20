package edu.pitt.dbmi.deepphe.summarization.jess.treeview;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import jess.Rete;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;

public class JessTree {

	private JTree tree;
	private List<Patient> patients;
	private TreeSelectionListener treeSelectionListener;

	private Rete engine;

	public JessTree() {
	}

	@SuppressWarnings("unchecked")
	public void build() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

		// deftemplates
		DefaultMutableTreeNode deftemplatesFolder = new DefaultMutableTreeNode(
				new JessDefTemplatesUserObj());
		root.add(deftemplatesFolder);
		Iterator<jess.Deftemplate> defTemplatesIterator = engine
				.listDeftemplates();
		while (defTemplatesIterator.hasNext()) {
			jess.Deftemplate template = (jess.Deftemplate) defTemplatesIterator
					.next();
			JessDefTemplateUserObj userObj = new JessDefTemplateUserObj();
			userObj.setTemplate(template);
			DefaultMutableTreeNode defTemplateNode = new DefaultMutableTreeNode(
					userObj);
			deftemplatesFolder.add(defTemplateNode);
		}

		// defrules
		DefaultMutableTreeNode defrulesFolder = new DefaultMutableTreeNode(
				new JessDefRulesUserObj());
		root.add(defrulesFolder);
		Iterator<jess.Defrule> defRulesIterator = engine.listDefrules();
		while (defRulesIterator.hasNext()) {
			jess.Defrule rule = (jess.Defrule) defRulesIterator.next();
			JessDefRuleUserObj userObj = new JessDefRuleUserObj();
			userObj.setRule(rule);
			DefaultMutableTreeNode defRuleNode = new DefaultMutableTreeNode(
					userObj);
			defrulesFolder.add(defRuleNode);
		}

		tree = new JTree(root);

		JessTreeCellRenderer renderer = new JessTreeCellRenderer();
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

	public Rete getEngine() {
		return engine;
	}

	public void setEngine(Rete engine) {
		this.engine = engine;
	}

	class JessTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		private ImageIcon deftemplateIcon = new ImageIcon(
				JessTree.class.getResource("/jessimages/deftemplate.gif"));
		private ImageIcon defruleIcon = new ImageIcon(
				JessTree.class.getResource("/jessimages/defrule.gif"));
		private ImageIcon deffunctionIcon = new ImageIcon(
				JessTree.class.getResource("/jessimages/deffunction.gif"));

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean exp, boolean leaf, int row,
				boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			boolean isDefTemplate = node.getUserObject() instanceof JessDefTemplatesUserObj;
			isDefTemplate = isDefTemplate
					|| node.getUserObject() instanceof JessDefTemplateUserObj;
			boolean isDefRule = node.getUserObject() instanceof JessDefRulesUserObj;
			isDefRule = isDefRule
					|| node.getUserObject() instanceof JessDefRuleUserObj;
			boolean isDefFunction = node.getUserObject() instanceof JessDefFunctionsUserObj;
			isDefFunction = isDefFunction
					|| node.getUserObject() instanceof JessDefFunctionUserObj;

			if (isDefTemplate) {
				setOpenIcon(deftemplateIcon);
				setClosedIcon(deftemplateIcon);
				setLeafIcon(deftemplateIcon);
			} else if (isDefRule) {
				setOpenIcon(defruleIcon);
				setClosedIcon(defruleIcon);
				setLeafIcon(defruleIcon);
			} else if (isDefFunction){
				setOpenIcon(deffunctionIcon);
				setClosedIcon(deffunctionIcon);
				setLeafIcon(deffunctionIcon);
			}
			return super.getTreeCellRendererComponent(tree, value, sel, exp,
					leaf, row, hasFocus);
		}
	}
}