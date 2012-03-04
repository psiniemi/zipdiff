/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package net.diibadaaba.zipdiff;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.diibadaaba.zipdiff.output.Builder;
import net.diibadaaba.zipdiff.output.BuilderFactory;
import net.diibadaaba.zipdiff.output.SplitZipBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Provides a command line interface to zipdiff
 *
 * @author Sean C. Sullivan
 * @author James Stewart
 * @author Hendrik Brummermann
 * @author Pasi Niemi
 */
public class Main {
	private static final int EXITCODE_ERROR = 2;

	private static final int EXITCODE_DIFF = 1;

	private static final String OPTION_COMPARE_CRC_VALUES = "c";

	private static final String OPTION_COMPARE_TIMESTAMPS = "t";

	private static final String OPTION_IGNORE_CVS_FILES = "i";

	private static final String OPTION_OUTPUT_FILE = "o";

	private static final String OPTION_FILE1 = "1";

	private static final String OPTION_FILE2 = "2";

	private static final String OPTION_SKIP_OUTPUT_PREFIXES = "so";

	private static final String OPTION_SKIP_PREFIX1 = "s1";

	private static final String OPTION_SKIP_PREFIX2 = "s2";

	private static final String OPTION_REGEX = "r";

	private static final String OPTION_EXIT_WITH_ERROR_ON_DIFF = "e";

	private static final String OPTION_VERBOSE = "v";

	private static final String OPTION_PROCESS_EMBEDDED = "p";

	private static final String OPTION_SPLIT = "sp";

	private static final String OPTION_COMPARE_CRC_VALUES_LONG = "comparecrcvalues";

	private static final String OPTION_COMPARE_TIMESTAMPS_LONG = "comparetimestamps";

	private static final String OPTION_IGNORE_CVS_FILES_LONG = "ignorecvsfiles";

	private static final String OPTION_OUTPUT_FILE_LONG = "outputfile";

	private static final String OPTION_FILE1_LONG = "file1";

	private static final String OPTION_FILE2_LONG = "file2";

	private static final String OPTION_SKIP_OUTPUT_PREFIXES_LONG = "skipoutputprefixes";

	private static final String OPTION_SKIP_PREFIX1_LONG = "skipprefixes1";

	private static final String OPTION_SKIP_PREFIX2_LONG = "skipprefixes2";

	private static final String OPTION_REGEX_LONG = "regex";

	private static final String OPTION_EXIT_WITH_ERROR_ON_DIFF_LONG = "exitwitherrorondifference";

	private static final String OPTION_VERBOSE_LONG = "verbose";

	private static final String OPTION_PROCESS_EMBEDDED_LONG = "processembedded";

	private static final String OPTION_SPLIT_LONG = "split";

    private static final Options options;

	// static initializer
	static {
		options = new Options();

		Option compareTS = new Option(OPTION_COMPARE_TIMESTAMPS, OPTION_COMPARE_TIMESTAMPS_LONG, false, "Compare timestamps");
		compareTS.setRequired(false);

		Option compareCRC = new Option(OPTION_COMPARE_CRC_VALUES, OPTION_COMPARE_CRC_VALUES_LONG, false, "Compare CRC values");
		compareCRC.setRequired(false);

		Option file1 = new Option(OPTION_FILE1, OPTION_FILE1_LONG, true, "<filename> first file to compare");
		file1.setRequired(true);

		Option file2 = new Option(OPTION_FILE2, OPTION_FILE2_LONG, true, "<filename> second file to compare");
		file2.setRequired(true);

		Option numberOfOutputPrefixesToSkip = new Option(OPTION_SKIP_OUTPUT_PREFIXES, OPTION_SKIP_OUTPUT_PREFIXES_LONG, true, "<n> number of directory prefix to skip in the output file (if supported by outputter");
		numberOfOutputPrefixesToSkip.setRequired(false);


		Option numberOfPrefixesToSkip1 = new Option(OPTION_SKIP_PREFIX1, OPTION_SKIP_PREFIX1_LONG, true, "<n> number of directory prefix to skip for the first file");
		numberOfPrefixesToSkip1.setRequired(false);

		Option numberOfPrefixesToSkip2 = new Option(OPTION_SKIP_PREFIX2, OPTION_SKIP_PREFIX2_LONG, true, "<n> number of directory prefix to skip for the second file");
		numberOfPrefixesToSkip2.setRequired(false);

		Option outputFileOption = new Option(OPTION_OUTPUT_FILE, OPTION_OUTPUT_FILE_LONG, true, "output filename");
		outputFileOption.setRequired(false);

		Option regex = new Option(OPTION_REGEX, OPTION_REGEX_LONG, true, "regular expression to match files to exclude e.g. (?i)meta-inf.*");
		regex.setRequired(false);

		Option ignoreCVSFilesOption = new Option(OPTION_IGNORE_CVS_FILES, OPTION_IGNORE_CVS_FILES_LONG, false, "ignore CVS files");
		ignoreCVSFilesOption.setRequired(false);

		Option exitWithError = new Option(OPTION_EXIT_WITH_ERROR_ON_DIFF, OPTION_EXIT_WITH_ERROR_ON_DIFF_LONG, false, "if a difference is found then exit with error " + EXITCODE_DIFF);

		Option verboseOption = new Option(OPTION_VERBOSE, OPTION_VERBOSE_LONG, false, "verbose mode");

	    Option processEmbedded = 
	            		new Option(OPTION_PROCESS_EMBEDDED, OPTION_PROCESS_EMBEDDED_LONG, false, "Process embedded zip files");
	    processEmbedded.setRequired(false);

	    Option split = new Option(OPTION_SPLIT, OPTION_SPLIT_LONG, false, "Split ZIP files into shared, file1 and file2 files");
	    split.setRequired(false);
	    
	    options.addOption(compareTS);
		options.addOption(compareCRC);
		options.addOption(file1);
		options.addOption(file2);
		options.addOption(numberOfOutputPrefixesToSkip);
		options.addOption(numberOfPrefixesToSkip1);
		options.addOption(numberOfPrefixesToSkip2);
		options.addOption(regex);
		options.addOption(ignoreCVSFilesOption);
		options.addOption(exitWithError);
		options.addOption(verboseOption);
		options.addOption(outputFileOption);
		options.addOption(processEmbedded);
		options.addOption(split);
	}

