package br.com.mineda.gameExperiment1.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BotBattle implements Game {
    
    // Mundo do jogo
    private BotBattleWorld world = null;
    // Acoes disponiveis
    private Map<String, Action> actionsByName = null;
    // Acao corrente do jogador
    private String playerCharacterCurrentAction = null;
    // Acao corrente do computador
    private String nonPlayerCharacterCurrentAction = null;
    // Atributos de controle do jogador
    private Map<String, Attribute> playerCharacterControlAttributes = new HashMap<String, Attribute>();
    // Atributos de controle do computador
    private Map<String, Attribute> nonPlayerCharacterControlAttributes = new HashMap<String, Attribute>();
    // Atributos de controle do jogo
    private Map<String, Attribute> controlAttributes = new HashMap<String, Attribute>();    
    // Variaveis a ser colhidas ao salvar
    private Map<String, String> watchVariables = new HashMap<String, String>();
    // Indica fim de jogo
    private Boolean endGame = false;
    
    /**
     * Construtor
     * @param world Mundo do jogo
     */
    public BotBattle(BotBattleWorld world) {
        this.world = world;
        createActions();
        setTurn(0f);
        setEndGame(false);
    }

    @Override
    public Float solveVariable(String variableName, String variableOwner) {
        Float value = null;
        value = world.solveVariable(variableName, variableOwner);
        if(value == null) {
            Attribute attribute = null;
            if(Character.PLAYER.equals(variableOwner)) {
                attribute = playerCharacterControlAttributes.get(variableName);
            }
            else if(Character.COMPUTER.equals(variableOwner)) {
                attribute = nonPlayerCharacterControlAttributes.get(variableName);
            }
            else if(variableName.equals("RAND")) {
                value = (float)Math.random();
            }
            else {
                attribute = controlAttributes.get(variableName);
            }
            if(attribute != null) {
                value = attribute.getValue();
            }
        }
        return value;
    }

    @Override
    public Boolean testAction(String action, String characterName) {
        Character actor, adversary;
        if(world.getPlayerCharacter().getName().equals(characterName)) {
            actor = world.getPlayerCharacter();
            adversary = world.getNonPlayerCharacter();
        }
        else {
            actor = world.getNonPlayerCharacter();
            adversary = world.getPlayerCharacter();
        }
            
        Float distance = actor.getAttributeValue("POS") + adversary.getAttributeValue("POS");
        // Se o personagem esta atordoado nao pode executar acoes
        if(actor.isTrueAttribute("ATO")) {
            return false;
        }
        // Verifica efeito das acoes
        if(action.equals("AFA")) {
            if(actor.getAttributeValue("POS") >= world.getMaxPosition() || actor.getAttributeValue("PRO") > 0) {
                return false;
            }
            return true;
        }
        if(action.equals("APR")) {
            if(actor.getAttributeValue("PRO") > 0) {
                return false;
            }
            return true;
        }
        if(action.equals("MDF")) {
            if(actor.isTrueAttribute("MFO")) {
                return false;
            }
            return true;
        }
        if(action.equals("MDA")) {
            if(actor.isTrueAttribute("MAO")) {
                return false;
            }
            return true;
        }
        if(action.equals("MDN")) {
            if(actor.isTrueAttribute("MFO") || actor.isTrueAttribute("MAO")) {
                return true;
            }
            return false;
        }        
        if(action.equals("ATF") || action.equals("AFC") || action.equals("AFA")) {
            if(distance < 0.5f && distance > -0.5f) {
                return true;
            }
            return false;
        }
        if(action.equals("ATL") || action.equals("ALC") || action.equals("APR")) {
            if(distance >= 0.5f) {
                return true;
            }
            return false;
        }
        if(action.equals("DFC") || action.equals("REC") || action.equals("CAR")) {
            if(actor.getAttributeValue("ENE") >= 5f) {
                return true;
            }
            return false;
        } 
        if(action.equals("AVA")) {
            if(actor.isTrueAttribute("CAO") && 
                !actor.isTrueAttribute("PRO") && 
                !actor.isTrueAttribute("INO") && 
                !actor.isTrueAttribute("ELO") && 
                !actor.isTrueAttribute("ATO") && 
                (actor.getAttributeValue("ENE") >= 5f)) {
                return true;
            }
            return false;
        }          
        if(action.equals("PRE")) {
            if(actor.getAttributeValue("ENE") >= 6f && actor.getAttributeValue("POS") < world.getMaxPosition()) {
                return true;
            }
            return false;
        }
        if(action.equals("PXM")) {
            if(actor.isTrueAttribute("CAO") && 
                (actor.getAttributeValue("ENE") >= distance)) {
                return true;
            }
            return false;
        }
        if(action.equals("COR")) {
            if(actor.getAttributeValue("ENE") >= distance) {
                return true;
            }
            return false;
        }        
        if(action.equals("LIM")) {
            if((actor.isTrueAttribute("PRO") || 
                actor.isTrueAttribute("ELO")) && 
                (actor.getAttributeValue("ENE") >= 6f)) {
                return true;
            }
            return false;
        }        
        return true;
    }

    @Override
    public void setAction(String action, String characterName) {
        if(world.getPlayerCharacter().getName().equals(characterName)) {
            playerCharacterCurrentAction = action;
        }
        else {
            nonPlayerCharacterCurrentAction = action;
        }

    }

    @Override
    public void turn() {
        // Executa acao do jogador
        executeAction(playerCharacterCurrentAction, world.getPlayerCharacter(), nonPlayerCharacterCurrentAction, world.getNonPlayerCharacter());
        // Executa acao do computador
        executeAction(nonPlayerCharacterCurrentAction, world.getNonPlayerCharacter(), playerCharacterCurrentAction, world.getPlayerCharacter());
        // Atualiza acao anterior
        updateLastAction(world.getPlayerCharacter(), playerCharacterCurrentAction);     
        updateLastAction(world.getNonPlayerCharacter(), nonPlayerCharacterCurrentAction);
        // Verifica se jogo terminou
        if((world.getPlayerCharacter().getAttributeValue("SAU") <= 0F) || world.getNonPlayerCharacter().getAttributeValue("SAU") <= 0F) {
            setEndGame(true);
        }
        else {
            // Aplica modificadores
            updateModifiers(world.getPlayerCharacter());
            updateModifiers(world.getNonPlayerCharacter());
        }
        setTurn(getTurn() + 1F);
    }
    
    /**
     * Atualiza o atributo ultima acao
     * @param actor Ator cujo atributo sera atualizado
     * @param lastAction Ultima acao executada
     */
    private void updateLastAction(Character actor, String lastAction) {
        Float lastActionId = 0f;
        if(lastAction != null) {
            Action lastActionObject = actionsByName.get(lastAction);
            lastActionId = (lastActionObject == null)? 0: new Float(lastActionObject.getId());
        }
        actor.setAttribute("UAC", lastActionId);
    }
    
    /**
     * Executa uma acao
     * @param actorAction Acao do ator
     * @param actor Personagem a executar a acao
     * @param adversaryAction Acao do adversario
     * @param adversary Personagem adversario
     */
    private void executeAction(String actorAction, Character actor, String adversaryAction, Character adversary) {
        
        Float absorption = adversary.getAttributeValue("DNA");
        Boolean forceFieldActive = false;
        Boolean absorbing = false;
        
        // Acao inicialmente e considerada como executada
        actor.setAttribute("ERO", false);
        
        // Verifica se existe acao do adversario a executar que interfere na acao do ator
        if(adversaryAction != null) {
            if(adversaryAction.equals("APR")) {
                adversary.addToAttribute("POS", -1f);
            }
            else if(adversaryAction.equals("AFA")) {
                adversary.addToAttribute("POS", 1f);
            }
            else if(adversaryAction.equals("DEF")) {
                absorption *= 2f;
            }
            else if(adversaryAction.equals("DFC")) {
                forceFieldActive = true;
                absorption += 50f;
                adversary.addToAttribute("ENE", - 5f);
            }
            else if(adversaryAction.equals("AVA")) {
                adversary.addToAttribute("CMO", 10f);
                adversary.addToAttribute("ENE", -5f);
            }
            else if(adversaryAction.equals("ABS")) {
                absorbing = true;
            }
        }
        
        // Verifica se existe acao a executar
        if(actorAction != null) {
        
            Float damage = new Float(0);
            Float position = actor.getAttributeValue("POS");
            Float precision = actor.getAttributeValue("PRE");
            Boolean isImmuneAdversary = false;
            Boolean isImmuneActor = false;
            Float extraDamage = actor.getAttributeValue("COO");
            
            // Limita dano extra
            if(extraDamage > 5f) {
                extraDamage = 5f;
            } 
            
            // Verifica imunidade do ator
            if(actor.getAttributeValue("CMO") > 0f) {
                isImmuneActor = true;
            }
            
            if(adversary.getAttributeValue("CMO") > 0f) {
                isImmuneAdversary = true;
                absorption += 25f;
                // Decrementa um turno da duracao
                adversary.addToAttribute("CMO", -1f);
            } 
            
            // Atualiza duracao de carregado
            if(actor.getAttributeValue("CAO") > 0f)  {
                actor.addToAttribute("CAO", -1f);  
            }            
            
            // Verifica efeito das acoes
            if(actorAction.equals("ATF")) {
                damage = actor.getAttributeValue("DNF");
            }
            else if(actorAction.equals("AFC")) {
                damage = actor.getAttributeValue("DNF") + extraDamage*30f;
                actor.addToAttribute("COO", -extraDamage);
            }       
            else if(actorAction.equals("ATL")) {
                damage = actor.getAttributeValue("DNL");
                // Se campo magnetico esta ativo, dano de longa distancia e diminuido
                if(isImmuneActor) {
                    damage -= 25f;
                }
            }
            else if(actorAction.equals("ALC")) {
                damage = actor.getAttributeValue("DNL") + extraDamage*30f;
                actor.addToAttribute("COO", -extraDamage);
             // Se campo magnetico esta ativo, dano de longa distancia e diminuido
                if(isImmuneActor) {
                    damage -= 25f;
                }
            }        
            else if(actorAction.equals("REC")) {
                actor.addToAttribute("REO", 5f);
                actor.addToAttribute("ENE", -5f);
            }
            else if(actorAction.equals("LIM")) {
                actor.setAttribute("PRO", false);
                actor.setAttribute("ELO", false);
            }           
            else if(actorAction.equals("CON")) {
                actor.addToAttribute("COO", 1f);
            }
            else if(actorAction.equals("CAR")) {
                actor.addToAttribute("CAO", 5f);
                actor.addToAttribute("ENE", -5f);
            }
            else if(actorAction.equals("FOC")) {
                actor.setAttribute("FOO", true);
            }
            else if(actorAction.equals("PRE")) {
                // Se o adversario nao possui campo magnetico ativo
                if(!isImmuneAdversary) {
                    // Aplica status negativo paralizado
                    adversary.setAttribute("PRO", 6f);
                }
                // Senao
                else {
                    // Aplica status carregado
                    adversary.addToAttribute("CAO", 1f);
                }            
                actor.addToAttribute("ENE", -6f);
                if(!actor.isTrueAttribute("PRO")) {
                    position+=1;
                }
            }
            else if(actorAction.equals("PXM")) {
                actor.setAttribute("CAO", false);
                if(!isImmuneAdversary) {
                    adversary.setAttribute("PRO", 6f);
                }
                else {
                    adversary.addToAttribute("CAO", 1f);
                }
                if(!isImmuneActor) {
                    // Utiliza valor 6 porque ao final ocorre decremento automatico de todos os valores
                    actor.setAttribute("PRO", 6f);
                }
                else {
                    actor.addToAttribute("CAO", 1f);
                }            
                Float distance = position + adversary.getAttributeValue("POS");
                adversary.addToAttribute("SAU", -distance*20f);
                actor.addToAttribute("ENE", -distance);
                adversary.setAttribute("POS", -position);
            }   
            else if(actorAction.equals("COR")) {
                Float distance = position + adversary.getAttributeValue("POS");
                actor.addToAttribute("ENE", -distance);
                position = -adversary.getAttributeValue("POS");
            }       
            else if(actorAction.equals("ENE")) {
                actor.addToAttribute("ENE", 2f);
            }

            // Aplica efeitos positivos independentes do jogador
            if((actor.getAttributeValue("FOO") > 0f) && (damage > 0f))  {
                precision *= 2f;
                actor.setAttribute("FOO", false);
            }
            if((actor.getAttributeValue("CAO") > 0f) && (damage > 0f))  {
                if(!isImmuneAdversary) {
                    if(!adversary.isTrueAttribute("ELO")) {
                        adversary.addToAttribute("ELO", 5f);
                    }
                }
                else {
                    adversary.setAttribute("CAO", true);
                }            
            }
            
            actor.setAttribute("POS", position);
            
            Float avoidanceRoll = new Float(Math.random()*actor.getAttributeValue("PRE"));
            Float avoidance = adversary.getAttributeValue("ESQ");
            // Se o adversario esquiva
            if(avoidanceRoll > avoidance) {
                // Calcula e aplica dano
                Float effectiveDamage = damage - absorption;
                if(absorbing && effectiveDamage > 0) {
                    Float adversaryEnergy = adversary.getAttributeValue("ENE");
                    Float necessaryEnergy = new Float(Math.ceil(effectiveDamage / 10));
                    Float usedEnergy = adversaryEnergy;
                    if(adversaryEnergy > necessaryEnergy) {
                        usedEnergy = necessaryEnergy;
                        adversary.addToAttribute("COO", 1f);
                    }
                    adversary.addToAttribute("ENE", -usedEnergy);
                    effectiveDamage -= usedEnergy*10f;
                }
                if(effectiveDamage > 0) {
                    // Se o dano efetivo e positivo, avalia efeitos adversos
                    if(forceFieldActive) {
                        adversary.setAttribute("INO", true);
                        adversary.addToAttribute("ENE", new Float(Math.floor(effectiveDamage / 5f)));
                    }
                    Float hp = adversary.getAttributeValue("SAU") - effectiveDamage;
                    adversary.setAttribute("SAU", hp);
                    if(actorAction.equals("AFC") && effectiveDamage > 150f) {
                        adversary.setAttribute("ATO", 2f);
                    }
                    if(actorAction.equals("ALC") && effectiveDamage > 100f) {
                        adversary.addToAttribute("POS", new Float(Math.floor(effectiveDamage/10f)));
                        if(adversary.getAttributeValue("POS") > world.getMaxPosition()) {
                            adversary.setAttribute("POS", world.getMaxPosition());
                        }
                    }
                }
            }
            else if(damage > 0){
                // Acao falha
                actor.setAttribute("ERO", true);
            }
        }
    }
    
    /**
     * Atualiza status do ator de acordo com modificadores
     * @param actor Ator
     */
    private void updateModifiers(Character actor) {

        // Calcula recuperacao
        if(actor.getAttributeValue("REO") > 0f) {
            actor.addToAttribute("SAU", 10f);
            actor.addToAttribute("REO", -1f);
        }
        
        // Verifica efeitos adversos do ator
        if(actor.getAttributeValue("INO") > 0f) {
            actor.addToAttribute("INO", -1f);
            if(!actor.isTrueAttribute("INO")) {
                actor.setAttribute("ENE", 0f);
            }
        }
        if(actor.getAttributeValue("PRO") > 0f) {
            actor.addToAttribute("PRO", -1f);
        }
        if(actor.getAttributeValue("ATO") > 0f) {
            actor.addToAttribute("ATO", -1f);
        }
        if(actor.getAttributeValue("ELO") > 0f) {
            actor.addToAttribute("ELO", -1f);
            actor.addToAttribute("SAU", -10f);
        }
        
        // Atualiza quantidade de energia
        actor.addToAttribute("ENE", 1f);        
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void setWorld(World world) {
        if(world instanceof BotBattleWorld) {
            this.world = (BotBattleWorld)world;
        }
    }

    @Override
    public void reset() {
        world.reset();
        setTurn(0f);
        setEndGame(false);
    }

    @Override
    public Collection<Action> getActions() {
        if(actionsByName == null) {
            createActions();
        }
        return actionsByName.values();
    }
    
    /**
     * Cria lista de acoes disponiveis
     */
    private void createActions() {
        actionsByName = new HashMap<String, Action>();
        actionsByName.put("ATF", new Action(new Long(1), "ATF", "Ataque Fisico"));
        actionsByName.put("ATL", new Action(new Long(2), "ATL", "Ataque Longa Distancia"));
        actionsByName.put("APR", new Action(new Long(3), "APR", "Aproximar"));
        actionsByName.put("AFA", new Action(new Long(4), "AFA", "Afastar"));
        actionsByName.put("DEF", new Action(new Long(5), "DEF", "Defender"));
        actionsByName.put("AFC", new Action(new Long(6), "AFC", "Ataque Fisico Concentrado"));
        actionsByName.put("ALC", new Action(new Long(7), "ALC", "Ataque Longa Distancia Concentrado"));
        actionsByName.put("DFC", new Action(new Long(8), "DFC", "Defesa Concentrada"));
        actionsByName.put("REC", new Action(new Long(9), "REC", "Recuperar"));
        actionsByName.put("CON", new Action(new Long(10), "CON", "Concentrar"));
        actionsByName.put("CAR", new Action(new Long(11), "CAR", "Carregar"));
        actionsByName.put("PRE", new Action(new Long(12), "PRE", "Prender"));
        actionsByName.put("PXM", new Action(new Long(13), "PXM", "Puxao Magnetico"));
        actionsByName.put("COR", new Action(new Long(14), "COR", "Corrida"));
        actionsByName.put("AVA", new Action(new Long(15), "AVA", "Avatar"));
        actionsByName.put("FOC", new Action(new Long(16), "FOC", "Focar"));
        actionsByName.put("ENE", new Action(new Long(17), "ENE", "Energizar"));
        actionsByName.put("LIM", new Action(new Long(18), "LIM", "Limpar"));
        actionsByName.put("ABS", new Action(new Long(22), "ABS", "Absorver"));        
    }
    
    @Override
    public void addWatchVariable(String variableName, String watchName) {
        watchVariables.put(variableName, watchName);
    }
    
    @Override
    public void save() {
        // Para cada atributo disponivel
        for(String variable: watchVariables.keySet()) {
            // Recupera atributo do playerCharacter
            Attribute playerCharacterAttribute = world.getPlayerCharacter().getAttribute(variable);
            // Salva valor
            if(playerCharacterAttribute != null) {
                playerCharacterControlAttributes.put(watchVariables.get(variable), playerCharacterAttribute.clone());
            }
            // Recupera atributo do nonPlayerCharacter
            Attribute nonPlayerCharacterAttribute = world.getNonPlayerCharacter().getAttribute(variable);
            // Salva valor
            if(nonPlayerCharacterAttribute != null) {
                nonPlayerCharacterControlAttributes.put(watchVariables.get(variable), nonPlayerCharacterAttribute.clone());
            }            
        }
    }

    public String getPlayerCharacterCurrentAction() {
        return playerCharacterCurrentAction;
    }

    public void setPlayerCharacterCurrentAction(
            String playerCharacterCurrentAction) {
        this.playerCharacterCurrentAction = playerCharacterCurrentAction;
    }

    public String getNonPlayerCharacterCurrentAction() {
        return nonPlayerCharacterCurrentAction;
    }

    public void setNonPlayerCharacterCurrentAction(
            String nonPlayerCharacterCurrentAction) {
        this.nonPlayerCharacterCurrentAction = nonPlayerCharacterCurrentAction;
    }
    
    /**
     * Altera o valor da variável Turno
     * @param turn Turno
     */
    public void setTurn(Float turn) {
        controlAttributes.put("TRN", new Attribute("TRN", "Turn", turn));
    }
    
    /**
     * Recupera o valor da variável Turno
     * @return Turno
     */
    @Override
    public Float getTurn() {
        return controlAttributes.get("TRN").getValue();
    }
    
    public Float getPlayerHp() {
        return world.getPlayerCharacter().getAttributeValue("SAU");
    }
    
    public Float getComputerHp() {
        return world.getNonPlayerCharacter().getAttributeValue("SAU");
    }     

    public Boolean getEndGame() {
        return endGame;
    }

    public void setEndGame(Boolean endGame) {
        this.endGame = endGame;
    }

    @Override
    public Map<String, Attribute> getControlAttributes() {
        return controlAttributes;
    }    

}
