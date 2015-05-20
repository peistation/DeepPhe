package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class IsValidUMLSUser {
	public static void main(String[] argv) throws Exception {
		if (argv.length == 1 && argv[0].equals("help")) {
			System.out
					.println("Usage: java isValidUMLSUser <LicenseCode> <UserName> <Password>");
			return;
		}

		if (argv.length != 3) {
			System.out.println("Incorrect Arguments!!");
			System.out
					.println("Usage: java isValidUMLSUser <LicenseCode> <UserName> <Password>");
			return;
		}

		String data = URLEncoder.encode("licenseCode", "UTF-8") + "="
				+ URLEncoder.encode(argv[0], "UTF-8");
		data += "&" + URLEncoder.encode("user", "UTF-8") + "="
				+ URLEncoder.encode(argv[1], "UTF-8");
		data += "&" + URLEncoder.encode("password", "UTF-8") + "="
				+ URLEncoder.encode(argv[2], "UTF-8");

		URL url = new URL("https://uts-ws.nlm.nih.gov/restful/isValidUMLSUser");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
		wr.close();
		rd.close();
	}
}