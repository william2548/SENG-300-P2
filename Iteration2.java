/**
 * Program which finds and prints all java type declarations and references
 * within a specified directory or JAR file.
 * 
 * @author Vishaal Bakshi, Katie Tieu, William Zhou
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.*;
import java.util.zip.ZipEntry;

import org.eclipse.jdt.core.dom.*;

public class Iteration2 {
	public static ArrayList<String> listOfDirs = new ArrayList<String>();
	public static ArrayList<File> javaFiles = new ArrayList<File>();
	public static ArrayList<String> typeDecs = new ArrayList<String>();
	public static ArrayList<String> typeRefs = new ArrayList<String>();
	public static int count_dec;
	public static int count_ref;
	public String type;

	/**
	 * Takes the pathname of a directory or jar file in the command line
	 * and counts type references/declarations in all java files within that specified path
	 * 
	 * @param args the command line arguments passed
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int argsLength = args.length;
		
		// If invalid amount of arguments, print error message and terminate
		if (argsLength < 1) {
			System.out.println("Error: Not enough arguments passed.\nExpected: <directory-pathname>");
			return;
		} else if (argsLength > 1) {
			System.out.println("Error: Too many arguments passed.\nExpected: <directory-pathname>");
			return;
		}
		
		// Else create a new instance and start work
		Iteration2 it2 = new Iteration2(args[0]);

		for(int i = 0; i <= listOfDirs.size() - 1; i++ ) {
			fileFinder(listOfDirs.get(i));
		}
			
		for (File javaFile : Iteration2.javaFiles) {
			String sourceCode;
			
			// Try to read the contents of file, if error occurs, skip and go to next file.
			try {
				sourceCode = new String(Files.readAllBytes(Paths.get(javaFile.toURI())));
			} catch (IOException e) {
				continue;
			}
			
			// If contents are read successfully then parse the contents
			it2.parse(sourceCode);
		}
		
		it2.increment();
	}
	
	/**
	 * Iteration2 constructor
	 * Takes a given directory and finds all sub-directories
	 * 
	 * @param pathName the pathname of the root directory to be searched
	 * @throws IOException 
	 */
	public Iteration2(String pathName) throws IOException {
		listOfDirs.add(pathName);
		File[] files = new File(pathName).listFiles();
		pathFinder(files);
		count_dec = 0;
		count_ref = 0;

	}
	
	/**
	 * Recursively find all sub-directories and add their pathnames to an arrayList
	 * 
	 * @param files the list of parent directories to be searched
	 * @throws IOException 
	 */
	public void pathFinder(File[] files) throws IOException {
		for(File i : files) {
			if(i.isDirectory()) {
				listOfDirs.add(i.toString());
				pathFinder(i.listFiles());
			}
			if(i.getName().endsWith(".jar")) {
				JarFile jf = new JarFile(i);
				getJarEntries(jf);
			}
		}
	}
	
	
	/**
	 * Searches a directory for java files and adds them to an array list
	 * Code for this method taken and modified from StackOverflow:
	 * https://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder
	 * 
	 * @param dirName the pathname of the directory to be searched
	 */
	private static void fileFinder(String dirName) {
		File dir = new File(dirName);
		File[] dirFiles = dir.listFiles();
		
		for (File i : dirFiles) {
			if (i.getName().endsWith(".java")) {
				javaFiles.add(i);
			}
		}
	}
        
	
	/**
	 * Goes through each entry in a jar file
	 * @param jarFile the absolute pathname to jar file
	 * @throws IOException 
	 */
	private static void getJarEntries(JarFile jarFile) throws IOException {
		Enumeration<JarEntry> jarEnum = jarFile.entries();
		while (jarEnum.hasMoreElements()) {
			checkFileType(jarFile, jarEnum.nextElement());
		}
	}
	
	/**
	 * Checks a JarEntry and adds it to the appropriate array list if it is java file or directory
	 * If the entry is a jar file, the contents are checked recursively
	 * 
	 * @param jarFile the jar file being checked
	 * @param jarEntry the jar entry being checked
	 * @throws IOException
	 */
	private static void checkFileType(JarFile jarFile, JarEntry jarEntry) throws IOException {
		JarEntry entry = jarEntry;
		String name = entry.getName();

		File tempFile = File.createTempFile(name, "");	// Create temporary file with same name as file in jar
		tempFile.deleteOnExit();

		ZipEntry ze = jarFile.getEntry(name);		// Convert JarEntry to File
		InputStream in = jarFile.getInputStream(ze);
		FileOutputStream out = new FileOutputStream(tempFile);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
		    out.write(buffer, 0, length);
		}
		out.close();
		
		if (name.endsWith(".java")) {			// Add to list if appropriate
			javaFiles.add(tempFile);
		} else if (tempFile.isDirectory()){
			listOfDirs.add(name);
		} else if (name.endsWith(".jar")) {		// If entry is a jar file, recursively call getJarEntries
			JarFile newJar = new JarFile(name);
			getJarEntries(newJar);
		}
	}

	/**
	 * Parses given source code and increments count_dec and count_ref if necessary.
	 * 
	 * @param sourceCode the source code to be parsed
	 */
	public void parse(String sourceCode) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceCode.toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			//add the declaration type to an array list
			public boolean visit(TypeDeclaration node) {
				typeDecs.add(node.getName().toString());

				return true;
			}
			
			//add the reference type to an array list
			public boolean 	visit(FieldDeclaration node) {
				typeRefs.add(node.getType().toString());
				
				return true;
			}
		});
	}
	
	/**
	 * Increments the declaration and reference counts and prints the final
	 */
	public void increment() {
		ArrayList<String> list = new ArrayList<String>();
		for(String i : typeDecs) {
			list.add(i);
		}
		
		Set<String> tempList = new LinkedHashSet<String>(list);		// Remove repeats in list
		list.clear();
		list.addAll(tempList);
		

		for(String i : list) {
			for(String j : typeDecs) {
				if(i.equals(j)) {
					count_dec++;
				}
			}
			for(String k : typeRefs) {
				if(i.equals(k)){
					count_ref++;
				}
			}
			print(i);
			resetCount();
		}
	}
	
	
	/**
	 * Prints the type declarations and references found
	 * 
	 * @param type the java type being counted
	 */
	public void print(String type) {
		System.out.println(type + " Declarations found: " + count_dec + "; References found: " + count_ref + ".");

	}
	
	/**
	 * Resets the declaration and reference counts to zero
	 */
	public void resetCount() {
		count_ref = 0;
		count_dec = 0;
	}
}
