package br.com.mineda.gameExperiment1.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String owner;
	private Map<String, Attribute> attributes;
	
	public final static String PLAYER = "P";
	public final static String COMPUTER = "C";
    public final static String NONE = "N";	
	
	public Character(String name, String owner, Collection<Attribute> attributes) {
		this.attributes = new HashMap<String, Attribute>();
		setName(name);
		setOwner(owner);
		setAttributes(attributes);
	}
	
	public Character(String name, String owner) {
		this(name, owner, new ArrayList<Attribute>());
	}
	
	public Character(String name) {
		this(name, null, new ArrayList<Attribute>());
	}
	
	public Map<String, Attribute> getAttributes() {
	    return attributes;
	}
	
    public List<Attribute> getAttributesAsList() {
        return new ArrayList<Attribute>(attributes.values());
    }	

	public void setAttributes(Collection<Attribute> attributes) {
		if(attributes == null) {
			throw new RuntimeException("Atributos não podem ser nulos");
		}
		for(Attribute attribute: attributes) {
			addAttribute(attribute);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name == null) {
			throw new RuntimeException("Nome não pode ser nulo");
		}	
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void addAttribute(Attribute attribute) {
		getAttributes().put(attribute.getName(), attribute.clone());
	}
	
	public Attribute getAttribute(String key) {
	    return getAttributes().get(key);
	}
	
    public Float getAttributeValue(String key) {
        Attribute attribute = getAttributes().get(key);
        if(attribute == null)
            return new Float(0);
        return getAttributes().get(key).getValue();
    }
    
    public Boolean isTrueAttribute(String key) {
        Attribute attribute = getAttributes().get(key);
        if(attribute == null || attribute.getValue() < 0.5f) {
            return false;
        }
        return true;
    }    
	
	public void setAttribute(String key, Float value) {
	    Attribute attribute = getAttributes().get(key);
	    if(key != null) {
	        attribute.setValue(value);
	    }
	    else {
	        getAttributes().put(key, new Attribute(key, key, value));
	    }
	}
	
    public void addToAttribute(String key, Float value) {
        Attribute attribute = getAttributes().get(key);
        if(key != null) {
            attribute.setValue(attribute.getValue() + value);
        }
        else {
            getAttributes().put(key, new Attribute(key, key, value));
        }
    }	
	
    public void setAttribute(String key, Boolean value) {
        Attribute attribute = getAttributes().get(key);
        if(key != null) {
            attribute.setValue(value);
        }
        else {
            getAttributes().put(key, new Attribute(key, key, value));
        }
    }	
	
	public Character clone() {
		Character clone = new Character(getName(), getOwner());
		List<Attribute> attributesClone = new ArrayList<Attribute>();
		for(Attribute attribute: getAttributes().values()) {
			attributesClone.add(attribute.clone());
		}
		clone.setAttributes(attributesClone);
		return clone;
	}
	
	public void reset() {
	    for(Attribute attribute: getAttributes().values()) {
	        attribute.reset();
	    }
	}

}
