package net.diibadaaba.zipdiff.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility to copy streams and make sure all resources get closed
 * 
 * @author Pasi Niemi
 */
public class StreamUtil {
	/**
	 * copies data from an input stream to an output stream
	 *
	 * @param input InputStream
	 * @param output OutputStream
	 * @throws IOException in case of an input/output error
	 */
	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte buffer[] = new byte[4096];
		try {
			int count = input.read(buffer);
			while (count > -1) {
				output.write(buffer, 0, count);
				count = input.read(buffer);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				output.flush();
			} catch (IOException e0) {
				throw e0;
			} finally {
				if (input != null) input.close();
			}
		}
	}

}
