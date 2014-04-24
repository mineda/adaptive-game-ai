package br.com.mineda.gameExperiment1.service;

import java.util.List;

import br.com.mineda.gameExperiment1.valueObjects.GameVO;

public interface GameBO {
	
	public GameVO addGame(GameVO gameVO) throws Exception;
	public GameVO readGame(Long id) throws Exception;
	public List<GameVO> listAll();

}
