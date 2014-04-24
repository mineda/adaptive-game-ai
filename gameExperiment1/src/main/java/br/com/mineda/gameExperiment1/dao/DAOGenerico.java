package br.com.mineda.gameExperiment1.dao;

import java.io.Serializable;
import java.util.List;

public interface DAOGenerico<T, ID extends Serializable> {
	
	public Class<T> getObjectClass();
	public T salvar(T object);
	public T pesquisarPorId(ID id);
	public T atualizar(T object);
	public void excluir(T object);
	public List<T> todos();

}
