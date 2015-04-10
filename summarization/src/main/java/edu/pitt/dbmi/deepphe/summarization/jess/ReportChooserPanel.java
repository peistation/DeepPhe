package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ReportChooserPanel {
	

	private SummarizationGui gui;
	private JPanel panel;
	private JList<ReportWidget> reportList;

	public void initialize() {
		reportList = new JList<ReportWidget>();
		ReportChooserListModel reportChooserListModel = new ReportChooserListModel();
		reportChooserListModel.initialize();
		reportList.setModel(reportChooserListModel);
		reportList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		reportList.setVisibleRowCount(-1);
		reportList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gui.loadReportWiget(reportList.getSelectedValue());
			}
		});
		JScrollPane listScroller = new JScrollPane(reportList);
		listScroller.setPreferredSize(new Dimension(300, 100));
		listScroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Lay out the buttons from left to right.
//		JPanel buttonPane = new JPanel();
//		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
//		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
//		getPanel().add(buttonPane, BorderLayout.PAGE_START);
		getPanel().add(listPane, BorderLayout.CENTER);

	}

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public SummarizationGui getGui() {
		return gui;
	}

	public void setGui(SummarizationGui gui) {
		this.gui = gui;
	}

}
