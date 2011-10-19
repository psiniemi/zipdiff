package zipdiff.output;

import static zipdiff.util.StreamUtil.copyStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import zipdiff.Differences;

public class SplitZipBuilder implements Builder {
	private Set<String> added = new HashSet<String>();

	
	public void build(String filename, int numberOfOutputPrefixesToSkip, Differences d) throws IOException {
		ZipArchiveOutputStream zo = new ZipArchiveOutputStream(new FileOutputStream(filename));
		ZipArchiveOutputStream z1 = new ZipArchiveOutputStream(new FileOutputStream(filename.substring(0, filename.length() -4) + "-1.zip"));
		ZipArchiveOutputStream z2 = new ZipArchiveOutputStream(new FileOutputStream(filename.substring(0, filename.length() -4) + "-2.zip"));
		ZipFile zin1 = new ZipFile(d.getFilename1());

		ZipFile zin2 = new ZipFile(d.getFilename2());
		handleFile(zin1, zo, z1, d);
		handleFile(zin2, zo, z2, d);
		zo.close();
		z1.close();
		z2.close();
		zin1.close();
		zin2.close();
	}

	private void handleFile(ZipFile original, ZipArchiveOutputStream unchanged,
			ZipArchiveOutputStream changed, Differences d) throws IOException {
		@SuppressWarnings("unchecked")
		Enumeration<? extends ZipArchiveEntry> entries = original.getEntries();
		while (entries.hasMoreElements()) {
			ZipArchiveEntry entry =  entries.nextElement();
			if (added.contains(entry.getName())) continue;
			InputStream is = null;
			ZipArchiveOutputStream toWrite;
			if (d.getUnchanged().containsKey(entry.getName())) {
				added.add(entry.getName());
				toWrite = unchanged;
			} else {
				toWrite = changed;
			}
			ZipArchiveEntry newEntry = (ZipArchiveEntry) entry.clone();
			toWrite.putArchiveEntry(newEntry);
			if (!entry.isDirectory()) {
				is = original.getInputStream(entry);
				copyStream(is, toWrite);
			}
			toWrite.closeArchiveEntry();

		}

	}
}
