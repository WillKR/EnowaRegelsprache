package project;

public abstract class BlockOSymbols {
	
	public String input;
	public String type;
	
	public BlockOSymbols(String input) {
		this.input = input;
	}
	
	public abstract boolean checkForError();
	
	public void print() {
		System.out.println(input);
	}
	public String getType() {
		return this.type;
	}
	public String getValue() {
		return input;
	}
}
