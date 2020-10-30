package testCases;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import project.LanguageInterpreter;
import project.SymNumber;
import project.Word;
import project.BlockOSymbols;
import project.FileHandler;

public class InterpreterTester {
	
	LanguageInterpreter interpreter;
	ArrayList<BlockOSymbols> blocks;
	
	@Before
	public void init() {
		interpreter = new LanguageInterpreter();
		blocks = new ArrayList<BlockOSymbols>();
	}
	
	//FileHandler
	
	@Test
	public void readFile1Correct() {
		interpreter.wordList = FileHandler.readFile("test.txt");
		Word word = interpreter.wordList.get(0);
		assertTrue(word.getName().contentEquals("ein.viertel") && word.getNumber().contentEquals("0.25"));
	}
	@Test
	public void readFile2Correct() {
		interpreter.wordList = FileHandler.readFile("test.txt");
		Word word = interpreter.wordList.get(1);
		assertTrue(word.getName().contentEquals("ein.praefix") && word.getText().contentEquals("AgentTyp ein Bond"));
	}
	
	//Calculations
	
	@Test
	public void checkCalculateTest() {
		blocks.add(new SymNumber("(1+2)*3"));
		assertEquals(interpreter.calculate(blocks),"9");
	}
	@Test
	public void checkCalculateDecimalTest() {
		blocks.add(new SymNumber("((1+2)*3+ 7 * 4) * 0,5 "));
		assertEquals(interpreter.calculate(blocks),"18,5");
	}
	@Test
	public void checkCalculateThousendTest() {
		blocks.add(new SymNumber("(1.000,01 -0,01)/100 "));
		assertEquals(interpreter.calculate(blocks),"10");
	}
	@Test
	public void checkCalculatejfkTest() {
		blocks.add(new SymNumber("1.000/0 "));
		try {
			assertEquals(interpreter.calculate(blocks),"10");
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
}
