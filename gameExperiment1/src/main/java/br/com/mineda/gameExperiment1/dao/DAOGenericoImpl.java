package br.com.mineda.gameExperiment1.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DAOGenericoImpl<T, ID extends Serializable> extends HibernateDaoSupport implements DAOGenerico<T, ID> {
	
	private final Class<T> oClass;

	@Override
	public Class<T> getObjectClass() {
		return this.oClass;
	}
	
	@SuppressWarnings("unchecked")
	public DAOGenericoImpl() {
		//determina através do argumento genérico T
		//a classe que será utilizada para o DAO
		this.oClass = (Class<T>)
		((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public T salvar(T object) {
		getHibernateTemplate().save(object);
		return object;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T pesquisarPorId(ID id) {
		return (T)getHibernateTemplate().get(oClass, id);
	}

	@Override
	public T atualizar(T object) {
		getHibernateTemplate().update(object);
		return object;
	}

	@Override
	public void excluir(T object) {
		getHibernateTemplate().delete(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> todos() {
		return getHibernateTemplate().loadAll(oClass);
	}

}
