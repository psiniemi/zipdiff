/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package net.diibadaaba.zipdiff.util;

/**
 * String manipulation methods
 *
 * @author Hendrik Brummermann
 */
public class StringUtil {

	/**
	 * removes the specified number of prefix components from a path
	 * @param name file name with path
	 * @param number number of directory prefixed to remove
	 * @return path without prefix
	 */
	public static String removeDirectoryPrefix(String name, int number) {
		int pos = 0;
		for (int i = 0; i < number; i++) {
			pos = name.indexOf("/", pos) + 1;
		}
		if (pos < 0) {
			return null;
		}
		return name.substring(pos);
	}

	/**
	 * Escapes HTML characters to prevent Cross-Site Scripting (XSS).
	 *
	 * @param input string to escape
	 * @return escaped string
	 */
	public static String escapeHtml(String input) {
		if (input == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(input.length() + 16);
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&#39;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
