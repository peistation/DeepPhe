package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class DeepPheSummaryPanel {

	private JPanel panel;
	private JTextField annotatorNameTextField = new JTextField();
	private JTextField annotatorDateTimeField = new JTextField();
	private JTextField leftResultTextField = new JTextField();
	private JTextField rightResultTextField = new JTextField();
	private JTextField foundRightTextField = new JTextField();
	private JTextField foundLeftTextField = new JTextField();
	private JTextField foundBilateralTextField = new JTextField();
	private JTextField foundNonSpecificTextField = new JTextField();
	private JTextField lateralityTextField = new JTextField();

	public DeepPheSummaryPanel() {
	}

	public void inititialize() {
		panel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		panel.setLayout(gridBagLayout);
		Border border = BorderFactory.createTitledBorder("DeepPhe Machine Summary");
		panel.setBorder(border);
		
		// Annotator
		GridBagConstraints labelGbcs = templateGbcForLabels();
		panel.add(new JLabel("Annotator:"), labelGbcs);
		GridBagConstraints textFieldGbcs = templateGbcForTextFields();
		textFieldGbcs.gridwidth = 3;
		panel.add(annotatorNameTextField, textFieldGbcs);
		
		// AnnotationDate
		labelGbcs.gridy++;
		panel.add(new JLabel("Annotation Date:"), labelGbcs);
		textFieldGbcs.gridy++;
		panel.add(annotatorDateTimeField, textFieldGbcs);

		// left result
		labelGbcs.gridy++;
		panel.add(new JLabel("Left Result:"), labelGbcs);
		textFieldGbcs.gridwidth = 1;
		textFieldGbcs.gridy++;
		panel.add(leftResultTextField, textFieldGbcs);
				
		// right result
		labelGbcs.gridx += 2;
		panel.add(new JLabel("Right Result:"), labelGbcs);
		textFieldGbcs.gridx += 2;
		panel.add(rightResultTextField, textFieldGbcs);
		
		// foundLeft
		labelGbcs.gridx = 0;
		labelGbcs.gridy++;
		panel.add(new JLabel("Found Left:"), labelGbcs);
		textFieldGbcs.gridx = 1;
		textFieldGbcs.gridy++;
		panel.add(foundLeftTextField, textFieldGbcs);
		
		// foundRight
		labelGbcs.gridx += 2;
		panel.add(new JLabel("Found Right:"), labelGbcs);
		textFieldGbcs.gridx += 2;
		panel.add(foundRightTextField, textFieldGbcs);
		
		// foundBilateral
		labelGbcs.gridx = 0;
		labelGbcs.gridy++;
		panel.add(new JLabel("Found Bilateral"), labelGbcs);
		
		textFieldGbcs.gridx = 1;
		textFieldGbcs.gridy++;
		panel.add(foundBilateralTextField, textFieldGbcs);
		
		// foundNonSpecific
		labelGbcs.gridx += 2;
		panel.add(new JLabel("Found NonSpecific:"), labelGbcs);
		textFieldGbcs.gridx += 2;
		panel.add(foundNonSpecificTextField, textFieldGbcs);
	
		labelGbcs.gridx = 0;
		labelGbcs.gridy++;
		panel.add(new JLabel("Laterality"), labelGbcs);
		textFieldGbcs.gridx = 1;
		textFieldGbcs.gridy++;
		panel.add(lateralityTextField, textFieldGbcs);	
	}
	

	private GridBagConstraints templateGbcForLabels() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 5;
		gbc.ipady = 5;
		return gbc;
	}

	private GridBagConstraints templateGbcForTextFields() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 5;
		gbc.ipady = 5;
		return gbc;
	}


	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
}
