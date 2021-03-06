package org.xmlcml.norma;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/** tests issues on Github
 * 
 * @author pm286
 *
 */
public class IssueTest {

	/**
	 * #3
if --xsl <code> - where code = 'bmc2html', etc fails to resolve the error is not trapped. This causes serious downstream confusion (suggesting there is no input file)
	 */
	@Test
	public void testMissingStyleSheet() throws Exception {
		File targetFile = new File("target/bmc/1471");
		FileUtils.copyDirectory(new File(NormaFixtures.TEST_BMC_DIR, "1471-2148-14-70"), targetFile);
		String args = "-q "+targetFile+" -i fulltext.xml -o fulltext.html --transform bmc2html";
		// OK
		NormaArgProcessor argProcessor = new NormaArgProcessor(args);
		argProcessor.runAndOutput();
		// bad stylesheet
		FileUtils.copyDirectory(new File(NormaFixtures.TEST_BMC_DIR, "1471-2148-14-70"), targetFile);
		args = "-q "+targetFile+" -i fulltext.xml -o fulltext.html --transform bmc2htmlbad";
		// OK
		argProcessor = new NormaArgProcessor(args);
		argProcessor.runAndOutput();
	}
}
