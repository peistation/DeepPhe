package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class JessTextInputer extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private PatientKnowledgeExtractor tmnExtractor;
	private JessTextOutputer jessTextOutputer;

	private JScrollPane inputScrollPane;
	private JTextPane inputTextPane;

	private JPopupMenu popup = new JPopupMenu();

	private File readSourceFile;
	private File saveTargetFile;

	private JFileChooser fc;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JessTextInputer inputer = new JessTextInputer();
		frame.getContentPane().add(inputer);
		frame.setSize(800, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public JessTextInputer() {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 5;
		gbc.ipady = 5;
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		inputTextPane = new JTextPane();
		PopupListener pl = new PopupListener();
		inputTextPane.addMouseListener(pl);
		inputScrollPane = new JScrollPane(inputTextPane);
		add(inputScrollPane, gbc);

		buildPopUpMenu();

		TitledBorder border = BorderFactory.createTitledBorder("Jess Input");
		setBorder(border);
	}

	private void buildPopUpMenu() {
		popup = new JPopupMenu();
		JMenuItem m = new JMenuItem("Jess Eval");
		m.addActionListener(this);
		popup.add(m);
		m = new JMenuItem("Jess Run");
		m.addActionListener(this);
		popup.add(m);
		m = new JMenuItem("Jess Reset");
		m.addActionListener(this);
		popup.add(m);
		m = new JMenuItem("Jess Clear");
		m.addActionListener(this);
		popup.add(m);
		popup.addSeparator();
		m = new JMenuItem("Clear Text");
		m.addActionListener(this);
		popup.add(m);
		popup.addSeparator();
		m = new JMenuItem("Read Clips");
		m.addActionListener(this);
		popup.add(m);
		m = new JMenuItem("Execute Clips");
		m.addActionListener(this);
		popup.add(m);
		popup.addSeparator();
		m = new JMenuItem("Save Clips");
		m.addActionListener(this);
		popup.add(m);
		m = new JMenuItem("Save Clips As");
		m.addActionListener(this);
		popup.add(m);
		inputTextPane.addMouseListener(new PopupListener());
	}

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger())
				popup.show(getInputTextPane(), e.getX(), e.getY());
		}
	}
	
	public void appendText(String content) {
		inputTextPane.setText(inputTextPane.getText() + "\n" + content);	
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Jess Eval")) {
			processEvalJess();
		} else if (e.getActionCommand().equals("Jess Run")) {
			processRunJess();
		} else if (e.getActionCommand().equals("Jess Reset")) {
			processResetJess();
		} else if (e.getActionCommand().equals("Jess Clear")) {
			processClearJess();
		} else if (e.getActionCommand().equals("Clear Text")) {
			inputTextPane.setText("");
		} else if (e.getActionCommand().equals("Read Clips")) {
			processRead();
		} else if (e.getActionCommand().equals("Execute Clips")) {
			processExecute();
		} else if (e.getActionCommand().equals("Save Clips")) {
			processSave();
		} else if (e.getActionCommand().equals("Save Clips As")) {
			processSaveAs();
		}
	}

	private void processExecute() {
		try {
			if (readSourceFile == null) {
				establishReadSourceFile();
			}
			String textRead = FileUtils.readFileToString(readSourceFile);
			inputTextPane.setText(inputTextPane.getText() + "\n" + textRead);
			tmnExtractor.executeJess(textRead);
		} catch (IOException e) {
			jessTextOutputer.displayException(e);
		}
	}

	private void processRead() {
		try {
			if (getReadSourceFile() == null) {
				establishReadSourceFile();
			}
			String textRead = FileUtils.readFileToString(readSourceFile);
			inputTextPane.setText(inputTextPane.getText() + "\n" + textRead);	
			setReadSourceFile(null);
		} catch (IOException e) {
			jessTextOutputer.displayException(e);
		}
		
	}

	private void establishReadSourceFile() {
		if (fc == null) {
			fc = new JFileChooser();
			File workingDirectory = new File(".");
			System.out.println("The working directory is " + workingDirectory);
			File fileSearchDirectory = new File(workingDirectory, "src\\main\\jess");
			fc.setCurrentDirectory(fileSearchDirectory);
			FileFilter filter = new FileNameExtensionFilter("Clips files",
					"clp");
			fc.addChoosableFileFilter(filter);
		}
		int returnVal = fc.showOpenDialog(JessTextInputer.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			setReadSourceFile(fc.getSelectedFile());
		} else {
			jessTextOutputer.appendError("\nRead command cancelled by user.\n");
		}
		
	}
	
	private File getReadSourceFile() {
		return readSourceFile;
	}

	private void setReadSourceFile(File readSourceFile) {
		this.readSourceFile = readSourceFile;
	}

	private void processEvalJess() {
		if (!StringUtils.isEmpty(inputTextPane.getSelectedText())) {
			tmnExtractor.executeJess(inputTextPane.getSelectedText());
		}
	}

	private void processClearJess() {
		tmnExtractor.executeJess("(clear)");
	}

	private void processRunJess() {
		tmnExtractor.executeJess("(run)");
	}

	private void processResetJess() {
		tmnExtractor.executeJess("(reset)");
	}

	private void processSave() {
		try {
			if (saveTargetFile == null) {
				establishSaveTargetFile();
			}
			String textToSave = inputTextPane.getSelectedText();
			if (textToSave == null) {
				textToSave = inputTextPane.getText();
			}
			FileUtils.write(saveTargetFile, textToSave);
		} catch (IOException e) {
			jessTextOutputer.displayException(e);
		}

	}

	private void processSaveAs() {
		try {
			establishSaveTargetFile();
			String textToSave = inputTextPane.getSelectedText();
			if (textToSave == null) {
				textToSave = inputTextPane.getText();
			}
			FileUtils.write(saveTargetFile, textToSave);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void establishSaveTargetFile() {
		if (fc == null) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(".\\src\\main\\jess"));
			FileFilter filter = new FileNameExtensionFilter("Clips files",
					"clp");
			fc.addChoosableFileFilter(filter);
		}
		int returnVal = fc.showSaveDialog(JessTextInputer.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			setSaveTargetFile(fc.getSelectedFile());
		} else {
			jessTextOutputer.appendError("\nSave command cancelled by user.\n");
		}
	}

	public String geSelectedText() {
		return getInputTextPane().getSelectedText();
	}

	public JTextPane getInputTextPane() {
		return inputTextPane;
	}

	public File getSaveTargetFile() {
		return saveTargetFile;
	}

	public void setSaveTargetFile(File saveTargetFile) {
		this.saveTargetFile = saveTargetFile;
	}

	public void setKnowledgeExtractor(PatientKnowledgeExtractor tmnExtractor) {
		this.tmnExtractor = tmnExtractor;
	}

	public void setJessTextOutputer(JessTextOutputer jessTextOutputer) {
		this.jessTextOutputer = jessTextOutputer;
	}

}
