package br.com.mineda.gameExperiment1.common;

import java.io.Serializable;

public class Attribute implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// Nome do atributo
	private String name;
	// Descrição do atributo
	private String description;	
	// Valor do atributo
	// Por simplificacao somente serao aceitos atributos Float
	private Float value;
	// Valor original do atributo
	private Float originalValue;

    public Attribute(String name, String description, Float value, Float originalValue) {
        setName(name);
        setDescription(description);
        setValue(value);
        setOriginalValue(originalValue);
    }
    
    public Attribute(String name, String description, Boolean value, Boolean originalValue) {
        setName(name);
        setDescription(description);
        setValue(toFloat(value));
        setOriginalValue(toFloat(originalValue));        
    }
	
	public Attribute(String name, String description, Float value) {
		this(name, description, value, value);
	}
	
	public Attribute(String name, String description, Boolean value) {
	    this(name, description, value, value);
	}
	
	public Attribute(String name) {
		this(name, name, false, false);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if(name == null) {
			throw new RuntimeException("Nome nao pode ser nulo");
		}		
		this.name = name;
	}
	
	public Float getValue() {
		// O valor padr�o � 0
		if(value == 0) {
			return new Float(0);
		}
		return value;
	}
	
	public void setValue(Float value) {
		this.value = value;
	}
	
	public void setValue(Boolean value) {
	    setValue(toFloat(value));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Float getOriginalValue() {
        return originalValue;
    }
	
    public void setOriginalValue(Boolean value) {
        setOriginalValue(toFloat(value));
    }	

    public void setOriginalValue(Float originalValue) {
        this.originalValue = originalValue;
    }
    
    
    public Float toFloat(Boolean value) {
        if(value) {
            return new Float(1);
        }
        return new Float(0);
    }    

    public Attribute clone() {
		return new Attribute(getName(), getDescription(), getValue(), getOriginalValue());
	}
    
    public void reset() {
        setValue(getOriginalValue());
    }

}
