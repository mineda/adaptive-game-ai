package br.com.mineda.gameExperiment1.common;


public class Rule{

	private Clausule clausule;
	private String action;
	private Solver solver;
	private Integer priority = 0;
	private Boolean used = false;
	private Float weight = 0f;
	private Long id;
	
	public Rule(Clausule clausule, String action) {
		setClausule(clausule);
		setAction(action);
	}
	
    public Rule(Clausule clausule, String action, Long id, Integer priority, Float weight) {
        this(clausule, action);
        setId(id);
        setPriority(priority);
        setWeight(weight);
    }	
	
	public Rule(Clausule clausule, String action, Solver solver) {
		setSolver(solver);
		setClausule(clausule);
		setAction(action);
	}
	
	public Clausule getClausule() {
		return clausule;
	}


	public void setClausule(Clausule clausule) {
		if(clausule == null) {
			throw new RuntimeException("Cláusula não pode ser nula");
		}
		clausule.setSolver(getSolver());
		this.clausule = clausule;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		if(action == null) {
			throw new RuntimeException("Ação não pode ser nula");
		}
		this.action = action;
	}


	public Solver getSolver() {
		return solver;
	}

	public void setSolver(Solver solver) {
		if(solver == null) {
			throw new RuntimeException("Solver não pode ser nulo");
		}
		this.solver = solver;
		if(getClausule() != null) {
			getClausule().setSolver(solver);
		}
	}
	
	public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean evaluate() {
		if(clausule.isTrue()) {
			return true;
		}
		return false;
	}
	
	public Rule clone() {
	    Rule rule = new Rule(getClausule(), getAction(), getSolver());
	    rule.setId(getId());
	    rule.setPriority(getPriority());
	    rule.setUsed(getUsed());
	    rule.setWeight(getWeight());
		return rule;
	}

}