	private static void checkFile(java.io.File f) {
		String filename = f.toString();

		if (!f.exists()) {
			System.err.println("'" + filename + "' does not exist");
			System.exit(EXITCODE_ERROR);
		}

		if (!f.canRead()) {
			System.err.println("'" + filename + "' is not readable");
			System.exit(EXITCODE_ERROR);
		}

		if (f.isDirectory()) {
			System.err.println("'" + filename + "' is a directory");
			System.exit(EXITCODE_ERROR);
		}

	}

	private static void writeOutputFile(String filename, int numberOfOutputPrefixesToSkip, Differences d) throws java.io.IOException {
		Builder builder = BuilderFactory.create(filename);
		builder.build(filename, numberOfOutputPrefixesToSkip, d);
	}

	/**
	 *
	 * The command line interface to zipdiff utility
	 *
	 * @param args The command line parameters
	 *
	 */
	public static void main(String[] args) {
		CommandLineParser parser = new GnuParser();

		try {
			CommandLine line = parser.parse(options, args);

			String filename1 = null;
			String filename2 = null;

			filename1 = line.getOptionValue(OPTION_FILE1);
			filename2 = line.getOptionValue(OPTION_FILE2);

			File f1 = new File(filename1);
			File f2 = new File(filename2);

			checkFile(f1);
			checkFile(f2);

			System.out.println("File 1 = " + f1);
			System.out.println("File 2 = " + f2);

			DifferenceCalculator calc = new DifferenceCalculator(f1, f2);

			int numberOfPrefixesToSkip1 = 0;
			if (line.getOptionValue(OPTION_SKIP_PREFIX1) != null) {
				numberOfPrefixesToSkip1 = Integer.parseInt(line.getOptionValue(OPTION_SKIP_PREFIX1));
			}
			int numberOfPrefixesToSkip2 = 0;
			if (line.getOptionValue(OPTION_SKIP_PREFIX2) != null) {
				numberOfPrefixesToSkip2 = Integer.parseInt(line.getOptionValue(OPTION_SKIP_PREFIX2));
			}
			int numberOfOutputPrefixesToSkip = 0;
			if (line.getOptionValue(OPTION_SKIP_OUTPUT_PREFIXES) != null) {
				numberOfOutputPrefixesToSkip = Integer.parseInt(line.getOptionValue(OPTION_SKIP_OUTPUT_PREFIXES));
			}

			calc.setNumberOfPrefixesToSkip1(numberOfPrefixesToSkip1);
			calc.setNumberOfPrefixesToSkip2(numberOfPrefixesToSkip2);

			String regularExpression = null;

			// todo - calc.setFilenamesToIgnore();

			if (line.hasOption(OPTION_COMPARE_CRC_VALUES)) {
				calc.setCompareCRCValues(true);
			} else {
				calc.setCompareCRCValues(false);
			}

			if (line.hasOption(OPTION_IGNORE_CVS_FILES)) {
				calc.setIgnoreCVSFiles(true);
			} else {
				calc.setIgnoreCVSFiles(false);
			}

			if (line.hasOption(OPTION_COMPARE_TIMESTAMPS)) {
				calc.setIgnoreTimestamps(false);
			} else {
				calc.setIgnoreTimestamps(true);
			}

			if (line.hasOption(OPTION_REGEX)) {
				regularExpression = line.getOptionValue(OPTION_REGEX);
				Set<String> regexSet = new HashSet<String>();
				regexSet.add(regularExpression);

				calc.setFilenameRegexToIgnore(regexSet);
			}

			boolean exitWithErrorOnDiff = false;
			if (line.hasOption(OPTION_EXIT_WITH_ERROR_ON_DIFF)) {
				exitWithErrorOnDiff = true;
			}

			if (line.hasOption(OPTION_PROCESS_EMBEDDED)) {
            	calc.setProcessEmbeddedZipFiles(true);
            } else {
            	calc.setProcessEmbeddedZipFiles(false);
            }

			if (line.hasOption(OPTION_SPLIT)) {
				BuilderFactory.setZipBuilder(new SplitZipBuilder());
			}

			Differences d = calc.getDifferences();

			if (line.hasOption(OPTION_OUTPUT_FILE)) {
				String outputFilename = line.getOptionValue(OPTION_OUTPUT_FILE);
				writeOutputFile(outputFilename, numberOfOutputPrefixesToSkip, d);
			}
			
			if (d.hasDifferences()) {
				if (line.hasOption(OPTION_VERBOSE)) {
					System.out.println(d);
					System.out.println(d.getFilename1() + " and " + d.getFilename2() + " are different.");
				}
				if (exitWithErrorOnDiff) {
					System.exit(EXITCODE_DIFF);
				}
			} else {
				System.out.println("No differences found.");
			}
		} catch (ParseException pex) {
			System.err.println(pex.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("zipdiff.Main [options] ", options);
			System.exit(EXITCODE_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(EXITCODE_ERROR);
		}

	}

}
