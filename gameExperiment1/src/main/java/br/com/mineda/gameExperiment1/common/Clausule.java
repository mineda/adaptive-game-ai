package br.com.mineda.gameExperiment1.common;

public interface Clausule {
	
	public Float evaluate ();
	public Boolean isTrue ();
	public void setSolver (Solver solver);
	public Solver getSolver ();

}
