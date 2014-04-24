package br.com.mineda.gameExperiment1.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
	
	public class Token {
		private Boolean operator;
		private String value;
		public Token(Boolean operator, String value) {
			setOperator(operator);
			setValue(value);
		}
		public Boolean getOperator() {
			return operator;
		}
		public void setOperator(Boolean operator) {
			this.operator = operator;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public Clausule parse(String expression) throws Exception{
		String string;
		Integer index = 0;
		String cleanExpression = expression.replaceAll(" ", "");
		List<Token> tokens = new ArrayList<Token>();
		while((string = getNextToken(cleanExpression, index)) != null) {
			index += string.length();
			if(Operation.isOperator(string)) {
				tokens.add(new Token(true, string));
			}
			else {
				tokens.add(new Token(false, string));
			}
		}
		List<Token> posfix = new ArrayList<Token>();
		Stack<Token> operators = new Stack<Token>();
		// Posfixa a expressao
		// Para cada token
		for(Token token: tokens) {
			// Se é um operando adiciona à expressão
			if(token.getOperator() == false) {
				posfix.add(token);
			}
			// Senão
			else {
				// Se é um (, empilha
				if(token.getValue().equals(Operation.OPEN_PARENTHESIS)) {
					operators.push(token);
				}
				// Se é um ), desempilha e adiciona à expressão até achar um (
				else if(token.getValue().equals(Operation.CLOSE_PARENTHESIS)) {
					while(!operators.isEmpty()) {
						Token operator = operators.pop();
						if(operator.getValue().equals(Operation.OPEN_PARENTHESIS)) {
							break;
						}
						posfix.add(operator);
					}
				}
				// Se é um operador lógico desempilha e adiciona à expressão até achar um ( ou esvaziar a pilha
				else if(Operation.isLogical(token.getValue())) {
					while(!operators.isEmpty() && !operators.peek().getValue().equals(Operation.OPEN_PARENTHESIS)) {
						posfix.add(operators.pop());
					}
					operators.add(token);
				}
				// Se é + ou - desempilha e adiciona à expressão até achar um operador lógico, um ( ou esvaziar a pilha
				else if(token.getValue().equals(Operation.ADDITION) || token.getValue().equals(Operation.SUBTRACTION)) {
					while(!operators.isEmpty() && !operators.peek().getValue().equals(Operation.OPEN_PARENTHESIS) &&
							!Operation.isLogical(operators.peek().getValue())) {
						posfix.add(operators.pop());
					}
					operators.add(token);					
				}
				// Se é * ou / desempilha e adiciona à expressão até achar um operador lógico, um (, um +, um - ou esvaziar a pilha
				else if(token.getValue().equals(Operation.MULTIPLICATION) || token.getValue().equals(Operation.DIVISION)) {
					while(!operators.isEmpty() && !operators.peek().getValue().equals(Operation.OPEN_PARENTHESIS) &&
							!operators.peek().getValue().equals(Operation.ADDITION) &&
							!operators.peek().getValue().equals(Operation.SUBTRACTION) &&
							!Operation.isLogical(operators.peek().getValue())) {
						posfix.add(operators.pop());
					}
					operators.add(token);					
				}				
			}
		}
		// Por fim esvazia a pilha e adiciona tudo à expressão
		while(!operators.isEmpty()) {
			posfix.add(operators.pop());
		}
		// Falta transformar em Expression
		Stack<Clausule> operands = new Stack<Clausule>();
		for(Token token: posfix) {
			// Se é operador 
			if(token.getOperator() == true) {
				Boolean unary = Operation.isUnary(token.getValue());
				// Se não existem parâmetros suficientes na pilha
				if((unary && operands.isEmpty()) || (!unary && operands.size() < 2)) {
					// Lança exception
					throw new RuntimeException("Parâmetros insuficientes para operador " + token.getValue());
				}				
				// Cria uma Expression utilizando Clausules da pilha como parâmetros
				Clausule clausule1 = operands.pop();
				Expression newExpression;
				if(unary) {
					newExpression = new Expression(clausule1, token.getValue());
				}
				else {
					newExpression = new Expression(operands.pop(), clausule1, token.getValue());
				}
				// Empilha Expression
				operands.push(newExpression);				
			}
			// Senão é operando
			else {
				// Se é uma variável
				if(isAlpha(token.getValue().charAt(0))) {
					// Cria uma Variable e empilha
					operands.push(new Variable(token.getValue().substring(1,token.getValue().length()),token.getValue().substring(0,1)));
				}
				// Senão é uma constante
				else {
					// Cria uma Constant e empilha
					operands.push(new Constant(new Float(token.getValue())));
				}
			}
		}
		return operands.pop();
	}
	
	private String getNextToken(String expression, Integer index) {
		String token = null;
		if(index < expression.length()) {
			char character = expression.charAt(index++);
			token = "" + character;
			if(index < expression.length()) {
				if(isAlpha(character)) {
					while(index < expression.length() && isAlphanumeric(character = expression.charAt(index))) {
						index++;
						token += character;
					}
				} 
				else if(isNumeric(character)) {
					Boolean decimal = false;
					while(index < expression.length() && isNumeric(character = expression.charAt(index))) {
						index++;
						if(character == ',')
							character = '.';
						if(character == '.') {
							if(!decimal) {
								decimal = true;
								token += character;
							}
						}
						else {
							token += character;
						}
					}				
				}
				else {
					while(index < expression.length() && !isAlphanumeric(character = expression.charAt(index))) {
						String temp = token + character;
						if(!Operation.isOperator(temp))
							break;
						index++;
						token = temp;
					}
				}
			}
			
		}
		return token;
	}
	
	public static Boolean isNumeric(char character) {
		if((character >= '0' && character <= '9') || character == '.' || character == ',') {
			return true;
		}
		return false;
	}
	
	public static Boolean isAlpha(char character) {
		if((character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z')) {
			return true;
		}
		return false;
	}
	
	public static Boolean isAlphanumeric(char character) {
		if(isNumeric(character) || isAlpha(character)) {
			return true;
		}
		return false;
	}

}
