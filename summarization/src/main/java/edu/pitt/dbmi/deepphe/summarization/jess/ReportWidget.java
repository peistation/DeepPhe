package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import edu.pitt.dbmi.deepphe.summarization.pojos.Report;

public class ReportWidget {
	
	private Report report;
	
	public ReportWidget() {
		;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
	
	public String toString() {
		return StringUtils.substringAfterLast(report.getPath(),File.separator);
	}
		

}
