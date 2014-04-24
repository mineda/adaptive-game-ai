package br.com.mineda.gameExperiment1.valueObjects;

import java.io.Serializable;
import java.sql.Timestamp;

public class LogVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long gameId;		
	
	private Timestamp dateTime;
	
	private Integer turn;	
	
	private String decisionMakingSystem;		
	
	private Float playerHP;	
	
	private Float computerHP;
	
	private Float performance;
	
	private String playerAction;
	
	private String computerAction;	
	
	private String playerAttributes;	
	
	private String computerAttributes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Integer getTurn() {
		return turn;
	}

	public void setTurn(Integer turn) {
		this.turn = turn;
	}

	public String getDecisionMakingSystem() {
		return decisionMakingSystem;
	}

	public void setDecisionMakingSystem(String decisionMakingSystem) {
		this.decisionMakingSystem = decisionMakingSystem;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public Float getPlayerHP() {
		return playerHP;
	}

	public void setPlayerHP(Float playerHP) {
		this.playerHP = playerHP;
	}

	public Float getComputerHP() {
		return computerHP;
	}

	public void setComputerHP(Float computerHP) {
		this.computerHP = computerHP;
	}

	public Float getPerformance() {
        return performance;
    }

    public void setPerformance(Float performance) {
        this.performance = performance;
    }

    public String getPlayerAction() {
		return playerAction;
	}

	public void setPlayerAction(String playerAction) {
		this.playerAction = playerAction;
	}

	public String getComputerAction() {
		return computerAction;
	}

	public void setComputerAction(String computerAction) {
		this.computerAction = computerAction;
	}

	public String getPlayerAttributes() {
		return playerAttributes;
	}

	public void setPlayerAttributes(String playerAttributes) {
		this.playerAttributes = playerAttributes;
	}

	public String getComputerAttributes() {
		return computerAttributes;
	}

	public void setComputerAttributes(String computerAttributes) {
		this.computerAttributes = computerAttributes;
	}
	

}
