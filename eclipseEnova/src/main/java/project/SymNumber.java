package project;

public class SymNumber extends BlockOSymbols{
	
	public SymNumber(String input) {
		super(input);
		this.type = "number";
	}
	
	@Override
	public boolean checkForError() {
		return false;
	}
	
}
