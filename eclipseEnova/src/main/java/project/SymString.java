package project;

public class SymString extends BlockOSymbols{
	
	public SymString(String input) {
		super(input);
		this.type = "string";
	}
	
	@Override
	public boolean checkForError() {
		return false;
	}
	
}
