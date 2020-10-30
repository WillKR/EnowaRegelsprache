package testCases;

import static org.junit.Assert.*;

import org.junit.*;

import project.LanguageInterpreter;


public class ApplicationTests {
	
	LanguageInterpreter interpreter;
	
	@Before
	public void init() {
		interpreter = new LanguageInterpreter();
	}
	@After
	public void finish() {
		System.out.println("\n\n");
	}

	@Test
	public void checkTausendTest() {
		String answer = interpreter.interprete("100.100,7 / 7");
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"14.300,1");
	}
	
	
	@Test
	public void checkCalculusTest() {
		String answer = interpreter.interprete("2 * (3 + (5 - 2))");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"12");
	}
	@Test
	public void checkBondTest() {
		String answer = interpreter.interprete("ein.praefix + ' ' + \"00\" + 14 * ein.halb");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"Agent 007");
	}
	@Test
	public void checkExeptionTousendTest() {
		try {
			interpreter.interprete("1001.00,7 / 7");
			fail();
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
	
	@Test
	public void checkExeptionBadMathTest() {
		try {
			interpreter.interprete("1+2--3");
			fail();
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
	
	@Test
	public void checkExeptionNoSuchVariableTest() {
		try {
			interpreter.interprete("zweiHalbe + \"ist keine Variable\"");
			fail();
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
	
	@Test
	public void checkExeptionKeinPlusTest() {
		try {
			String answer = interpreter.interprete("ein.praefix \" something\"");
			System.out.print("Answer is: "+answer);
			fail();
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
	
	@Test
	public void checkExeptionDoppelPlusTest() {
		try {
			String answer = interpreter.interprete("ein.praefix ++\" something\"");
			System.out.print("Answer is: "+answer);
			fail();
		}catch(Exception e){
			assertEquals(e.getClass().getName(),"java.lang.IllegalArgumentException");
		}
	}
	
	@Test
	public void checkVariablesTest() {
		String answer = interpreter.interprete("ein.praefix +\" ist ein.praefix\"");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"Agent ist ein.praefix");
	}
	@Test
	public void checkCalVariablesTest() {
		String answer = interpreter.interprete("ein.halb + ein.halb / ein.halb");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"1,5");
	}
	@Test
	public void checkCalVariablesCloserTest() {
		String answer = interpreter.interprete("ein.halb+ein.halb/ein.halb");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"1,5");
	}
	@Test
	public void checkStringStartsTest() {
		String answer = interpreter.interprete("\"strings können mit ' ' starten\"");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"strings können mit ' ' starten");
	}
	@Test
	public void checkPlusesTest() {
		String answer = interpreter.interprete("\"strings können ein + haben \" + \"sie addieren \" + 1 +2");	
		System.out.print("Answer is: "+answer);
		assertEquals(answer,"strings können ein + haben sie addieren 3");
	}
	
	//if somehow stuff was before the string variable
}
