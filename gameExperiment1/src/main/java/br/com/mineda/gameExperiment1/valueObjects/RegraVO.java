package br.com.mineda.gameExperiment1.valueObjects;

import java.io.Serializable;


public class RegraVO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String nome;
	
	private String clausula;
	
	private String acao;
	
	private Integer prioridade;
	
	public RegraVO() {
		super();
	}
	
	public RegraVO(Long id, String nome, String clausula, String acao) {
		super();
		setId(id);
		setNome(nome);
		setClausula(clausula);
		setAcao(acao);
	}
	
	public RegraVO(String nome, String clausula, String acao) {
		this(null, nome, clausula, acao);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setClausula(String clausula) {
		this.clausula = clausula;
	}

	public String getClausula() {
		return clausula;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getAcao() {
		return acao;
	}

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

}
