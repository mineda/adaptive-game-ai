package br.com.mineda.gameExperiment1.common;

import java.util.Collection;
import java.util.Map;

public interface Game extends Solver {
    
    /**
     * Metodo que testa se uma acao pode ser executada
     * @param action Acao a ser testada
     * @return true se a acao pode ser executada e false se nao pode
     */
    public Boolean testAction(String action, String characterName);
    
    /**
     * Metodo que indica a acao a executar
     * @param action Acao a executar
     * @param characterName Personagem que executara a acao
     */
    public void setAction(String action, String characterName);
    
    /**
     * Metodo que fecha um turno de jogo
     */
    public void turn();
    
    /**
     * Retorna o mundo do jogo
     * @return Mundo do jogo
     */
    public World getWorld();
    
    /**
     * Define o mundo do jogo
     * @param world Mundo do jogo
     */
    public void setWorld(World world);

    /**
     * Inicializa o jogo
     */
    public void reset();
    
    /**
     * Retorna acoes disponiveis
     * @return Acoes disponiveis
     */
    public Collection<Action> getActions();
    
    /**
     * Inclui uma variavel para salvamento
     * @param variableName Nome da variavel
     * @param watchName Nome pelo qual a variavel salva sera reconhecida
     */
    public void addWatchVariable(String variableName, String watchName);
    
    /**
     * Salva o estado das variaveis escolhidas
     */
    public void save();
    
    /**
     * Recupera atributos de controle
     * @return Atributos de controle
     */
    public Map<String, Attribute> getControlAttributes();
    
    /**
     * Recupera o turno de jogo corrente
     * @return Turno de jogo corrente
     */
    public Float getTurn();
}
