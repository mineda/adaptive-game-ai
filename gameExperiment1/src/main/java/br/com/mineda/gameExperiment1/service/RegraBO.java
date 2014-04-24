package br.com.mineda.gameExperiment1.service;

import java.util.List;

import br.com.mineda.gameExperiment1.model.Regra;
import br.com.mineda.gameExperiment1.valueObjects.RegraVO;

public interface RegraBO {
	
	public Regra adicionarRegra(RegraVO regra);
	public List<RegraVO> listarRegras();
	public RegraVO toRegraVO(Regra regra);
	public Regra toRegra(RegraVO regraVO);

}
