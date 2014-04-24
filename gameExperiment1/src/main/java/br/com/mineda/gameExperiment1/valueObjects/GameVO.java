package br.com.mineda.gameExperiment1.valueObjects;

import java.io.Serializable;
import java.util.Collection;

public class GameVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	Long id;
	
	Collection<LogVO> logs;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<LogVO> getLogs() {
		return logs;
	}

	public void setLogs(Collection<LogVO> logs) {
		this.logs = logs;
	}	
	
}
