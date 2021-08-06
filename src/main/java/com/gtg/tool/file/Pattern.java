package com.gtg.tool.file;

public class Pattern {

	public Pattern() {
		super();
	}

	public Pattern(String pattern, int cnt) {
		super();
		this.pattern = pattern;
		this.cnt = cnt;
	}

	private String pattern;
	private int cnt;
	private String outStr = "";
	private int lineNumber = 0;
	
	public String getOutStr() {
		return outStr;
	}

	public void setOutStr(String outStr) {
		this.outStr = outStr;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public int getCnt() {
		return cnt;
	}
	
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

}



