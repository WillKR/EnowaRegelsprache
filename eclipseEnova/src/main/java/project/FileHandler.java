package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
	
	public static ArrayList<Word> readFile(String filename) {
		ArrayList<Word> wordList = new ArrayList<Word>();

		try {
			File languageFile = new File(filename);
		    Scanner Reader = new Scanner(languageFile);
		    while (Reader.hasNextLine()) {
		    	String line = Reader.nextLine();
		        Word word = lineDevision(line);
		        wordList.add(word);
		    }
		    Reader.close();
		    
		} 
		catch (FileNotFoundException e) {
		    System.out.println("Language File was not found");
		    e.printStackTrace();
		}
		return wordList;
	}
	
	public static Word lineDevision(String line) {
		String[] wordParts = line.split("=");
		String type;
		if (wordParts[1].charAt(0)=='\"') {
			type = "text";
		}else {
			type = "number";
		}
		Word word = new Word(type, wordParts[0]);
		word.allocate(wordParts[1]);
		return word;
	}
	
	public static void printWordList(ArrayList<Word> wordList){
		for (Word word : wordList) {
			System.out.println(word);
		}
	}
}
