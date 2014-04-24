package br.com.mineda.gameExperiment1.service;

import java.util.List;

import br.com.mineda.gameExperiment1.valueObjects.LogVO;

public interface LogBO {
	
	public LogVO addLog(LogVO log) throws Exception;
	public LogVO readLog(Long id) throws Exception;
	public List<LogVO> listAll();

}
