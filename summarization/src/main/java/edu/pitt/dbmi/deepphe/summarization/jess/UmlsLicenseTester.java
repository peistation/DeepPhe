package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * UIMA annotator that identified entities based on lookup.
 * 
 * @author Mayo Clinic
 */
public class UmlsLicenseTester {
	private String UMLSAddr = "https://uts-ws.nlm.nih.gov/restful/isValidUMLSUser";
	private String UMLSVendor = "NLM-6515182895";
	private String UMLSUser = "mitchellkj";
	private String UMLSPW = "JudUmls9908";

	public static void main(String[] args) {
		UmlsLicenseTester tester = new UmlsLicenseTester();
		tester.execute();
	}

	public void execute() {
		try {
			boolean isValidUser = isValidUMLSUser(UMLSAddr, UMLSVendor,
					UMLSUser, UMLSPW);
			String suffix = " is a valid user.";
			if (!isValidUser) {
				suffix = " is NOT a valid user.";
			}
			System.out.println(UMLSUser + suffix);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isValidUMLSUser(String umlsaddr, String vendor,
			String username, String password) throws Exception {
		String data = URLEncoder.encode("licenseCode", "UTF-8") + "="
				+ URLEncoder.encode(vendor, "UTF-8");
		data += "&" + URLEncoder.encode("user", "UTF-8") + "="
				+ URLEncoder.encode(username, "UTF-8");
		data += "&" + URLEncoder.encode("password", "UTF-8") + "="
				+ URLEncoder.encode(password, "UTF-8");
		URL url = new URL(umlsaddr);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		boolean result = false;
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			if (line != null && line.trim().length() > 0) {
				System.out.println(line);
				result = line.trim().equalsIgnoreCase("true");
			}
		}
		wr.close();
		rd.close();
		return result;
	}
}