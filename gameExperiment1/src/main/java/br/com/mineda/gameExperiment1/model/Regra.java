package br.com.mineda.gameExperiment1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="REGRA")
public class Regra {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="REG_ID")
	private Long id;
	
	@Column(name="REG_NOME")
	private String nome;
	
	@Column(name="REG_CLAUSULA")
	private String clausula;
	
	@Column(name="REG_ACAO")
	private String acao;
	
	@Column(name="REG_PRIORIDADE")
	private Integer prioridade;

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

    public Regra() {
		super();
	}
}
