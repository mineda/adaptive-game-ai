package br.com.mineda.gameExperiment1.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import br.com.mineda.gameExperiment1.common.Action;
import br.com.mineda.gameExperiment1.common.Attribute;
import br.com.mineda.gameExperiment1.common.Character;
import br.com.mineda.gameExperiment1.common.CompositeDecisionMakingSystem;
import br.com.mineda.gameExperiment1.common.DecisionMakingSystem;
import br.com.mineda.gameExperiment1.common.DynamicScripting;
import br.com.mineda.gameExperiment1.common.GameController;
import br.com.mineda.gameExperiment1.common.Parser;
import br.com.mineda.gameExperiment1.common.ProfileGroupElement;
import br.com.mineda.gameExperiment1.common.ProfileSimpleElement;
import br.com.mineda.gameExperiment1.common.Rule;
import br.com.mineda.gameExperiment1.common.RuleBasedSystem;

public class JogoController implements Serializable, GameController {
	
	private static final long serialVersionUID = 1L;
	
	private String status;
	private String action;
	private Boolean gameActive;
	private DecisionMakingSystem decisionMakingSystem;
	private DecisionMakingSystem playerDecisionMakingSystem;
	private CompositeDecisionMakingSystem compositeDecisionMakingSystem;
	private DynamicScripting dynamicScripting;
	private List<Attribute> attributes;
	private Map<String, Action> actionsByName;
	private Character player;
	private Character computer;
	private final Float maxPosition = 50f;
	
	public void init() {
		// inicializar variáveis do jogo
		// criar jogadores
		// criar um rule based system
		// carregar regras
		if(decisionMakingSystem == null) {
            createAttributes();
            createActions();
		    setPlayer(createCharacter("Jogador", Character.PLAYER, attributes));
	        setComputer(createCharacter("Computador", Character.COMPUTER, attributes));		    
	        createDynamicScripting();
	        createCompositeDecisionMakingSystem();
    		setGameActive(true);
    		newGame();
		}
	}
	
	private ProfileGroupElement createProfile() {
	    ProfileGroupElement profile = new ProfileGroupElement();
	    /*
	    ProfileGroupElement movProfile = new ProfileGroupElement();
	    movProfile.setKey("Movement");
	    movProfile.addProfileElement(new ProfileSimpleElement("AFA"));
	    movProfile.addProfileElement(new ProfileSimpleElement("APR"));
	    movProfile.addProfileElement(new ProfileSimpleElement("COR"));
	    profile.addProfileElement(movProfile);*/
	    ProfileGroupElement physicalProfile = new ProfileGroupElement();
	    physicalProfile.setKey("Physical");
	    physicalProfile.addProfileElement(new ProfileSimpleElement("ATF"));
	    physicalProfile.addProfileElement(new ProfileSimpleElement("AFC"));
	    profile.addProfileElement(physicalProfile);
	    ProfileGroupElement rangedProfile = new ProfileGroupElement();
	    rangedProfile.setKey("Ranged");
	    rangedProfile.addProfileElement(new ProfileSimpleElement("ATL"));
	    rangedProfile.addProfileElement(new ProfileSimpleElement("ALC"));
	    profile.addProfileElement(rangedProfile);
        ProfileGroupElement specialProfile = new ProfileGroupElement();
        specialProfile.setKey("Special");
        specialProfile.addProfileElement(new ProfileSimpleElement("PXM"));
        profile.addProfileElement(specialProfile);
        ProfileGroupElement debuffProfile = new ProfileGroupElement();
        debuffProfile.setKey("Debuff");
        debuffProfile.addProfileElement(new ProfileSimpleElement("PRE"));
        profile.addProfileElement(debuffProfile);  
        ProfileGroupElement buffProfile = new ProfileGroupElement();
        buffProfile.setKey("Buff");
        buffProfile.addProfileElement(new ProfileSimpleElement("FOC"));
        profile.addProfileElement(buffProfile);
        ProfileGroupElement healProfile = new ProfileGroupElement();
        healProfile.setKey("Heal");
        healProfile.addProfileElement(new ProfileSimpleElement("REC"));
        profile.addProfileElement(healProfile);        
        ProfileGroupElement defenseProfile = new ProfileGroupElement();
        defenseProfile.setKey("Defense");
        defenseProfile.addProfileElement(new ProfileSimpleElement("DEF"));
        defenseProfile.addProfileElement(new ProfileSimpleElement("DFC"));
        defenseProfile.addProfileElement(new ProfileSimpleElement("AVA"));
        profile.addProfileElement(defenseProfile);         
        profile.addProfileElement(new ProfileSimpleElement("CON"));
        profile.addProfileElement(new ProfileSimpleElement("ENE"));
        profile.addProfileElement(new ProfileSimpleElement("LIM"));
        return profile;
	}
	
