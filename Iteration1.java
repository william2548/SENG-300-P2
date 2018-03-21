package seng3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.*;

public class Iteration1 {
	public File[] javaFiles;
	private String type;
	public String typeSimple;
	public int count_dec;
	public int count_ref;

	public static void main(String[] args) {
		int argsLength = args.length;
		
		// Throw error if invalid amount of arguments and terminate
		if (argsLength < 2) {
			System.out.println("Error: Not enough arguments passed.\nExpected: <directory-pathname> <qualified-name-java-type>");
			return;
		} else if (argsLength > 2) {
			System.out.println("Error: Too many arguments passed.\nExpected: <directory-pathname> <qualified-name-java-type>");
			return;
		}
		
		// if correct amount of arguments then create new instance and start work
		Iteration1 it1 = new Iteration1(args[0], args[1]);
		
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
		
		// print result
		it1.print();
		
		return;
	}
	
	/**
	 * Iteration1 constructor
	 * Takes given pathName and typeName and initializes global variables
	 */
	public Iteration1(String pathName, String typeName) {
		javaFiles = fileFinder(pathName);
		type = typeName;
		count_dec = 0;
		count_ref = 0;
		
		String[] types = type.split("\\.");
		if (types.length >= 1) {
			typeSimple = types[types.length-1];
		} else {
			typeSimple = type;
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
				String qualifiedName = node.getName().getFullyQualifiedName();
				//System.out.println("dec: " + qualifiedName);
				if (typeSimple.equals(qualifiedName)) {
					count_dec++;
				}
				return true;
			}
			
			// Count References
			public boolean 	visit(FieldDeclaration node) {
				String qualifiedName = node.getType().toString();
				//System.out.println("ref: " + qualifiedName);
				if (typeSimple.equals(qualifiedName)) {
					count_ref++;
				}
				return true;
			}
		});
	}
	
	/**
	 * print:
	 * Prints the output string
	 */
	public void print() {
		System.out.println(type + "; Declarations found: " + count_dec + "; References found: " + count_ref + ".");
	}
}