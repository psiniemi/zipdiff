/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package net.diibadaaba.zipdiff.output;

import static net.diibadaaba.zipdiff.util.StreamUtil.copyStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;


import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import net.diibadaaba.zipdiff.Differences;
import net.diibadaaba.zipdiff.util.StringUtil;



/**
 * creates a zip file with the new versions of files that have been added or modified
 *
 * @author Hendrik Brummermann, HIS GmbH
 */
public class ZipBuilder extends AbstractBuilder {
	private Differences differences;

	private final Set<String> filenames = new TreeSet<String>();

	/**
	 * builds the output
	 *
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	@Override
	public void build(OutputStream out, Differences d) {
		differences = d;
		try {
			collectAddedFiles();
			collectModifiedFiles();
			copyEntries(out);
		} catch (IOException e) {
			System.err.println("Error while writing zip file: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * collects all the files that have been added in the second zip archive
	 */
	private void collectAddedFiles() {
		for (Entry<String, ZipArchiveEntry> mapEntry :  differences.getAdded().entrySet()) {
			if (mapEntry.getKey().toString().indexOf("!") < 0) {
				filenames.add(((ZipArchiveEntry) mapEntry.getValue()).getName());
			}
		}
	}

	/**
	 * collects all the files that have been added modified in the second zip archive
	 */
	private void collectModifiedFiles() {
		for (Entry<String, ZipArchiveEntry[]> mapEntry : differences.getChanged().entrySet()) {
			if (mapEntry.getKey().toString().indexOf("!") < 0) {
				filenames.add(((ZipArchiveEntry[]) mapEntry.getValue())[1].getName());
			}
		}
	}

	/**
	 * copies the zip entries (with data) from the second archive file to the output file.
	 *
	 * @param out output file
	 * @throws IOException in case of an input/output error
	 */
	private void copyEntries(OutputStream out) throws IOException {
		ZipArchiveOutputStream os = new ZipArchiveOutputStream(out);
		ZipFile zipFile = new ZipFile(differences.getFilename2());
		Iterator<String> itr = filenames.iterator();

		while (itr.hasNext()) {
			String filename = itr.next();
			ZipArchiveEntry zae = zipFile.getEntry(filename);
			
			ZipArchiveEntry z = new ZipArchiveEntry(StringUtil.removeDirectoryPrefix(filename, numberOfOutputPrefixesToSkip));
	        z.setInternalAttributes(zae.getInternalAttributes());
	        z.setExternalAttributes(zae.getExternalAttributes());
	        z.setExtraFields(zae.getExtraFields(true));

			
			os.putArchiveEntry(z);
			if (!z.isDirectory()) {
				InputStream is = zipFile.getInputStream(zae);
				copyStream(is, os);
			}
			os.closeArchiveEntry();
		}

		zipFile.close();
		os.close();
	}
}
