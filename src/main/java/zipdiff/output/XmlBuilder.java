/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;

import zipdiff.Differences;

/**
 *
 * Generates xml output for a Differences instance
 *
 * @author Sean C. Sullivan
 *
 */
public class XmlBuilder extends AbstractBuilder {

	/**
	 * builds the output
	 *
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	@Override
	public void build(OutputStream out, Differences d) {
		PrintWriter pw = new PrintWriter(out);

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.print("<zipdiff filename1=\"");

		String filename1 = d.getFilename1();

		if (filename1 == null) {
			filename1 = "filename1.zip";
		}
		pw.print(filename1);
		pw.print("\" filename2=\"");

		String filename2 = d.getFilename2();

		if (filename2 == null) {
			filename2 = "filename2.zip";
		}
		pw.print(filename2);
		pw.println("\">");

		pw.println("<differences>");
		writeAdded(pw, d.getAdded().keySet());
		writeRemoved(pw, d.getRemoved().keySet());
		writeChanged(pw, d.getChanged().keySet());
		pw.println("</differences>");
		pw.println("</zipdiff>");

		pw.flush();
	}

	/**
	 * writes the list of added files
	 *
	 * @param pw    write to write to
	 * @param added set of added files
	 */
	protected void writeAdded(PrintWriter pw, Set<String> added) {
		for (String key : added) {
			pw.print("<added>");
			pw.print(key);
			pw.println("</added>");
		}

	}

	/**
	 * writes the list of removed files
	 *
	 * @param pw    write to write to
	 * @param removed set of removed files
	 */
	protected void writeRemoved(PrintWriter pw, Set<String> removed) {
		for (String key : removed) {
			pw.print("<removed>");
			pw.print(key);
			pw.println("</removed>");
		}
	}

	/**
	 * writes the list of modified files
	 *
	 * @param pw    write to write to
	 * @param changed set of modified files
	 */
	protected void writeChanged(PrintWriter pw, Set<String> changed) {
		for (String key : changed) {
			pw.print("<changed>");
			pw.print(key);
			pw.println("</changed>");
		}
	}

}
