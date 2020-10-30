package project;
import java.util.ArrayList;

public class LanguageInterpreter {
	
	public ArrayList<Word> wordList; 
	public ArrayList<BlockOSymbols> blockOSymbolsList;
	
	public String interprete(String inputPhrase) {
		
		blockOSymbolsList = new ArrayList<BlockOSymbols>();
		wordList = FileHandler.readFile("VariablenBelegung.txt");
		FileHandler.printWordList(wordList);
		
		System.out.println("-----");
		System.out.println(inputPhrase);
		
		readInputPhrase(inputPhrase);	

		String answer = buildResult();
		
		System.out.println("-----");
		return answer;
	}
	
	private String buildResult() {
		String answer = "";
		ArrayList<BlockOSymbols> blockBuffer = new ArrayList<BlockOSymbols>();
		
		for (BlockOSymbols block : blockOSymbolsList){
			if (block.getType().equals("string")){
				if (!blockBuffer.isEmpty()) {
					String calculation = calculate(blockBuffer);
					answer = answer + calculation;
					blockBuffer.clear();
				}
				answer = answer + block.getValue();

			}
			else if (block.getType().equals("number")) {
				blockBuffer.add(block);
			}else {
				System.out.println("ERROR: Has no Type");
			}
		}
		if (!blockBuffer.isEmpty()) {
			String calculation = calculate(blockBuffer);
			answer = answer + calculation;
			blockBuffer.clear();
		}
		return answer;
	}
	
	//-------------
	//State class and method to handle an input phrase by dividing it into blocks
	//-------------
	
	private class State{
		char mainState = '0';		//0= null state; s= in string; r=in rechnung/variable;
		char stringState = '0';		//0= neuer Block; '' or "" Abänging vom Block umfang
		char numberState = '0';		//0= neuer Block; i=integer; d=double; z=zeichen(+,-,*,/)
		
		int tausendCounter = 0;		//um zu zählen wie viele zahlen hinter einen tausenderpunkt sind
		int bracketCounter = 0;
		boolean tausenderCountON = false;
		boolean hadOperator = true;
	}
	
	public void readInputPhrase(String inputPhrase) {
		char[] charInputPhrase = inputPhrase.toCharArray();
		State readerState = new State();
		ArrayList<Character> charBuffer = new ArrayList<Character>();
		
		for (char character : charInputPhrase) {
			if (readerState.mainState == 's') {
				handleStrings(character, readerState, charBuffer);
				continue;
			} 
			if (readerState.mainState == '0') {
				handleBetween(character, readerState, charBuffer);
				continue;
			}
			if (readerState.mainState == 'r') {
				handleNumbersAndVariables(character, readerState, charBuffer);
				continue;
			}
			if (character == ' ') continue;
			System.out.println("ERROR: Fehlerhafte Eingabe <EndOfChecks>"+getString(charBuffer)+ "<"+character+">");
			throw new IllegalArgumentException("Fehlerhafte Eingabe: "+ "<"+character+">");
		}	
		
		if (!charBuffer.isEmpty() && readerState.mainState == 'r') {
			char newState = checkForVariable(charBuffer);
			String block = getString(charBuffer);			
			if(newState == 'e' && (block.matches("[()0-9+\\-*\\/.,]+") == false)) {
				System.out.println("ERROR: Fehlerhafte Variable <Variable>"+getString(charBuffer));
				throw new IllegalArgumentException("Fehlerhafte Eingabe");
			}
			checkBracketCounter(readerState.bracketCounter);
			checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
			blockOSymbolsList.add(new SymNumber(block));
			charBuffer.clear();
		}
	}
	
	//-------------
	//methods to handle Strings, Numbers+Variables and the State in between
	//-------------

	public void handleStrings(char character, State readerState, ArrayList<Character> charBuffer) {
		if(readerState.stringState == '0') {
			if (character=='+') {
				readerState.mainState = '0';
				readerState.hadOperator = true;
			}
			return;
		}
		if(character == readerState.stringState) {
			String block = getString(charBuffer);
			blockOSymbolsList.add(new SymString(block));
			charBuffer.clear();
			readerState.stringState = '0';
			return;
		}
		charBuffer.add(character);
	}
	
