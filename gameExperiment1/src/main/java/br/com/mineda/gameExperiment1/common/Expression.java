package br.com.mineda.gameExperiment1.common;

public class Expression implements Clausule {
	
	private Clausule firstOperand;
	private Clausule secondOperand;
	private String operation;
	private Solver solver;
	
	public Expression(Clausule firstOperand, Clausule secondOperand, String operation, Solver solver) {
		setOperation(operation);
		setFirstOperand(firstOperand);
		setSecondOperand(secondOperand);
		setSolver(solver);
	}
	
	public Expression(Clausule firstOperand, Clausule secondOperand, String operation) {
		this(firstOperand, secondOperand, operation, null);
	}
	
	public Expression(Clausule firstOperand, String operation) {
		this(firstOperand, null, operation, null);
	}
	
	public Expression() {
		super();
	}

	@Override
	public Float evaluate() {
		// Para efeito de opera��es l�gicas True � considerado 1
		Float fTrue = new Float(1);
		// Para efeito de opera��es l�gicas False � considerado 0
		// Retorno padr�o para fun��o evaluate � False
		Float fFalse = new Float(0);
		// Para efeito de opera��es l�gicas valores superiores a 0.5 s�o True
		Float half = new Float(0.5);	
		if(getFirstOperand() == null || getOperation() == null) {
			return fFalse;
		}
		Float firstOperand = getFirstOperand().evaluate();
		if(Operation.isUnary(getOperation())) {
			if(operation.equals(Operation.NOT)) {
				if(firstOperand > half) {
					return fFalse;
				}
				else {
					return fTrue;
				}
			}
			return fFalse;
		}
		if(getSecondOperand() == null) {
			return fFalse;
		}
		Float secondOperand = getSecondOperand().evaluate();
		if(operation.equals(Operation.ADDITION)) {
			return firstOperand + secondOperand;
		}
		if(operation.equals(Operation.SUBTRACTION)) {
			return firstOperand - secondOperand;
		}
		if(operation.equals(Operation.MULTIPLICATION)) {
			return firstOperand * secondOperand;
		}
		if(operation.equals(Operation.DIVISION)) {
			return firstOperand / secondOperand;
		}		
		if(operation.equals(Operation.EQUAL)) {
			if(firstOperand.equals(secondOperand)) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}
		if(operation.equals(Operation.NOT_EQUAL)) {
			if(!firstOperand.equals(secondOperand)) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}
		if(operation.equals(Operation.GREATER_THAN)) {
			if(firstOperand > secondOperand) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}	
		if(operation.equals(Operation.GREATER_EQUAL_THAN)) {
			if(firstOperand >= secondOperand) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}	
		if(operation.equals(Operation.LESSER_THAN)) {
			if(firstOperand < secondOperand) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}	
		if(operation.equals(Operation.LESSER_EQUAL_THAN)) {
			if(firstOperand <= secondOperand) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}
		if(operation.equals(Operation.AND)) {
			if(firstOperand >= fTrue && secondOperand >= fTrue) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}
		if(operation.equals(Operation.OR)) {
			if(firstOperand >= fTrue || secondOperand >= fTrue) {
				return fTrue;
			}
			else {
				return fFalse;
			}
		}		
		return fFalse;
	}

	public Clausule getFirstOperand() {
		return firstOperand;
	}

	public void setFirstOperand(Clausule firstOperand) {
		if(firstOperand == null) {
			throw new RuntimeException("Primeiro operando não pode ser nulo");
		}
		this.firstOperand = firstOperand;
	}

	public Clausule getSecondOperand() {
		return secondOperand;
	}

	public void setSecondOperand(Clausule secondOperand) {
		if(!Operation.isUnary(getOperation()) && secondOperand == null) {
			throw new RuntimeException("Segundo operando não pode ser nulo para operações binárias");
		}
		this.secondOperand = secondOperand;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Override
	public Boolean isTrue() {
		// Para efeito de opera��es l�gicas valores superiores a 0.5 s�o True
		Float half = new Float(0.5);
		if(evaluate() > half)
			return true;
		return false;
	}

	@Override
	public void setSolver(Solver solver) {
		this.solver = solver;
		if(getFirstOperand() != null) {
			getFirstOperand().setSolver(solver);
		}
		if(getSecondOperand() != null) {
			getSecondOperand().setSolver(solver);
		}
	}

	@Override
	public Solver getSolver() {
		return this.solver;
	}	

}
