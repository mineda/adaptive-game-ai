package br.com.mineda.gameExperiment1.common;

import java.io.Serializable;

public class Action implements Serializable{
	
	private static final long serialVersionUID = 2873070402948048359L;
	
	private Long id;
	private String name;
	private String description;
	
	public Action(Long id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
