package br.com.mineda.gameExperiment1.common;

import java.util.Collection;

public interface World extends Solver{
    
    /**
     * Reinicializa o mundo
     */
    public void reset();
    
    /**
     * Retorna uma lista de atributos que definem um personagem
     * @return Lista de atributos que definem um personagem
     */
    public Collection<Attribute> getCharacterAttributes();
    
}
