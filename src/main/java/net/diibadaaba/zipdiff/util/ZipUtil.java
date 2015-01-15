package net.diibadaaba.zipdiff.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class ZipUtil {
	public static ZipArchiveEntry copyToNewName(ZipArchiveEntry zae, String newName) {
		ZipArchiveEntry z = new ZipArchiveEntry(newName);
        z.setExtra(zae.getExtra());
        z.setTime(zae.getTime());
        z.setComment(zae.getComment());
		z.setInternalAttributes(zae.getInternalAttributes());
        z.setExternalAttributes(zae.getExternalAttributes());
        z.setExtraFields(zae.getExtraFields(true));
        return z;
	}
}
