
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.*;

public class Iteration1 {
	public static ArrayList<String> listOfDirs = new ArrayList<String>();
	public static File[] javaFiles;
	public int count_dec;
	public int count_ref;

	public static void main(String[] args) {
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
		Iteration1 it1 = new Iteration1(args[0]);
		System.out.println("Current List of Dirs: " + listOfDirs + "\n");

		for(int i = 0; i <= listOfDirs.size() - 1; i++ ) {
			javaFiles = fileFinder(listOfDirs.get(i));
			
			for (File javaFile : it1.javaFiles) {
				String sourceCode;
				
				// Try to read the contents of file, if error occurs, skip and go to next file.
				try {
					sourceCode = new String(Files.readAllBytes(Paths.get(javaFile.toURI())));
				} catch (IOException e) {
					continue;
				}
				
				// if contents successfully read then parse the contents
				it1.parse(sourceCode);
			}
		}
		// print result
		it1.print();
		
		return;
	}
	
	/**
	 * Iteration1 constructor
	 * Takes given pathName
	 *  
	 */
	public Iteration1(String pathName) {
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
	 * Parses given source code and increments count_dec and count_ref if necessary.
	 */
	@SuppressWarnings("deprecation")
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
