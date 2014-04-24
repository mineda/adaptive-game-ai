package br.com.mineda.gameExperiment1.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.mineda.gameExperiment1.dao.GameDAO;
import br.com.mineda.gameExperiment1.dao.LogDAO;
import br.com.mineda.gameExperiment1.model.Log;
import br.com.mineda.gameExperiment1.valueObjects.LogVO;

public class LogBOImpl implements LogBO, Serializable {

	private static final long serialVersionUID = 1L;
	
	LogDAO logDAO;
	GameDAO gameDAO;
	
	public void setLogDAO(LogDAO logDAO) {
		this.logDAO = logDAO;
	}
	
	public void setGameDAO(GameDAO gameDAO) {
		this.gameDAO = gameDAO;
	}	

	@Override
	public LogVO addLog(LogVO log) throws Exception {
		return logDAO.salvar(toLog(log)).toLogVO();
	}

	@Override
	public LogVO readLog(Long id) throws Exception {
		return logDAO.pesquisarPorId(id).toLogVO();
	}

	@Override
	public List<LogVO> listAll() {
		List<LogVO> list = new ArrayList<LogVO>();
		List<Log> listLog = logDAO.todos();
		for(Log log: listLog) {
			list.add(log.toLogVO());
		}
		return list;
	}
	
	public Log toLog(LogVO logVO) {
		Log log = new Log();
		log.setId(logVO.getId());
		log.setGame(gameDAO.pesquisarPorId(logVO.getGameId()));
		log.setComputerAction(logVO.getComputerAction());
		log.setComputerAttributes(logVO.getComputerAttributes());
		log.setComputerHP(logVO.getComputerHP());
		log.setPlayerAction(logVO.getPlayerAction());
		log.setPlayerAttributes(logVO.getPlayerAttributes());
		log.setPlayerHP(logVO.getPlayerHP());
		log.setPerformance(logVO.getPerformance());
		log.setDateTime(logVO.getDateTime());
		log.setDecisionMakingSystem(logVO.getDecisionMakingSystem());
		log.setTurn(logVO.getTurn());
		return log;
	}

}