	private void createCompositeDecisionMakingSystem() {
        compositeDecisionMakingSystem = new CompositeDecisionMakingSystem("composite", getPlayer(), getComputer(), this);
        compositeDecisionMakingSystem.setWatchVariable("SAU");
        try {
            compositeDecisionMakingSystem.setChangeCondition("((NCSAUANT - CSAU) - (NPSAUANT - PSAU) ) > 100");
            compositeDecisionMakingSystem.setAdjustExpression("((NPSAUANT - PSAU) - (NCSAUANT - CSAU))/NTRN");
            compositeDecisionMakingSystem.setEvaluationExpression("(NPSAUANT - PSAU) - (NCSAUANT - CSAU)");
        }
        catch(Exception e) {
            e.printStackTrace();
            setStatus(e.getMessage());
        }
        DecisionMakingSystem rangedRuleBasedSyste = new RuleBasedSystem("rangedRules", getPlayer(), getComputer(), createRangedRules(), this);
        DecisionMakingSystem meleeRuleBasedSyste = new RuleBasedSystem("meleeRules", getPlayer(), getComputer(), createRecklessPhysicalRules(), this);
        DecisionMakingSystem defensiveRuleBasedSyste = new RuleBasedSystem("defensiveRules", getPlayer(), getComputer(), createDefensiveRules(), this);
        compositeDecisionMakingSystem.addDecisionMakingSystem(rangedRuleBasedSyste, 1000);
        compositeDecisionMakingSystem.addDecisionMakingSystem(meleeRuleBasedSyste, 999);
        compositeDecisionMakingSystem.addDecisionMakingSystem(defensiveRuleBasedSyste, 998);
        compositeDecisionMakingSystem.getPlayerProfile().setProfile(createProfile());
	}
	
    private void createDynamicScripting() {
        dynamicScripting = new DynamicScripting("dynamicScripting", getPlayer(), getComputer());
        dynamicScripting.setGameController(this);
        dynamicScripting.setRules(createRuleBase());
        try {
            dynamicScripting.setAdjustExpression("((1000 - PSAU) - (1000 - CSAU))/NTRN");
        }
        catch(Exception e) {
            e.printStackTrace();
            setStatus(e.getMessage());
        }
    }	
	
	public void newGame() {
	    writeLogFile("C:\\gameLog.txt", "New Game");
        setPlayer(createCharacter("Jogador", Character.PLAYER, attributes));
        setComputer(createCharacter("Computador", Character.COMPUTER, attributes));
		//decisionMakingSystem = new RuleBasedSystem("simpleRules", getPlayer(), getComputer(), createDefensiveRules(), this);
        decisionMakingSystem = compositeDecisionMakingSystem;
        decisionMakingSystem.setNonPlayerCharacter(getComputer());
        decisionMakingSystem.setPlayerCharacter(getPlayer());
        decisionMakingSystem.begin();
        /*
        if(playerDecisionMakingSystem == null || playerDecisionMakingSystem.getIdentifier().equals("playerStrategicRanged")) {
            playerDecisionMakingSystem = new RuleBasedSystem("playerMelee", getComputer(), getPlayer(), createRules(), this);
        }
        else {
            playerDecisionMakingSystem = new RuleBasedSystem("playerStrategicRanged", getComputer(), getPlayer(), createStrategicRangedRules(), this);
        }*/
        playerDecisionMakingSystem = dynamicScripting;
        decisionMakingSystem.setNonPlayerCharacter(getComputer());
        decisionMakingSystem.setPlayerCharacter(getPlayer());
		playerDecisionMakingSystem.begin();
		setGameActive(true);
		setStatus("");
	}
	
