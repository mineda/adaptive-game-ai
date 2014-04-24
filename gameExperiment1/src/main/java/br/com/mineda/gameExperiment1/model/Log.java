package br.com.mineda.gameExperiment1.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.mineda.gameExperiment1.valueObjects.LogVO;

@Entity
@Table(name="LOG")
public class Log {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="LOG_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn (name="GAM_ID", nullable=false)
	private Game game;		
	
	@Column(name="LOG_DATE_TIME")
	private Timestamp dateTime;
	
	@Column(name="LOG_TURN")
	private Integer turn;	
	
	@Column(name="LOG_DECISION_MAKING_SYSTEM")
	private String decisionMakingSystem;		
	
	@Column(name="LOG_PLAYER_HP")
	private Float playerHP;	
	
	@Column(name="LOG_COMPUTER_HP")
	private Float computerHP;	
    
    @Column(name="LOG_PERFORMANCE")
    private Float performance;
    
	@Column(name="LOG_PLAYER_ACTION")
	private String playerAction;
	
	@Column(name="LOG_COMPUTER_ACTION")
	private String computerAction;	
	
	@Column(name="LOG_PLAYER_ATTRIBUTES")
	private String playerAttributes;	
	
	@Column(name="LOG_COMPUTER_ATTRIBUTES")
	private String computerAttributes;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
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

	public Float getPerformance() {
        return performance;
    }

    public void setPerformance(Float performance) {
        this.performance = performance;
    }

    public void setComputerHP(Float computerHP) {
		this.computerHP = computerHP;
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
	
	public LogVO toLogVO() {
		LogVO logVO = new LogVO();
		logVO.setId(getId());
		logVO.setGameId(getGame().getId());
		logVO.setTurn(getTurn());
		logVO.setDateTime(getDateTime());
		logVO.setDecisionMakingSystem(getDecisionMakingSystem());
		logVO.setComputerAction(getComputerAction());
		logVO.setComputerAttributes(getComputerAttributes());
		logVO.setComputerHP(getComputerHP());
		logVO.setPerformance(getPerformance());
		logVO.setPlayerAction(getPlayerAction());
		logVO.setPlayerAttributes(getPlayerAttributes());
		logVO.setPlayerHP(getPlayerHP());
		return logVO;
	}

}