	public void handleBetween(char character, State readerState, ArrayList<Character> charBuffer) {
		if (character!=' ') {
			if (character=='\'') {
				checkOperatorON(readerState, true);
				readerState.stringState = '\'';
				readerState.mainState = 's';
				readerState.hadOperator = false;
				return;
			}
			if (character=='"') {
				checkOperatorON(readerState, true);
				readerState.stringState = '"';
				readerState.mainState = 's';
				readerState.hadOperator = false;
				return;
			}
			if (character=='+') {
				checkOperatorON(readerState, false);
				readerState.hadOperator = true;
				return;
			}	
			if (character == '(') {
				checkOperatorON(readerState, true);
				readerState.mainState = 'r';
				charBuffer.add(character);						
				readerState.bracketCounter++;
				readerState.hadOperator = false;
				return;
			}
			if (Character.isDigit(character)) {
				checkOperatorON(readerState, true);
				readerState.mainState = 'r';
				readerState.numberState = 'i';
				charBuffer.add(character);						
				if (readerState.tausenderCountON) readerState.tausendCounter++;
				readerState.hadOperator = false;
				return;
			}
			
			if (Character.isAlphabetic(character)) {
				checkOperatorON(readerState, true);
				readerState.mainState = 'r';
				readerState.numberState = 'v';
				charBuffer.add(character);
				readerState.hadOperator = false;
				return;
			}
			System.out.println("ERROR: Fehlerhafte Symbol <String> "+character);
			throw new IllegalArgumentException("Fehlerhaft Symbol: "+character);
		}
	}
	
