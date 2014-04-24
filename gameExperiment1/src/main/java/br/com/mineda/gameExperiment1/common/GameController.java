package br.com.mineda.gameExperiment1.common;

public interface GameController {
	
	/**
	 * Metodo que testa se uma acao pode ser executada
	 * @param action Acao a ser testada
	 * @return true se a acao pode ser executada e false se nao pode
	 */
    public Boolean testAction(String action, Character actor, Character adversary);
    
    /**
     * Metodo que retorna o valor de uma dada variavel
     * @param variableName Nome da variavel
     * @param variableOwner Dono da variavel
     * @return Valor da variavel
     */
	public Float solveVariable(String variableName, String variableOwner);

}
