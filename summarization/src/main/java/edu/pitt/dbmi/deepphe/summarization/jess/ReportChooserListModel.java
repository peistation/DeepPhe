package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import edu.pitt.dbmi.deepphe.summarization.pojos.Report;

public class ReportChooserListModel implements ListModel<ReportWidget> {	
	
	private final ArrayList<ReportWidget> reports = new ArrayList<ReportWidget>();
	private final ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

	public ReportChooserListModel() {
		;
	}
	
	public void initialize() {
		final Collection<Report> dbReports = pullReportsFromDatabase();
		for (Report dbReport : dbReports) {
			ReportWidget reportWidget = new ReportWidget();
			reportWidget.setReport(dbReport);
			reports.add(reportWidget);
		}
	}
	
	private Collection<Report> pullReportsFromDatabase() {
		final ArrayList<Report> result = new ArrayList<Report>();
		File workingDirectory = new File(".");
		System.out.println("We are in " + workingDirectory.getAbsolutePath());
		File reportsDirectory = new File("summarization\\src\\main\\resources\\summarization\\raw");
		File[] rawFiles = reportsDirectory.listFiles();
		for (File rawFile : rawFiles) {
			Report r = new Report();
			r.setPath(rawFile.getAbsolutePath());
			result.add(r);
		}
		return result;
	}
	
	

	public int getSize() {
		return reports.size();
	}
	
	public ReportWidget getElementAt(int index) {
		return reports.get(index);
	}


	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
		
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}


}
