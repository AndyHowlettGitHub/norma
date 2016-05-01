package org.xmlcml.norma.pubstyle.tf;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.norma.NormaArgProcessor;

import junit.framework.Assert;

public class TFTest {
	private static final Logger LOG = Logger.getLogger(TFTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testHtml2Scholarly() {
		File targetDir = new File("target/tutorial/tf");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/norma/pubstyle/tf/TandF_OA_Test"), targetDir);
		String args = "--project "+targetDir+" -i fulltext.html -o scholarly.html --transform jsoup";
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args); 
		argProcessor.runAndOutput(); 
	}
	
	@Test
	public void testHtml2Scholarly2Step() {
		File targetDir = new File("target/tutorial/tf");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/xmlcml/norma/pubstyle/tf/TandF_OA_Test"), targetDir);
		String args = "--project "+targetDir+" -i fulltext.html -o fulltext.xhtml --html jsoup";
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args); 
		argProcessor.runAndOutput(); 
		CProject project = new CProject(targetDir);
		CTree ctree0 = project.getCTreeList().get(0);
		File xhtml = ctree0.getExistingFulltextXHTML();
		Assert.assertTrue("xhtml: ", xhtml.exists());
		args = "--project "+targetDir+" -i fulltext.xhtml -o scholarly.html --transform tf2html";
		argProcessor = new NormaArgProcessor(args); 
		argProcessor.runAndOutput(); 
		File shtml = ctree0.getExistingScholarlyHTML();
		Assert.assertTrue("shtml: ", shtml.exists());
	}
	
}