	public void handleNumbersAndVariables(char character, State readerState, ArrayList<Character> charBuffer) {
		if (character == '(') {
			charBuffer.add(character);	
			readerState.bracketCounter++;
			readerState.hadOperator = false;
			return;
		}
		if (character == ')') {
			charBuffer.add(character);	
			readerState.bracketCounter--;
			readerState.hadOperator = false;
			return;
		}
		if (readerState.numberState == 'i'|| readerState.numberState == 'd') {
			if (Character.isDigit(character)) {
				charBuffer.add(character);
				if (readerState.tausenderCountON) readerState.tausendCounter++;
				readerState.hadOperator = false;
				return;
			}
			if (character==',' && readerState.numberState == 'i'){
				readerState.numberState = 'd';
				charBuffer.add(character);
				readerState.tausendCounter++;
				return;
			}
			if (character=='.' && readerState.numberState == 'i'){
				charBuffer.add(character);
				readerState.tausendCounter = readerState.tausendCounter - 3;
				readerState.tausenderCountON = true;
				return;
			}
			if (character==' ') {
				readerState.numberState = 'z';
				return;
			}
			if (character=='-'||character=='*'||character=='/') {
				readerState.numberState = '0';
				charBuffer.add(character);
				checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
				readerState.tausenderCountON = false;
				readerState.tausendCounter = 0;
				checkOperatorON(readerState, false);
				readerState.hadOperator = true;
				return;
			}
			if (character=='+') {
				checkBracketCounter(readerState.bracketCounter);
				String block = getString(charBuffer);
				blockOSymbolsList.add(new SymNumber(block));
				charBuffer.clear();
				readerState.mainState = '0';
				readerState.numberState = '0';
				checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
				readerState.tausenderCountON = false;
				readerState.tausendCounter = 0;
				checkOperatorON(readerState, false);
				readerState.hadOperator = true;
				return;
			}
			System.out.println("ERROR: Fehlerhafte Symbol <Number>"+ character);
			throw new IllegalArgumentException("Fehlerhafte Symbol: " + character);
		}
		if (readerState.numberState == 'z') {
			if (character==' ') {
				return;
			}
			if (character=='+'||character=='-'||character=='*'||character=='/') {
				readerState.numberState = '0';
				charBuffer.add(character);
				checkOperatorON(readerState, false);
				readerState.hadOperator = true;
				return;
			}
		}
		
		if (readerState.numberState == 'v') {
			if (character=='-'||character=='*'||character=='/') {
				char newState = checkForVariable(charBuffer);
				if (newState == 'e') {
					System.out.println("ERROR: Fehlerhafte Variable <Variable>"+getString(charBuffer));
					throw new IllegalArgumentException("Fehlerhafte Eingabe: "+ "<"+character+">");
				}
				readerState.mainState = newState;
				readerState.numberState = '0';
				charBuffer.add(character);
				checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
				readerState.tausenderCountON = false;
				readerState.tausendCounter = 0;
				readerState.hadOperator = true;
				return;
			}
			if (character=='+') {
				char newState = checkForVariable(charBuffer);
				if (newState == 'e') {
					System.out.println("ERROR: Fehlerhafte Variable <Variable>"+getString(charBuffer));
					throw new IllegalArgumentException("Fehlerhafte Eingabe: "+ "<"+character+">");
				}
				String block = getString(charBuffer);
				if (newState == 'r') {
					blockOSymbolsList.add(new SymNumber(block));
				}else {
					blockOSymbolsList.add(new SymString(block));
				}
				charBuffer.clear();
				readerState.mainState = '0';
				readerState.numberState = '0';
				checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
				readerState.tausenderCountON = false;
				readerState.tausendCounter = 0;
				readerState.hadOperator = true;
				return;
			}	
			if (character == ' ') {
				char newState = checkForVariable(charBuffer);
				if (newState == 'e') {
					System.out.println("ERROR: Fehlerhafte Variable <Variable>"+getString(charBuffer));
					throw new IllegalArgumentException("Fehlerhafte Eingabe: "+ "<"+character+">");
				}
				readerState.mainState = newState;
				readerState.numberState = '0';
				return;
			}else {
				if (Character.isAlphabetic(character)||Character.isDigit(character) || character=='.') {
					charBuffer.add(character);
					return;
				}
			}
		}
		
		if (character=='+') {
			checkBracketCounter(readerState.bracketCounter);
			String block = getString(charBuffer);
			blockOSymbolsList.add(new SymNumber(block));
			charBuffer.clear();
			readerState.mainState = '0';
			readerState.numberState = '0';
			checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
			readerState.tausenderCountON = false;
			readerState.tausendCounter = 0;
			checkOperatorON(readerState, false);
			readerState.hadOperator = true;
			return;
		}	
		
		if (character=='-'||character=='*'||character=='/') {
			readerState.numberState = '0';
			charBuffer.add(character);
			checkTousendCount(readerState.tausenderCountON, readerState.tausendCounter);
			readerState.tausenderCountON = false;
			readerState.tausendCounter = 0;
			checkOperatorON(readerState, false);
			readerState.hadOperator = true;
			return;
		}
		
		if (readerState.numberState == '0') {
			if (Character.isDigit(character)) {
				readerState.numberState = 'i';
				charBuffer.add(character);						
				if (readerState.tausenderCountON) readerState.tausendCounter++;
				readerState.hadOperator = false;
				return;
			}
			if (Character.isAlphabetic(character)) {
				readerState.numberState = 'v';
				charBuffer.add(character);
				readerState.hadOperator = false;
				return;
			}
			if (character == ' ') {
				return;
			}
		}
		System.out.println("ERROR: Fehlerhafte Eingabe <Number>"+getString(charBuffer));
		throw new IllegalArgumentException("Fehlerhafte Eingabe: "+ "<"+character+">");
	}

	//-------------
	//methods to help with the tasks above
	//-------------
	
	public void checkOperatorON(State readerState, boolean shouldBe) {
		if (!readerState.hadOperator==shouldBe) {
			throw new IllegalArgumentException("operator problem");
		}
	}
	
	public void checkTousendCount(boolean tausenderCountON,int tausendCounter) {
		if (tausenderCountON && tausendCounter!=3) {
			System.out.println("ERROR: Tausender nicht richtig gezählt");
			throw new IllegalArgumentException("Tausender nicht richtig gezählt");
		}
	}
	
	public void checkBracketCounter(int bracketCounter) {
		if (bracketCounter!=0) {
			System.out.println("ERROR: Fehlerhafte Klammerzahl"+ bracketCounter);
			throw new IllegalArgumentException("Fehlerhafte Klammerzahl"+ bracketCounter);
		}
	}
	
