package project;

public class Main {

	public static void main(String[] args) {
		
		LanguageInterpreter interpreter = new LanguageInterpreter();
		String answer;
		answer = interpreter.interprete("ein.praefix + ' ' + \"00\" + 14 * ein.halb");	
		System.out.println(answer+"\n\n");
	}

}
