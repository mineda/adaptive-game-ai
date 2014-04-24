package br.com.mineda.gameExperiment1.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.mineda.gameExperiment1.valueObjects.GameVO;
import br.com.mineda.gameExperiment1.valueObjects.LogVO;

@Entity
@Table(name="GAME")
public class Game {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="GAM_ID")
	private Long id;
	
	@OneToMany(mappedBy="game", targetEntity = Log.class, fetch = FetchType.EAGER)
	private Collection<Log> logs;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<Log> getLogs() {
		return logs;
	}

	public void setLogs(Collection<Log> logs) {
		this.logs = logs;
	}	
	
	
	public GameVO toGameVO() {
		GameVO gameVO = new GameVO();
		gameVO.setId(getId());
		List<LogVO> logs = new ArrayList<LogVO>();
		for(Log log: getLogs()) {
			logs.add(log.toLogVO());
		}
		gameVO.setLogs(logs);
		return gameVO;
	}	
}
