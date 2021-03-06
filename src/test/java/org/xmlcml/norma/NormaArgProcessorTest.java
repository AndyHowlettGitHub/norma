package org.xmlcml.norma;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;

public class NormaArgProcessorTest {

	
	private static final Logger LOG = Logger.getLogger(NormaArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // as this just prints help
	public void testNoArgs() {
		String[] args = {
		};
		new NormaArgProcessor(args);
	}
	
	@Test // outputs help
	@Ignore // to cut down output
	public void testNoArgsMain() {
		String[] args = {
		};
		Norma.main(args);
		Assert.assertTrue("finished", true);
		LOG.trace("finished");
	}
	

	@Test
	public void testArgs() {
		FileUtils.deleteQuietly(new File("foo"));
		FileUtils.deleteQuietly(new File("bar"));
		String[] args = {
			"-i", "foo", "bar", 
			"-o", "target/plugh",
		};
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args);
		Assert.assertEquals("output", "target/plugh", argProcessor.getOutput());
		Assert.assertEquals("input", 2, argProcessor.getInputList().size());
		//args get sorted
		Assert.assertEquals("input", "foo", argProcessor.getInputList().get(1));
		Assert.assertEquals("input", "bar", argProcessor.getInputList().get(0));
	}
	
	@Test
	public void testPDF() {
		String dir = "target/plugh/plugh0";
		String[] args = {
			"-i", new File(NormaFixtures.TEST_BMC_DIR, "misc/s12862-014-0277-x.pdf").toString(),
			"-o", dir,
		};
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args);
		Assert.assertEquals("output", dir, argProcessor.getOutput());
		Assert.assertEquals("input", 1, argProcessor.getInputList().size());
	}
	
	@Test
	public void testAutoDetect() {
		String dir = "target/plugh/plugh1";
		String[] args = {
			"-i", new File(NormaFixtures.TEST_BMC_DIR, "misc/s12862-014-0277-x.pdf").toString(),
			"-o", dir,
		};
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args);
		Assert.assertEquals("output", dir, argProcessor.getOutput());
		Assert.assertEquals("input", 1, argProcessor.getInputList().size());
	}
	
	@Test
	public void testMethod() {
		String dir = "target/plugh/plugh2";
		String inputFilename =  new File(NormaFixtures.TEST_BMC_DIR, "misc/s12862-014-0277-x.pdf").toString();
		String[] args = {
			"-i", inputFilename,
			"-o", dir,
			"-p", "bmc",
		};
		NormaArgProcessor argProcessor = new NormaArgProcessor(args);
		Assert.assertEquals("input", 1, argProcessor.getInputList().size());
		Assert.assertEquals("input",  inputFilename, argProcessor.getInputList().get(0));
		Assert.assertEquals("output", dir, argProcessor.getOutput());
		Assert.assertEquals("pubstyle", "bmc", argProcessor.getPubstyle().toString());
	}
	
	@Test
	@Ignore // because it is voluminous
	public void testHelp() {
		DefaultArgProcessor argProcessor = new NormaArgProcessor(new String[]{"-h"});
	}
	
	@Test
	/** normalizes an XML file and writes out shtml.
	 * 
	 *  // SHOWCASE
	 * Not fully tagged. this is to test directory mechanism.
	 * 
	 * @throws IOException
	 */
	public void testCreateScholarlyHtml() throws IOException {
		File container0115884 = new File("target/plosone/0115884/");
		if (container0115884.exists()) FileUtils.forceDelete(container0115884);
		FileUtils.copyDirectory(NormaFixtures.F0115884_DIR, container0115884);
		String args = "-q "+container0115884.toString()+
				" --transform nlm2html --input fulltext.xml --output scholarly.html --standalone true";
		Norma norma = new Norma();
		norma.run(args);
		CTreeList cTreeList = norma.getArgProcessor().getCTreeList();
		Assert.assertNotNull(cTreeList);
		Assert.assertEquals("CTree/s",  1,  cTreeList.size());
		CTree cTree = cTreeList.get(0);
		List<File> files = cTree.listFiles(true);
		LOG.trace(cTree+"; "+files);
		Assert.assertEquals(5, files.size());
	}
	
	/** normalizes an XML file and writes out shtml.
	 * 
	 * Not fully tagged. this is to test directory mechanism.
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore // FIXME 
	public void testCTreeWithDTD() throws IOException {
		File container0115884 = new File("target/plosone/0115884withdtd/");
		if (container0115884.exists()) FileUtils.forceDelete(container0115884);
		FileUtils.copyDirectory(NormaFixtures.F0115884_DIR, container0115884);
		String[] args = {
			"-q", container0115884.toString(), // output from quickscrape
			"--transform", "nlm2html",                  // stylesheet to use (code)
			"--standalone", "false",           // force use of DTD. May fail
			"-e", "xml"                       // type of file to transform
		};
		if (1==1) throw new RuntimeException("Recast as CTree");

		int expectedFileCount = 5; // because of the output file
		
		// note the XML file has a DTD and takes 10 secs to process because of repeated downloads.
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args);
		try {
//			argProcessor.normalizeAndTransform();
		} catch (Exception e) {
			// not connected
			String errorMessage = ExceptionUtils.getRootCauseMessage(e);
			Assert.assertEquals("UnknownHostException: dtd.nlm.nih.gov", errorMessage.trim());
			expectedFileCount = 4; // no output file
		}
		CTreeList cTreeList = argProcessor.getCTreeList();
		Assert.assertNotNull(cTreeList);
		Assert.assertEquals("CTree/s",  1,  cTreeList.size());
		CTree cTree = cTreeList.get(0);
		List<File> files = cTree.listFiles(true);
		Assert.assertEquals(expectedFileCount, files.size());
	}
	
	@Test
	/** transforms PDF to text
	 * 
	 *  // SHOWCASE
	 * 
	 * @throws IOException
	 */
	public void testPDF2TXT() throws IOException {
		File container0115884 = new File("target/plosone/0115884/");
		if (container0115884.exists()) FileUtils.forceDelete(container0115884);
		FileUtils.copyDirectory(NormaFixtures.F0115884_DIR, container0115884);
		String args = "-q "+container0115884.toString()+" --transform pdf2txt --input fulltext.pdf --output fulltext.pdf.txt";
		LOG.trace(args);
		Norma norma = new Norma();
		norma.run(args);
		CTreeList cTreeList = norma.getArgProcessor().getCTreeList();
		Assert.assertNotNull(cTreeList);
		Assert.assertEquals("CTree/s",  1,  cTreeList.size());
		CTree cTree = cTreeList.get(0);
		List<File> files = cTree.listFiles(true);
		LOG.trace(files);
		Assert.assertEquals(5, files.size());
	}
	
	@Test
	/** transforms raw Html to Html
	 * 
	 * @throws IOException
	 */
	public void testHTML2HTML() throws IOException {
		File container1196402 = new File("target/ieee/1196402/");
		if (container1196402.exists()) FileUtils.forceDelete(container1196402);
		FileUtils.copyDirectory(NormaFixtures.F0115884_DIR, container1196402);
		String args = "-q "+container1196402.toString()+" --html jsoup --input fulltext.html --output fulltext.xhtml";
		LOG.trace(args);
		Norma norma = new Norma();
		norma.run(args);
		CTreeList cTreeList = norma.getArgProcessor().getCTreeList();
		Assert.assertNotNull(cTreeList);
		Assert.assertEquals("CTree/s",  1,  cTreeList.size());
		CTree cTree = cTreeList.get(0);
		List<File> files = cTree.listFiles(true);
		LOG.trace(files);
		Assert.assertEquals(5, files.size());
	}

	@Test
	/** transforms raw Html to Html
	 * 
	 * @throws IOException
	 */
	@Ignore // not open access
	public void testHTML2HTMLNature() throws IOException {
		FileUtils.copyDirectory(new File("../miningtests/nature/doi_10_1038_nnano_2014_93"),  new File("target/nature/nnano/"));
		String args = "-q target/nature/nnano/"
				+ " --html jsoup"
				+ " --input fulltext.html"
				+ " --output fulltext.xhtml";
		Norma norma = new Norma();
		norma.run(args);

	}
	

	@Test
	public void testPubstyle() throws Exception {
		String args = "--pubstyle bmc";
		Norma norma = new Norma();
		norma.run(args);
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) norma.getArgProcessor();
		List<ArgumentOption> chosenOptions = argProcessor.getChosenArgumentList();
	}

	@Test
	public void testMultipleCTrees() throws IOException {
		File containerPLOSONE = new File("target/plosone/multiple/");
		if (containerPLOSONE.exists()) FileUtils.forceDelete(containerPLOSONE);
		FileUtils.copyDirectory(new File(NormaFixtures.TEST_PUBSTYLE_DIR, "plosoneMultiple"), containerPLOSONE);
		String args = "-q "+containerPLOSONE.toString()+
				" --transform nlm2html --input fulltext.xml --output scholarly.html --standalone true";
		LOG.trace("args> "+args);
		Norma norma = new Norma();
		norma.run(args);
	}
	
	@Test
	/** creates new CTrees for list of PDF and then transforms
	 * 
	 */
	@Ignore // uses non-local files
	public void testNormalizeIEEEPDFs() throws IOException {
		String args;
		args = "-i fulltext.pdf --ctree ../cmine/target/ieee/musti/Henniger -o fulltext.txt --transform pdf2txt";
		new Norma().run(args);
	}

	@Test
	/** creates new CTrees for list of HTML and then transforms
	 * 
	 */
	public void testCreateCTreesForIEEEHtml() throws IOException {
		new Norma().run("");
		String args;
		args = "-i src/test/resources/org/xmlcml/norma/pubstyle/ieee -o target/ieee/ -e html --ctree ";
		new Norma().run(args);
		args = "-i fulltext.html -o fulltext.xhtml --ctree target/ieee --html jsoup";
		new Norma().run(args);
	}


	@Test
	/** creates new CTrees for list of HTML and then transforms
	 * 
	 * SHOWCASE
	 */
	public void testTransformRawHtmlToScholarly() throws IOException {
		String args;
		args = "-i src/test/resources/org/xmlcml/norma/pubstyle/ieee -o target/ieee/ -e html --ctree ";
		new Norma().run(args);
		args = "-i fulltext.html -o fulltext.xhtml --ctree target/ieee --html jsoup";
		new Norma().run(args);
		args = "-i fulltext.xhtml -o scholarly.html --ctree target/ieee --transform ieee2html";
		new Norma().run(args);
	}

	@Test
	/** creates new CTrees for list of HTML and then transforms
	 * 
	 * SHOWCASE
	 */
	public void testTransformRawHtmlToScholarlyNature() throws IOException {
		FileUtils.copyDirectory(new File("src/test/resources/org/xmlcml/norma/pubstyle/nature/doi_10_1038_nnano_2011_101/"),
				new File("target/nature/"));
		String args;
		args = "-i fulltext.html -o fulltext.xhtml --ctree target/nature --html jsoup";
		new Norma().run(args);
		args = "-i fulltext.xhtml -o scholarly.html --ctree target/nature --transform nature2html";
		new Norma().run(args);
//		FileUtils.copyFile(new File("target/nature/fulltext.xhtml"), new File("target/nature/junk.xml")); //for display
	}
	
	@Test
	public void testMakeDocs() {
		String args = "--makedocs";
		NormaArgProcessor argProcessor = new NormaArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testVersion() {
		NormaArgProcessor argProcessor = new NormaArgProcessor();
		argProcessor.parseArgs("--version");
	}

	
	@Test
	public void testTag() {
		NormaArgProcessor argProcessor = new NormaArgProcessor();
		argProcessor.parseArgs("--chars a,b");
	}
	
	@Test
	public void testLog() throws IOException {
		DefaultArgProcessor argProcessor = new NormaArgProcessor();
		File targetFile = new File("target/test/log/");
		targetFile.mkdirs();
		// dummy file
		FileUtils.write(new File(targetFile, "fulltext.txt"), "fulltext");
		argProcessor.parseArgs("-q "+targetFile+" -i fulltext.txt  --c.test --log");
	}

}
