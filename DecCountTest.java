/**
 * Data taken from the feedback from Iteration 1
 * 
 * @author Bader
 */

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DecCountTest {

	public String javaType;
	public Iteration2 i;
	public String pathName;
	@Before
	public void setUp() throws Exception {
		pathName = AllTests.BASEDIR + "Iteration 2\\src\\test_files";
		i = new Iteration2(pathName);
	}

	@After
	public void tearDown() throws Exception {
		i = null;
	}

	@Test
	public void testADeclarations() {
		String sourceCode;
		
		i.fileFinder(pathName);
		i.type = "A";
		
		// Try to read the contents of file, if error occurs, skip and go to next file.
		try {
			sourceCode = new String(Files.readAllBytes(Paths.get(Iteration2.javaFiles.get(0).toURI())));
		} catch (IOException e) {
			
		}
		
		// If contents are read successfully then parse the contents
		i.parse(sourceCode);
		
		
		
	}
}
