package project;

public class Word {
	
	private String type; //number or text
	private String name;
	private double number;
	private String text;
	
	public Word(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public void allocate(String input) {
		if (this.isText()) {
			input = input.replace("\"", "");
			text = input;
		}
		else {
			number = Double.parseDouble(input);
		}
	}
	
	public boolean isText() {
		return (type=="text");
	}
	
	@Override
	public String toString() {
		String result = "Word: <" + type + "> " + name + " = ";
		if (this.isText()) {
			result = result + text;
		}else {
			result = result + number;
		}
		return result;
	}
	
	public String getName() {
		return this.name;
	}
	public String getNumber() {
		return Double.toString(this.number);
	}
	public String getText() {
		return this.text;
	}
}
