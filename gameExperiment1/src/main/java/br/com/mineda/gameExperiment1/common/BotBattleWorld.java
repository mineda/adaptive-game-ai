package br.com.mineda.gameExperiment1.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class BotBattleWorld implements Serializable, World {
    
    /**
     * Serial version UID 
     */
    private static final long serialVersionUID = -5632877934396121270L;
    
    // The Player Character
    private Character playerCharacter;
    // The Non-Player Character
    private Character nonPlayerCharacter;
    // Atributos que definem um personagem
    private Collection<Attribute> characterAttributes = null;
    // Posicao maxima
    private Float maxPosition = 20F;
    
    // Nome de PC padrão
    public static final String PLAYER_CHARACTER_NAME = "Player 1";
    // Nome de NPC padrão
    public static final String NON_PLAYER_CHARACTER_NAME = "Player 2";
    
    // Construtor padrão
    public BotBattleWorld() {
        
    }
   
    @Override
    public void reset() {
        if(getPlayerCharacter() == null) {
            setPlayerCharacter(new Character(PLAYER_CHARACTER_NAME, Character.PLAYER, getCharacterAttributes()));
        }
        else {
            getPlayerCharacter().reset();
        }
        if(getNonPlayerCharacter() == null) {
            setNonPlayerCharacter(new Character(NON_PLAYER_CHARACTER_NAME, Character.COMPUTER, getCharacterAttributes()));
        }
        else {
            getNonPlayerCharacter().reset();
        }
    }

    @Override
    public Float solveVariable(String variableName, String variableOwner) {
        Character character = null;
        // Se o dono da variavel e o jogador (inicia com P)
        if(variableOwner.equals(Character.PLAYER)) {
            // Personagem e o jogador
            character = getPlayerCharacter();
        }
        // Senao, se o dono da variavel e o computador (inicia com C)
        else if(variableOwner.equals(Character.COMPUTER)) {
            // Personagem e o computador
            character = getNonPlayerCharacter();
        }
        // Se descobriu o dono
        if(character != null) {
            // Procura pela variavel
            Attribute attribute = character.getAttribute(variableName);
            // Se achou
            if(attribute != null) {
                // Retorna o valor
                return attribute.getValue();
            }
        }
        // Se nao achou nada retorna null
        return null;
    }
    
    @Override
    public Collection<Attribute> getCharacterAttributes() {
        if(characterAttributes == null) {
            characterAttributes = new ArrayList<Attribute>();
            characterAttributes.add(new Attribute("SAU", "Saude", new Float(1000)));
            characterAttributes.add(new Attribute("FOR","Forca", new Float(100)));
            characterAttributes.add(new Attribute("AGI","Agilidade", new Float(100)));
            characterAttributes.add(new Attribute("HAB","Habilidade", new Float(100)));
            characterAttributes.add(new Attribute("PDF","Poder de Fogo", new Float(100)));
            characterAttributes.add(new Attribute("DEF","Defesa", new Float(100)));
            characterAttributes.add(new Attribute("ENE","Energia", new Float(3)));
            characterAttributes.add(new Attribute("POS","Posicao", new Float(1)));
            characterAttributes.add(new Attribute("UAC","Ultima Acao", new Float(0)));
            characterAttributes.add(new Attribute("DNF","Dano Fisico", new Float(50)));
            characterAttributes.add(new Attribute("DNL","Dano Longa Distancia", new Float(50)));
            characterAttributes.add(new Attribute("ESQ","Esquiva", new Float(10)));
            characterAttributes.add(new Attribute("PRE","Precisao", new Float(100)));
            characterAttributes.add(new Attribute("DNA","Dano Absorvido", new Float(30)));
            characterAttributes.add(new Attribute("COO","Concentrado", new Float(0)));
            characterAttributes.add(new Attribute("CAO","Carregado", new Float(0)));
            characterAttributes.add(new Attribute("REO","Recuperando", new Float(0)));
            characterAttributes.add(new Attribute("CMO","Campo Magnetico", new Float(0)));
            characterAttributes.add(new Attribute("FOO","Focado", new Float(0)));
            characterAttributes.add(new Attribute("ELO","Eletrificado", new Float(0)));
            characterAttributes.add(new Attribute("PRO","Preso", new Float(0)));
            characterAttributes.add(new Attribute("ATO","Atordoado", new Float(0)));
            characterAttributes.add(new Attribute("INO","Instavel", new Float(0)));
            characterAttributes.add(new Attribute("ERO","Errou", new Float(0)));
        }
        return characterAttributes;
    }    

    public Character getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(Character playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public Character getNonPlayerCharacter() {
        return nonPlayerCharacter;
    }

    public void setNonPlayerCharacter(Character nonPlayerCharacter) {
        this.nonPlayerCharacter = nonPlayerCharacter;
    }

    public Float getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(Float maxPosition) {
        this.maxPosition = maxPosition;
    }

}
