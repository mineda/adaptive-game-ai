package br.com.mineda.gameExperiment1.common;

public class Operation {

	public final static String GREATER_THAN = ">";
	public final static String LESSER_THAN = "<";
	public final static String GREATER_EQUAL_THAN = ">=";
	public final static String LESSER_EQUAL_THAN = "<=";
	public final static String NOT = "!";
	public final static String EQUAL = "=";
	public final static String NOT_EQUAL = "!=";
	public final static String ADDITION = "+";
	public final static String SUBTRACTION = "-";
	public final static String MULTIPLICATION = "*";
	public final static String DIVISION = "/";
	public final static String AND = "&";
	public final static String OR = "|";
	public final static String OPEN_PARENTHESIS = "(";
	public final static String CLOSE_PARENTHESIS = ")";	
	
	public static Boolean isUnary(String operation) {
		if(operation != null && operation.equals(Operation.NOT)) {
			return true;
		}
		return false;
	}
	
	public static Boolean isOperator(String string) {
		if(string == null)
			return false;
		if(string.equals(AND) || string.equals(NOT) || string.equals(ADDITION) || string.equals(CLOSE_PARENTHESIS) ||
			string.equals(DIVISION) || string.equals(EQUAL) || string.equals(GREATER_EQUAL_THAN) ||
			string.equals(GREATER_THAN) || string.equals(LESSER_EQUAL_THAN) || string.equals(LESSER_THAN) ||
			string.equals(MULTIPLICATION) || string.equals(NOT_EQUAL) || string.equals(OPEN_PARENTHESIS) ||
			string.equals(OR) || string.equals(SUBTRACTION))
			return true;
		return false;
	}
	
	public static Boolean isLogical(String string) {
		if(string == null)
			return false;
		if(string.equals(NOT) || string.equals(EQUAL) || string.equals(GREATER_EQUAL_THAN) || string.equals(GREATER_THAN) ||
			string.equals(LESSER_EQUAL_THAN) || string.equals(LESSER_THAN) || string.equals(NOT_EQUAL) ||
			string.equals(OR) || string.equals(AND))
			return true;
		return false;
	}
}
