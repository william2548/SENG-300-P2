import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.*;
import java.util.zip.ZipEntry;

import org.eclipse.jdt.core.dom.*;

public class Iteration2 {
	public static ArrayList<String> listOfDirs = new ArrayList<String>();
	public static File[] javaFiles;
	public int count_dec;
	public int count_ref;

	public static void main(String[] args) throws IOException {
		int argsLength = args.length;
		
		// Throw error if invalid amount of arguments and terminate
		if (argsLength < 1) {
			System.out.println("Error: Not enough arguments passed.\nExpected: <directory-pathname>");
			return;
		} else if (argsLength > 1) {
			System.out.println("Error: Too many arguments passed.\nExpected: <directory-pathname>");
			return;
		}
		
		// if correct amount of arguments then create new instance and start work
		Iteration2 it2 = new Iteration2(args[0]);
		System.out.println("Current List of Dirs: " + listOfDirs + "\n");

		for(int i = 0; i <= listOfDirs.size() - 1; i++ ) {
			javaFiles = fileFinder(listOfDirs.get(i));
			
			for (File javaFile : it2.javaFiles) {
//				if (javaFile.getName().endsWith(".jar")) {
//					javaFiles = getJarEntries(new JarFile(args[0] + "/" + javaFile.getName()));
//				}
				String sourceCode;
				
				// Try to read the contents of file, if error occurs, skip and go to next file.
				try {
					sourceCode = new String(Files.readAllBytes(Paths.get(javaFile.toURI())));
				} catch (IOException e) {
					continue;
				}
				
				// if contents successfully read then parse the contents
				it2.parse(sourceCode);
			}
		}
		// print result
		it2.print();
		
		return;
	}
	
	/**
	 * Iteration1 constructor
	 * Takes given pathName
	 *  
	 */
	public Iteration2(String pathName) {
		listOfDirs.add(pathName);			//Might need to change this.. does 
		File[] files = new File(pathName).listFiles();
		pathFinder(files);
		count_dec = 0;
		count_ref = 0;

	}
	
	/**
	 * Recursively find all sub-directories 
	 * add the pathName to an arrayList
	 * @param files
	 */
	public void pathFinder(File[] files) {
		for(File i : files) {
			if(i.isDirectory()) {
				listOfDirs.add(i.toString());
				pathFinder(i.listFiles());
			}
		}
	}
	
	
	/**
	 * Code for this method taken and modified from StackOverflow:
	 * https://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder
	 * 
	 * This method will return an array of .java files given a directory pathname
	 */
	private static File[] fileFinder(String dirName){
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() { 
        	public boolean accept(File dir, String filename) {
        		return filename.endsWith(".java");
        	}
        });
    }
	
	/**
	 * Adds java files from a jar file to the javaFiles array
	 * @param absolute pathname to jar file
	 * @throws IOException 
	 */
	private static File[] getJarEntries(JarFile jarFile) throws IOException {
		ArrayList<File> javaFilesCopy = new ArrayList<File>(Arrays.asList(javaFiles));	// Create new ArrayList for java files so we can append to it
		Enumeration<JarEntry> jarEnum = jarFile.entries();
		while (jarEnum.hasMoreElements()) {		// Go through each entry
			ZipEntry ze = checkIfJava(jarFile, jarEnum.nextElement());				// Check if java
			
			if (ze != null) {					// If is java, create InputStream and convert file to String
				File tempFile = File.createTempFile(jarEnum.nextElement().getName(), ".java");	// Create temp file with same name as file in jar
				tempFile.deleteOnExit();
				
				InputStream inputStream = jarFile.getInputStream(ze);
				FileOutputStream out = new FileOutputStream(tempFile);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) != -1) {
				    out.write(buffer, 0, length);
				}
				javaFilesCopy.add(tempFile);
//				System.out.println(javaFilesCopy);
			}
		}
		File[] javaFiles = new File[javaFilesCopy.size()];
		javaFiles = javaFilesCopy.toArray(javaFiles);
		return javaFiles;
		
	}
	
	/**
	 * Checks if the jar entry in question ends in .java
	 * @param jarFile the jar file being checked
	 * @param jarEntry the JarEntry to check
	 * @return ZipEntry of jar entry if it is a java file
	 */
	// Change method name?
	private static ZipEntry checkIfJava(JarFile jarFile, Object jarEntry) {
		JarEntry entry = (JarEntry)jarEntry;
		String name = entry.getName();
		if (name.endsWith(".java")) {
			ZipEntry ze = jarFile.getEntry(name);
			return ze;
		}
		else { 
			return null;
		}
	}

	/**
	 * Parses given source code and increments count_dec and count_ref if necessary.
	 */
	public void parse(String sourceCode) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceCode.toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			// Count Declarations
			public boolean visit(TypeDeclaration node) {
				count_dec++;

				return true;
			}
			
			// Count References
			public boolean 	visit(FieldDeclaration node) {
				count_ref++;
				
				return true;
			}
		});
	}
	
	/**
	 * print:
	 * Prints the output string
	 */
	public void print() {
		System.out.println("Declarations found: " + count_dec + "; References found: " + count_ref + ".");
	}
}