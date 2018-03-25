// CHANGES: - Changed javaFiles from File[] to ArrayList<File>
// - Added ArrayList<JarFile> listOfJars
// - getJarEntries now checks for directories and jars (but is untested; not sure if it actually works)
// TODO: - Rewrite main function or constructor to call getJarEntries somewhere
// - Test getJarEntries + debug

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.*;
import java.util.stream.Collectors;
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
			
			// if contents successfully read then parse the contents
			it2.parse(sourceCode);
		}
		System.out.println("All Dec Types: " + typeDecs);
		System.out.println("All Ref Types: " + typeRefs + "\n");
		
		it2.increment();
	}
	
	/**
	 * Iteration1 constructor
	 * Takes given pathName
	 * @throws IOException 
	 *  
	 */
	public Iteration2(String pathName) throws IOException {
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
	 * Code for this method taken and modified from StackOverflow:
	 * https://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder
	 * 
	 * This method will return an array of .java files given a directory pathname
	 */
	private static void fileFinder(String dirName){
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
	 * @param absolute pathname to jar file
	 * @throws IOException 
	 */
	private static void getJarEntries(JarFile jarFile) throws IOException {
		Enumeration<JarEntry> jarEnum = jarFile.entries();
		while (jarEnum.hasMoreElements()) {
			checkFileType(jarFile, jarEnum.nextElement());
		}
	}
	
	/**
	 * Checks a JarEntry and adds it to the appropriate ArrayList if it is java, jar, or directory
	 * @param jarFile the jar file being checked (needed in order to convert entry to file)
	 * @param jarEntry the jar entry being checked
	 * @throws IOException
	 */
	private static void checkFileType(JarFile jarFile, JarEntry jarEntry) throws IOException {
			JarEntry entry = jarEntry;
			String name = entry.getName();

			File tempFile = File.createTempFile(name, "");	// Create temporary file with same name as file in jar
			tempFile.deleteOnExit();

			ZipEntry ze = jarFile.getEntry(name);			// Convert JarEntry to File
			InputStream in = jarFile.getInputStream(ze);
			FileOutputStream out = new FileOutputStream(tempFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) != -1) {
			    out.write(buffer, 0, length);
			}
			out.close();
			
			if (name.endsWith(".java")) {					// Add to list if appropriate
				javaFiles.add(tempFile);
			} else if (name.endsWith(".jar")) {
				JarFile newJar = new JarFile(name);
				getJarEntries(newJar);
			} else if (tempFile.isDirectory()){
				listOfDirs.add(name);
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
			//add the declaration type to an arraylist
			public boolean visit(TypeDeclaration node) {
				typeDecs.add(node.getName().toString());

				return true;
			}
			
			//add the reference type to an arraylist
			public boolean 	visit(FieldDeclaration node) {
				typeRefs.add(node.getType().toString());
				
				return true;
			}
		});
	}
	
	/**
	 * Method that increments the declaration and reference count
	 * 
	 */
	
	public void increment() {

		ArrayList<String> list = new ArrayList<String>();
		for(String i : typeDecs) {
			list.add(i);
		}
		
		Set<String> tempList = new LinkedHashSet<String>(list);
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
	
	
	public void print(String type) {
		System.out.println(type + " Declarations found: " + count_dec + "; References found: " + count_ref + ".");

	}
	
	/**
	 * method that resets the counts to zero
	 */
	public void resetCount() {
		count_ref = 0;
		count_dec = 0;
	}
}
