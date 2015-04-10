package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang.exception.ExceptionUtils;

public class JessTextOutputer extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JScrollPane paneScrollPane;
	private JTextPane jessOutputTextPane;

	private JPopupMenu popup = new JPopupMenu();

	private File saveTargetFile;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JessTextOutputer outputer = new JessTextOutputer();
		frame.getContentPane().add(outputer);
		frame.setSize(800, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public JessTextOutputer() {

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
		jessOutputTextPane = new JTextPane();
		PopupListener pl = new PopupListener();
		jessOutputTextPane.addMouseListener(pl);
		paneScrollPane = new JScrollPane(jessOutputTextPane);
		add(paneScrollPane, gbc);

		buildPopUpMenu();

		TitledBorder border = BorderFactory.createTitledBorder("Jess Output");
		setBorder(border);
		
		setPreferredSize(new Dimension(1200, 300));
	}

	private void buildPopUpMenu() {
		popup = new JPopupMenu();
		JMenuItem m = new JMenuItem("Clear");
		m.addActionListener(this);
		popup.add(m);
		jessOutputTextPane.addMouseListener(new PopupListener());
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
				popup.show(getJessOutputTextPane(), e.getX(), e.getY());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Clear")) {
			jessOutputTextPane.setText("");
		}
	}
	
	public void displayException(Exception x) {
		appendError(ExceptionUtils.getStackTrace(x));
	}


	public void appendText(String text) {
		try {
			StyledDocument doc = jessOutputTextPane.getStyledDocument();
			SimpleAttributeSet plainText = new SimpleAttributeSet();
			StyleConstants.setForeground(plainText, Color.BLACK);
			StyleConstants.setBackground(plainText, Color.WHITE);
			StyleConstants.setBold(plainText, true);
			doc.insertString(doc.getLength(), text, plainText);
			doc.insertString(doc.getLength(), "\n", plainText);
			jessOutputTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void appendError(String text) {
		try {
			StyledDocument doc = jessOutputTextPane.getStyledDocument();
			SimpleAttributeSet plainText = new SimpleAttributeSet();
			StyleConstants.setForeground(plainText, Color.RED);
			StyleConstants.setBackground(plainText, Color.WHITE);
			StyleConstants.setBold(plainText, false);
			doc.insertString(doc.getLength(), text, plainText);
			doc.insertString(doc.getLength(), "\n", plainText);
			jessOutputTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public JTextPane getJessOutputTextPane() {
		return jessOutputTextPane;
	}

	public File getSaveTargetFile() {
		return saveTargetFile;
	}

	public void setSaveTargetFile(File saveTargetFile) {
		this.saveTargetFile = saveTargetFile;
	}

}
