import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *	Test suite to test iteration 2.
 *	Note tests included content from feedback for iteration 1.
 */
public class Iteration2Tests {
	// Please change this to match the base directory on your machine before running
	public static String BASEDIR = "C:\\Users\\Wesle\\workspace\\SENGProj2";
	
	// for evaluating standard output, code taken from https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println 
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Before
	public void setUp() throws Exception {
		System.setOut(new PrintStream(outContent));
	}
	
	@After
	public void restoreStreams() {
		System.setOut(System.out);
	}

	// Equivalence test. Checks for correct parsing of a single directory
	@Test
	public void testNonRecursiveDirectory() {
		try {
			String[] args = {BASEDIR + "\\test_files"};
			Iteration2.main(args);
			assertEquals("A Declarations found: 1; References found: 0.\r\n" + 
					"B Declarations found: 1; References found: 0.\r\n" + 
					"C Declarations found: 1; References found: 0.\r\n" + 
					"D Declarations found: 1; References found: 0.\r\n" + 
					"E Declarations found: 2; References found: 0.\r\n" + 
					"H Declarations found: 1; References found: 0.\r\n", outContent.toString());
		} catch (IOException e) {
			System.out.println("IO Error while performing testNonRecursiveDirectory\n\n");
			e.printStackTrace();
		}
	}
	
	// Equivalence test. Checks for correct parsing of nested directories.
	@Test
	public void testRecursiveDirectory() {
		try {
			String[] args = {BASEDIR + "\\test_files1"};
			Iteration2.main(args);
			assertEquals("A Declarations found: 2; References found: 0.\r\n" + 
					"B Declarations found: 2; References found: 0.\r\n" + 
					"C Declarations found: 2; References found: 0.\r\n" + 
					"D Declarations found: 2; References found: 0.\r\n" + 
					"E Declarations found: 4; References found: 0.\r\n" + 
					"F Declarations found: 1; References found: 0.\r\n" + 
					"G Declarations found: 1; References found: 0.\r\n" + 
					"H Declarations found: 2; References found: 0.\r\n", outContent.toString());
		} catch (IOException e) {
			System.out.println("IO Error while performing testRecursiveDirectory\n\n");
			e.printStackTrace();
		}
	}
	
	// Boundary test. Checks for correct parsing of nested directories, but with no java files in them.
	@Test
	public void testEmptyRecursiveDirectory() {
		try {
			String[] args = {BASEDIR + "\\test_files2"};
			Iteration2.main(args);
			assertEquals("", outContent.toString());
		} catch (IOException e) {
			System.out.println("IO Error while performing testEmptyRecursiveDirectory\n\n");
			e.printStackTrace();
		}
	}
	
	// Equivalence test. Checks for correct parsing within a jar.
	@Test
	public void testJarWithDirectoryInside() {
		try {
			String[] args = {BASEDIR + "\\test_jar"};
			Iteration2.main(args);
			assertEquals("A Declarations found: 2; References found: 0.\r\n" + 
					"H Declarations found: 1; References found: 0.\r\n" + 
					"B Declarations found: 1; References found: 0.\r\n" + 
					"E Declarations found: 2; References found: 0.\r\n" + 
					"C Declarations found: 1; References found: 0.\r\n" + 
					"D Declarations found: 1; References found: 0.\r\n", outContent.toString());
		} catch (IOException e) {
			System.out.println("IO Error while performing testJarWithDirectoryInside\n\n");
			e.printStackTrace();
		}
	}
	
	// Boundary test. Checks for correct parsing within a directory containing two jars
	@Test
	public void testMultipleJarsInSingleDirectory() {
		try {
			String[] args = {BASEDIR + "\\test_jar2"};
			Iteration2.main(args);
			assertEquals("A Declarations found: 4; References found: 0.\r\n" + 
					"H Declarations found: 2; References found: 0.\r\n" + 
					"B Declarations found: 2; References found: 0.\r\n" + 
					"E Declarations found: 4; References found: 0.\r\n" + 
					"C Declarations found: 2; References found: 0.\r\n" + 
					"D Declarations found: 2; References found: 0.\r\n", outContent.toString());
		} catch (IOException e) {
			System.out.println("IO Error while performing testMultipleJarsInSingleDirectory\n\n");
			e.printStackTrace();
		}
	}
}