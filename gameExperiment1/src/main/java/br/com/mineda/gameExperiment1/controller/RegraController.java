package br.com.mineda.gameExperiment1.controller;

import java.io.Serializable;
import java.util.List;

import br.com.mineda.gameExperiment1.service.RegraBO;
import br.com.mineda.gameExperiment1.valueObjects.RegraVO;

public class RegraController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private RegraBO regraBO;
	
	private RegraVO regra;
	
	public RegraVO getRegra() {
		return this.regra;
	}
	
	public void setRegra(RegraVO regra) {
		this.regra = regra;
	}
	
	public void setRegraBO(RegraBO regraBO) {
		this.regraBO = regraBO;
	}
	
	public RegraController() {
		this.setRegra(new RegraVO());
	}
	
	public void adicionarRegra() {
		regraBO.adicionarRegra(this.getRegra());
		this.setRegra(new RegraVO());
	}
	
	public List<RegraVO> getRegras() {
		return regraBO.listarRegras();
	}

}
