/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package net.diibadaaba.zipdiff.output;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import junit.framework.TestCase;
import net.diibadaaba.zipdiff.Differences;
import net.diibadaaba.zipdiff.util.StringUtil;

/**
 * Tests for HtmlBuilder and StringUtil.escapeHtml
 */
public class HtmlBuilderTest extends TestCase {

	public void testEscapeHtml() {
		assertNull(StringUtil.escapeHtml(null));
		assertEquals("hello", StringUtil.escapeHtml("hello"));
		assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;", StringUtil.escapeHtml("<script>alert('XSS')</script>"));
		assertEquals("a &amp; b &quot;c&quot;", StringUtil.escapeHtml("a & b \"c\""));
	}

	public void testHtmlBuilderEscaping() {
		Differences diffs = new Differences();
		diffs.setFilename1("file1_<script>.zip");
		diffs.setFilename2("file2_\"quote\".zip");

		String xssEntry = "<img src=x onerror=alert('xss')>";
		ZipArchiveEntry entry = new ZipArchiveEntry(xssEntry);
		diffs.getAdded().put(xssEntry, entry);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HtmlBuilder builder = new HtmlBuilder();
		builder.build(baos, diffs);

		String output = baos.toString();

		// Verify filenames are escaped
		assertTrue(output.contains("file1_&lt;script&gt;.zip"));
		assertTrue(output.contains("file2_&quot;quote&quot;.zip"));

		// Verify entry key is escaped and unescaped script tag is not present
		assertFalse(output.contains("<img src=x onerror=alert('xss')>"));
		assertTrue(output.contains("&lt;img src=x onerror=alert(&#39;xss&#39;)&gt;"));
	}
}