	public char checkForVariable(ArrayList<Character> charBuffer) {
		String block = getString(charBuffer);
		for (Word word : wordList) {
			if (block.contains(word.getName())) {
				if (word.isText()) {
					block = block.replace(word.getName(),"");
					blockOSymbolsList.add(new SymString(word.getText()));
					charBuffer.clear();
					return '0';
				}else {
					block = block.replace(word.getName(), word.getNumber());
					block = block.replace(".", ",");
					charBuffer.clear();
					for (char c : block.toCharArray()) {
						charBuffer.add(c);
					}
					return 'r';
				}
			}
		}
		return 'e';
	}
	
	public String getString(ArrayList<Character> list)
	{    
	    StringBuilder builder = new StringBuilder(list.size());
	    for(Character ch: list)
	    {
	        builder.append(ch);
	    }
	    return builder.toString();
	}
	
	//-------------
	//methods to calculate the results from consecutive blocks
	//-------------
	
	public String calculate(ArrayList<BlockOSymbols> blocks) {
		String unifiedQuery = "";
		String finalResult;
		for (BlockOSymbols block : blocks){
			unifiedQuery = unifiedQuery +"+"+ block.getValue();
		}
		unifiedQuery = unifiedQuery.replace(" ", "");	//lücken weg
		unifiedQuery = unifiedQuery.replace(".", "");	//tausenderzeichen weg
		unifiedQuery = unifiedQuery.replace(",", ".");	//kommazahlen mit punkt statt komma

		unifiedQuery = unifiedQuery.substring(1);
		char[] queryChars = unifiedQuery.toCharArray();
		while (true){
			String firstnumber = "";
			String secondnumber = "";
			char operand = '0';
			String result = "";
			for (char character : queryChars) {
				
				if (Character.isDigit(character) || character=='.') {
					if (operand == '0') {
						firstnumber = firstnumber + character;
						continue;
					}
					secondnumber = secondnumber + character;
					continue;
				}
				if (character=='+'||character=='*'||character=='-'||character=='/') {
					if (operand=='0') {
						operand = character;
						continue;
					}else {
						if (operand == '*'|| operand =='/') {
							break;
						}else {
							firstnumber = secondnumber;
							operand = character;
							secondnumber = "";
						}
					}
				}
				if (character == '(') {
					firstnumber = "";
					secondnumber = "";
					operand = '0';
					continue;
				}
				if (character == ')') {
					break;
				}
			}
			if (!firstnumber.isEmpty()&&!secondnumber.isEmpty()) {
				result = calculateSimple(firstnumber, secondnumber, operand);				
			}
			
			if (!result.isEmpty()) {
				String toReplace = firstnumber + operand + secondnumber;
				unifiedQuery = unifiedQuery.replace("("+toReplace+")", result);
				unifiedQuery = unifiedQuery.replace(toReplace, result);

				queryChars = unifiedQuery.toCharArray();
				continue;
			}
			
			if (!firstnumber.isEmpty()&&secondnumber.isEmpty()) {
				finalResult = firstnumber;
				break;
			}
		}
		if (Double.parseDouble(finalResult) % 1 == 0) {
			return String.valueOf((int)Double.parseDouble(finalResult));
		}
		if (finalResult.contains(".")) {
			int index = finalResult.indexOf('.');
			String justFront = finalResult.substring(0,index);
			String withThousend = String.format("%,d", Integer.parseInt(justFront));
			finalResult = withThousend + "," + finalResult.substring(index+1);
		}else {
			finalResult = String.format("%,d", Integer.parseInt(finalResult));
		}
		return finalResult;
	}
	
	public String calculateSimple(String firstnumber, String secondnumber, char operator) {
		double number1 = Double.parseDouble(firstnumber);
		double number2 = Double.parseDouble(secondnumber);
		switch(operator) {
		case '+':
			return String.valueOf(number1 + number2);
		case '-':
			return String.valueOf(number1 - number2);
		case '*':
			return String.valueOf(number1 * number2);
		case '/':
			if (number2==0) {
				System.out.println("Can't devide through zero!");
				throw new IllegalArgumentException("/0");
			}
			return String.valueOf(number1 / number2);	
		}		
		return "";

	}
}