    private void writeLogFile(String fileName, String line) {
        try {
            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }	
	
	public void turn() {
	    setAction(playerDecisionMakingSystem.evaluate());
	    compositeDecisionMakingSystem.getPlayerProfile().updateProfile(getAction());
        // Avalia acao do computador
        String action = decisionMakingSystem.evaluate();
	    // Executa acao do jogador
	    executeAction(getAction(), getPlayer(), action, getComputer());
	    // Executa acao do computador
	    executeAction(action, getComputer(), getAction(), getPlayer());
        // Atualiza acao anterior
	    updateLastAction(getPlayer(), getAction());	    
	    updateLastAction(getComputer(), action);
	    
	    writeLogFile("C:\\gameLog.txt", compositeDecisionMakingSystem.getActiveDecisionMakingSystem().getIdentifier() + " - " + compositeDecisionMakingSystem.getEvaluationExpression().evaluate().toString());
	    
	    if((getPlayerHP() <= new Float(0)) || getComputerHP() <= new Float(0) ) {
	        setGameActive(false);
	        decisionMakingSystem.end();
	        playerDecisionMakingSystem.end();
	        if(getPlayerHP() > getComputerHP())
	            setStatus("Jogador venceu");
	        else
	            setStatus("Computador venceu");
	    }
	    else
	        setStatus("Acao do jogador: " + getAction() + " - Acao do computador: " + action);
	}
	
	private void updateLastAction(Character actor, String lastAction) {
	    Float lastActionId = 0f;
	    if(lastAction != null) {
	        lastActionId = new Float(actionsByName.get(lastAction).getId());
	    }
	    actor.setAttribute("UAC", lastActionId);
	}
	
	private void executeAction(String actorAction, Character actor, String adversaryAction, Character adversary) {
	    
	    Float absorption = adversary.getAttributeValue("DNA");
        Boolean forceFieldActive = false;
        Boolean absorbing = false;
        
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
    	    if(avoidanceRoll > avoidance) {
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
                        if(adversary.getAttributeValue("POS") > maxPosition) {
                            adversary.setAttribute("POS", maxPosition);
                        }
                    }
    	        }
    	    }
	    }
    	    
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
	
	private Character createCharacter(String nome, String dono, List<Attribute> atributos) {
		Character personagem = new Character(nome, dono, atributos);
		return personagem;
	}
	
	private void createAttributes() {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("SAU", "Saude", new Float(1000)));
		attributes.add(new Attribute("FOR","Forca", new Float(100f)));
		attributes.add(new Attribute("AGI","Agilidade", new Float(100f)));
		attributes.add(new Attribute("HAB","Habilidade", new Float(100f)));
		attributes.add(new Attribute("PDF","Poder de Fogo", new Float(100f)));
		attributes.add(new Attribute("DEF","Defesa", new Float(100f)));
		attributes.add(new Attribute("ENE","Energia", new Float(3)));
		attributes.add(new Attribute("POS","Posicao", new Float(1)));
		attributes.add(new Attribute("UAC","Ultima Acao", new Float(0)));
		attributes.add(new Attribute("DNF","Dano Fisico", new Float(50)));
		attributes.add(new Attribute("DNL","Dano Longa Distancia", new Float(50)));
		attributes.add(new Attribute("ESQ","Esquiva", new Float(10)));
		attributes.add(new Attribute("PRE","Precisao", new Float(100f)));
		attributes.add(new Attribute("DNA","Dano Absorvido", new Float(30)));
		attributes.add(new Attribute("COO","Concentrado", new Float(0)));
		attributes.add(new Attribute("CAO","Carregado", new Float(0)));
		attributes.add(new Attribute("REO","Recuperando", new Float(0)));
		attributes.add(new Attribute("CMO","Campo Magnetico", new Float(0)));
		attributes.add(new Attribute("FOO","Focado", new Float(0)));
		attributes.add(new Attribute("ELO","Eletrificado", new Float(0)));
		attributes.add(new Attribute("PRO","Preso", new Float(0)));
		attributes.add(new Attribute("ATO","Atordoado", new Float(0)));
		attributes.add(new Attribute("INO","Instavel", new Float(0)));
		setAttributes(attributes);
	}
	
	private List<Rule> createRules() {
		List<Rule> rules = new ArrayList<Rule>();
		try {
			Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5)"), "ABS"));			
			rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!CPRO)"), "APR"));
			rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= CENE) & (!CPRO)"), "COR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CENE >= 6) & (CPRO)"), "LIM"));			
			rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (CCOO < 5)"), "CON"));
			rules.add(new Rule(parser.parse("CCOO >= 5"), "AFC"));
			rules.add(new Rule(parser.parse("1"), "ENE"));
		}
		catch(Exception e) {
			setStatus("Erro de parsing: " + e.getMessage());
		}		
		return rules;
	}
	
    private List<Rule> createRangedRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 1.5) & (!PPRO)"), "AFA"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PPRO) & (CENE >= 6)"), "PRE"));
            rules.add(new Rule(parser.parse("(CENE >= 6) & (CPRO)"), "LIM"));
            rules.add(new Rule(parser.parse("CCOO < 5"), "CON"));
            rules.add(new Rule(parser.parse("CCOO >= 5"), "ALC"));
            rules.add(new Rule(parser.parse("!CCOO"), "ATL"));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }
    
    private List<Rule> createStrategicRangedRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE > 0) & ((CCOO < 5) | (!PENE))"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (PENE < 3) & (PSAU < 90)"), "ALC"));
            rules.add(new Rule(parser.parse("!CCAO  & (CCOO >= 5) & (CENE >= 5)"), "CAR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ATL"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PPRO) & (CENE >= 6)"), "PRE"));
            rules.add(new Rule(parser.parse("1"), "CON"));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }
    
    private List<Rule> createRecklessPhysicalRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!CPRO)"), "APR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= CENE) & (!CPRO)"), "COR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CENE >= 6) & (CPRO)"), "LIM"));         
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!CCOO)"), "CON"));
            rules.add(new Rule(parser.parse("CCOO"), "AFC"));
            rules.add(new Rule(parser.parse("1"), "ENE"));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }     
    
    private List<Rule> createDefensiveRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO = 1) & (CENE > 5)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE >= 3)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE < 3)"), "DEF"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & CENE"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & CCOO"), "AFC"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ALC"));
            rules.add(new Rule(parser.parse("1"), "CON"));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }
    
    private List<Rule> createRuleBase() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5)"), "ABS", 1L, 1, 100f));           
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!CPRO)"), "APR", 2L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= CENE) & (!CPRO)"), "COR", 3L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CENE >= 6) & (CPRO)"), "LIM", 4L, 4, 100f));          
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (CCOO < 5)"), "CON", 5L, 5, 100f));
            rules.add(new Rule(parser.parse("CCOO >= 5"), "AFC", 6L, 6, 100f));
            rules.add(new Rule(parser.parse("1"), "ENE", 7L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 1.5) & (!PPRO)"), "AFA", 8L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PPRO) & (CENE >= 6)"), "PRE", 9L, 2, 100f));
            rules.add(new Rule(parser.parse("(CENE >= 6) & (CPRO)"), "LIM", 10L, 3, 100f));
            rules.add(new Rule(parser.parse("CCOO < 5"), "CON", 11L, 4, 100f));
            rules.add(new Rule(parser.parse("CCOO >= 5"), "ALC", 12L, 5, 100f));
            rules.add(new Rule(parser.parse("!CCOO"), "ATL", 13L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE > 0) & ((CCOO < 5) | (!PENE))"), "ABS", 14L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (PENE < 3) & (PSAU < 90)"), "ALC", 15L, 2, 100f));
            rules.add(new Rule(parser.parse("!CCAO  & (CCOO >= 5) & (CENE >= 5)"), "CAR", 16L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ATL", 17L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PPRO) & (CENE >= 6)"), "PRE", 18L, 5, 100f));
            rules.add(new Rule(parser.parse("1"), "CON", 19L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!CPRO)"), "APR", 20L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= CENE) & (!CPRO)"), "COR", 21L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CENE >= 6) & (CPRO)"), "LIM", 22L, 3, 100f));         
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!CCOO)"), "CON", 23L, 4, 100f));
            rules.add(new Rule(parser.parse("CCOO"), "AFC", 24L, 5, 100f));
            rules.add(new Rule(parser.parse("1"), "ENE", 25L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO = 1) & (CENE > 5)"), "ABS", 26L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE >= 3)"), "ABS", 27L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE < 3)"), "DEF", 28L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & CENE"), "ABS", 29L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & CCOO"), "AFC", 30L, 5, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ALC", 31L, 6, 100f));
            rules.add(new Rule(parser.parse("1"), "CON", 32L, 7, 100f));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }        
	
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
	
	public Collection<SelectItem> getActionsAsSelectItem() {
	    Collection<SelectItem> actions = new ArrayList<SelectItem>();
	    for(Action action: actionsByName.values()) {
	        if(testAction(action.getName(), getPlayer(), getComputer())) {
	            actions.add(new SelectItem(action.getName(), action.getDescription()));
	        }
	    }
	    return actions;
	}
	
	public Float getPlayerHP() {
	    return getPlayer().getAttributeValue("SAU");
	}
	
    public Float getComputerHP() {
        return getComputer().getAttributeValue("SAU");
    }	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DecisionMakingSystem getDecisionMakingSystem() {
		return decisionMakingSystem;
	}

	public void setDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem) {
		this.decisionMakingSystem = decisionMakingSystem;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Boolean testAction(String action, Character actor, Character adversary) {
	    Float distance = actor.getAttributeValue("POS") + adversary.getAttributeValue("POS");
	    // Se o personagem esta atordoado nao pode executar acoes
	    if(actor.isTrueAttribute("ATO")) {
	        return false;
	    }
	    // Verifica efeito das acoes
	    if(action.equals("AFA")) {
	        if(actor.getAttributeValue("POS") >= maxPosition) {
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
            if(actor.getAttributeValue("ENE") >= 6f && actor.getAttributeValue("POS") < maxPosition) {
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getGameActive() {
        return gameActive;
    }

    public void setGameActive(Boolean gameActive) {
        this.gameActive = gameActive;
    }

    public Character getPlayer() {
        return player;
    }

    public void setPlayer(Character player) {
        this.player = player;
    }

    public Character getComputer() {
        return computer;
    }

    public void setComputer(Character computer) {
        this.computer = computer;
    }

    @Override
    public Float solveVariable(String variableName, String variableOwner) {
        //Nao existem variaveis gerenciadas pelo controller ainda
        return null;
    }

}
