package br.com.mineda.gameExperiment1.common;


public class Variable extends Attribute implements Clausule {

	private static final long serialVersionUID = 1L;
	
	// Classe respons�vel por resolver a vari�vel, atualizando seu conte�do
	private Solver solver;
	// Owner da vari�vel
	private String owner;
	
	public Variable(String name, String description, Float value, String owner, Solver solver) {
		super(name, description, value);
		setOwner(owner);
		setSolver(solver);
	}
	
	public Variable(String name, String description, Float value, String owner) {
		this(name, description, value, owner, null);
	}
	
	public Variable(String name, String description, Float value, Solver solver) {
		this(name, description, value, null, solver);
	}
	
	public Variable(String name, String description, Float value) {
		this(name, description, value, null,  null);
	}
	
	public Variable(String name, String description, Boolean value, String owner, Solver solver) {
		super(name, description, value);
		setOwner(owner);
		setSolver(solver);
	}
	
	public Variable(String name, String description, Boolean value, String owner) {
		this(name, description, value, owner, null);
	}
	
	public Variable(String name, String description, Boolean value, Solver solver) {
		this(name, description, value, null, solver);
	}
	
	public Variable(String name, String description, Boolean value) {
		this(name, description, value, null,  null);
	}
	
	public Variable(String name, String owner) {
		this(name, name, false, owner, null);
	}

	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Override
	public Float evaluate() {
		Solver solver = getSolver();
		String owner = getOwner();
		if(solver != null && owner != null) {
			setValue(solver.solveVariable(getName(), getOwner()));
		}
		return getValue();
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
	}

	@Override
	public Solver getSolver() {
		return solver;
	}	
}
