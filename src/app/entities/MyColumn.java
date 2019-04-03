package app.entities;

public class MyColumn {
	
	private int colNum;
	private String colChar;
	
	public MyColumn(String colChar, int colNum) {
		this.colChar = colChar;
		this.colNum = colNum;
	}

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

	public String getColChar() {
		return colChar;
	}

	public void setColChar(String colChar) {
		this.colChar = colChar;
	}
	
}
