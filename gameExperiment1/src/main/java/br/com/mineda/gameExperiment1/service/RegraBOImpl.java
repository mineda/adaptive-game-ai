package br.com.mineda.gameExperiment1.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.mineda.gameExperiment1.dao.RegraDAO;
import br.com.mineda.gameExperiment1.model.Regra;
import br.com.mineda.gameExperiment1.valueObjects.RegraVO;

public class RegraBOImpl implements RegraBO, Serializable {

	private static final long serialVersionUID = 8277803249735628855L;
	
	RegraDAO regraDAO;
	
	public void setRegraDAO(RegraDAO regraDAO) {
		this.regraDAO = regraDAO;
	}
	
	@Override
	public Regra adicionarRegra(RegraVO regra) {
		return regraDAO.salvar(toRegra(regra));
	}

	@Override
	public List<RegraVO> listarRegras() {
		List<Regra> regras = regraDAO.todos();
		List<RegraVO> regrasVO = new ArrayList<RegraVO>();
		for(Regra regra: regras) {
			regrasVO.add(toRegraVO(regra));
		}
		return regrasVO;
		
	}

	@Override
	public RegraVO toRegraVO(Regra regra) {
		return new RegraVO(regra.getId(), regra.getNome(), regra.getClausula(), regra.getAcao());
	}

	@Override
	public Regra toRegra(RegraVO regraVO) {
		Regra regra = new Regra();
		regra.setNome(regraVO.getNome());
		regra.setClausula(regraVO.getClausula());
		regra.setAcao(regraVO.getAcao());
		regra.setPrioridade(regraVO.getPrioridade());
		return regra;
	}
	
	

}
