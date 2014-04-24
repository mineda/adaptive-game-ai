package br.com.mineda.gameExperiment1.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.mineda.gameExperiment1.dao.GameDAO;
import br.com.mineda.gameExperiment1.dao.LogDAO;
import br.com.mineda.gameExperiment1.model.Game;
import br.com.mineda.gameExperiment1.model.Log;
import br.com.mineda.gameExperiment1.valueObjects.GameVO;
import br.com.mineda.gameExperiment1.valueObjects.LogVO;

public class GameBOImpl implements GameBO, Serializable {

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
	public GameVO addGame(GameVO gameVO) throws Exception {
		Game game = new Game();
		game.setId(gameVO.getId());
		game = gameDAO.salvar(game);
		List<Log> logs = new ArrayList<Log>();
		for(LogVO logVO: gameVO.getLogs()) {
			logs.add(logDAO.salvar(toLog(logVO)));
		}
		game.setLogs(logs);
		return gameDAO.atualizar(game).toGameVO();
	}

	@Override
	public GameVO readGame(Long id) throws Exception {
		return gameDAO.pesquisarPorId(id).toGameVO();
	}

	@Override
	public List<GameVO> listAll() {
		List<GameVO> list = new ArrayList<GameVO>();
		List<Game> listGames = gameDAO.todos();
		for(Game game: listGames) {
			list.add(game.toGameVO());
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
		log.setDateTime(logVO.getDateTime());
		log.setDecisionMakingSystem(logVO.getDecisionMakingSystem());
		log.setTurn(logVO.getTurn());
		return log;
	}	

}
