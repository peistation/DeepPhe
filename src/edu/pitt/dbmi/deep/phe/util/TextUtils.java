package edu.pitt.dbmi.deep.phe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextUtils {
	/**
	 * This method gets a text file (HTML too) from input stream 
	 * from given map
	 * @param InputStream text input
	 * @return String that was produced
	 * @throws IOException if something is wrong
	 * WARNING!!! if you use this to read HTML text and want to put it somewhere
	 * you should delete newlines
	 */
	public static String getText(InputStream in) throws IOException {
		StringBuffer strBuf = new StringBuffer();
		BufferedReader buf = new BufferedReader(new InputStreamReader(in));
		try {
			for (String line = buf.readLine(); line != null; line = buf.readLine()) {
				strBuf.append(line.trim() + "\n");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			buf.close();
		}
		return strBuf.toString();
	}
}
