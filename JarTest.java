/**
 * Code taken and modified from http://www.java2s.com/Code/Java/File-Input-Output/Listfilesinajarfile.htm
 */

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class JarTest {
	public static ArrayList<JarEntry> files = new ArrayList<JarEntry>();
	
	public static void main (String args[]) throws IOException {
		JarFile jarFile = new JarFile("/home/uga/katie.tieu/Untitled.jar");
		Enumeration<JarEntry> jarEnum = jarFile.entries();
		while (jarEnum.hasMoreElements()) {
			checkIfJava(jarEnum.nextElement());
		}
		
		jarFile.close();
		
		System.out.println(files);
	}

	private static void checkIfJava(Object obj) {
		JarEntry entry = (JarEntry)obj;
		String name = entry.getName();
		if (name.endsWith(".java")) {
			files.add(entry);
		}
	}
	
	private static void checkIfDir(Object obj) {
		// Same as checkIfJava, but with directories
		
		// Make another method for JAR files?
		// Can we combine them all into one method?
	}
	
}