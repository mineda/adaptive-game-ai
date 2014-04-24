package br.com.mineda.gameExperiment1.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.mineda.gameExperiment1.common.Action;
import br.com.mineda.gameExperiment1.common.BotBattle;
import br.com.mineda.gameExperiment1.common.BotBattleWorld;
import br.com.mineda.gameExperiment1.common.Character;
import br.com.mineda.gameExperiment1.common.CompositeDecisionMakingSystem2;
import br.com.mineda.gameExperiment1.common.DecisionMakingSystem;
import br.com.mineda.gameExperiment1.common.DynamicScripting2;
import br.com.mineda.gameExperiment1.common.Parser;
import br.com.mineda.gameExperiment1.common.PlayerProfile;
import br.com.mineda.gameExperiment1.common.ProfileGroupElement;
import br.com.mineda.gameExperiment1.common.ProfileSimpleElement;
import br.com.mineda.gameExperiment1.common.RankedDecisionMakingSystem;
import br.com.mineda.gameExperiment1.common.Rule;
import br.com.mineda.gameExperiment1.common.RuleBasedSystem2;

public class BotBattleController  implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -349693468422752442L;
    
    // Mundo do jogo
    BotBattleWorld world = new BotBattleWorld();
    // Jogo e suas regras
    BotBattle botBattle = new BotBattle(world);
    
    private String status;
    private Boolean gameActive;
    private String playerAction;
    private String computerAction;
    private DecisionMakingSystem decisionMakingSystem;
    private CompositeDecisionMakingSystem2 compositeDecisionMakingSystem = null;
    private DynamicScripting2 dynamicScripting = null;  
    
    private Integer playerVictoriesWithoutChange = 0;
    private Integer playerVictoriesWithChange = 0;
    private Integer playerDefeatsWithoutChange = 0;
    private Integer playerDefeatsWithChange = 0;
    private Boolean change = false;
    private Integer playerVictoriesIn10 = 0;
    private Boolean playerVictory = false;
    private Integer contadorJogos = 0;

    /**
     * Inicializa o controller
     */
    public void init() {
        if(dynamicScripting == null) {
            botBattle.reset();
            createDynamicScripting();
            createCompositeDecisionMakingSystem();
            setGameActive(true);
            newGame();
            // Inicializa logs de experimento
            writeLogFile("C:\\BattleBotData\\experimentResults.txt", "New Experiment");
            writeLogFile("C:\\BattleBotData\\experimentStatistics.txt", "New Experiment");
        }
    }
    
    /**
     * Inicia um novo jogo
     */
    public void newGame() {
        setGameActive(true);
        botBattle.reset();
        decisionMakingSystem = compositeDecisionMakingSystem;
        decisionMakingSystem.begin();
        playerAction="";
        computerAction="";
        setStatus("");
        // Variaveis de controle
        writeLogFile("C:\\BattleBotData\\gameLog.txt", "New Game");
        writeLogFile("C:\\BattleBotData\\gameActionLog.txt", "New Game");
        change = false;
    }
    
    /**
     * Escreve um registro no arquivo de log
     * @param fileName Nome do arquivo
     * @param line Linha a ser escrita
     */
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
    
    /**
     * Executa um turno do jogo
     */
    public void turn() {
        if(playerAction == null) {
            playerAction = "";
        }
        // Atualiza profile
        compositeDecisionMakingSystem.getPlayerProfile().updateProfile(playerAction);
        // Verifica mudanca
        if(compositeDecisionMakingSystem.getChangeCondition().isTrue()) {
            change = true;
        }
        // Avalia acao do computador
        computerAction = decisionMakingSystem.evaluate();
        // Carrega acao do jogador
        botBattle.setPlayerCharacterCurrentAction(playerAction);
        // Carrega acao do computador
        botBattle.setNonPlayerCharacterCurrentAction(computerAction);
        // Executa turno
        botBattle.turn();
        
        setStatus("Player 1: " + playerAction + " - Player 2: " + computerAction);
        writeLogFile("C:\\BattleBotData\\gameActionLog.txt", getStatus());
        
        Float performance = compositeDecisionMakingSystem.getEvaluationExpression().evaluate();
        String mensagem = compositeDecisionMakingSystem.getPlayerProfile().getName() + " - " + compositeDecisionMakingSystem.getActiveDecisionMakingSystem().getIdentifier() + " - " + performance.toString();
        writeLogFile("C:\\BattleBotData\\gameLog.txt", mensagem);
        
        if(botBattle.getEndGame()) {
            setGameActive(false);
            decisionMakingSystem.end();
            if(botBattle.getPlayerHp() > botBattle.getComputerHp()) {
                setStatus("Jogador venceu");
                playerAction = "VIT";
                computerAction = "DER";
                playerVictory = true;
            }
            else {
                setStatus("Computador venceu");
                playerAction = "DER";
                computerAction = "VIT";
                playerVictory = false;
            }
            writeLogFile("C:\\BattleBotData\\gameLog.txt", getStatus());
            writeLogFile("C:\\BattleBotData\\gameActionLog.txt", getStatus());
            
            relatorioJogo();
        }
        else
            setStatus("Acao do jogador: " + playerAction + " - Acao do computador: " + computerAction);
    }
    
    private void relatorioJogo() {
        contadorJogos++;
        // Inicializa logs de experimento
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "New Experiment");
        writeLogFile("C:\\BattleBotData\\experimentStatistics.txt", "New Experiment");
        // Executa jogos de acordo com configura
        if(playerVictory) {
            playerVictoriesIn10++;
            if(change) {
                playerVictoriesWithChange++;
            }
            else {
                playerVictoriesWithoutChange++;
            }
        }
        else {
            if(change) {
                playerDefeatsWithChange++;
            }
            else {
                playerDefeatsWithoutChange++;
            }
        }
        // A cada 10 jogos atualiza estatistica de vitorias
        if((contadorJogos)%10 == 0) {
            writeLogFile("C:\\BattleBotData\\experimentStatistics.txt", "Player victories: " + playerVictoriesIn10);
            playerVictoriesIn10 = 0;
        }

        // Grava resultados do experimento
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player victories with change: " + playerVictoriesWithChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player victories without change: " + playerVictoriesWithoutChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player defeats with change: " + playerDefeatsWithChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player defeats without change: " + playerDefeatsWithoutChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player profiles");
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Profile: {Ataque fisico, Ataque de longa distancia, Especial, Efeito adverso, Aperfeicoamento, Cura, Defesa, Concentracao, Energia, Limpeza}");
        for(PlayerProfile profile: compositeDecisionMakingSystem.getPreviousProfiles()) {
            String statistics = "{ ";
            for(Integer j=0; j < profile.getStatistics().length; j++) {
                statistics += profile.getStatistics()[j].toString() + " ";
            }
            statistics += "}";
            writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player profile " + profile.getName() + ": " + statistics);
            String dmsWeigths = "{";
            for(RankedDecisionMakingSystem dms: profile.getDecisionMakingSystems()) {
                dmsWeigths += dms.getDecisionMakingSystem().getIdentifier() + "(" + dms.getRanking() + ") ";
            }
            dmsWeigths += "}";
            writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Weigths: " + dmsWeigths);
        }
    }
    
    /**
     * Retorna uma lista de acoes para escolha
     * @return Lista de acoes para escolha
     */
    public Collection<SelectItem> getActionsAsSelectItem() {
        Collection<SelectItem> actions = new ArrayList<SelectItem>();
        // Para cada acao
        for(Action action: botBattle.getActions()) {
            // Testa se acao pode ser executada
            if(botBattle.testAction(action.getName(), world.getPlayerCharacter().getName())) {
                actions.add(new SelectItem(action.getName(), action.getDescription()));
            }
        }
        return actions;
    }    
    
    public Float getPlayerPosition() {
        return world.getPlayerCharacter().getAttributeValue("POS");
    }
    
    public Float getComputerPosition() {
        return world.getNonPlayerCharacter().getAttributeValue("POS");
    }
    
    public Float getPlayerHp() {
        return botBattle.getPlayerHp();
    }
    
    public Float getComputerHp() {
        return botBattle.getComputerHp();
    }    
    
    /**
     * Cria um profile para uso em Composite Decision Making System
     * @return Profile criado
     */
    private ProfileGroupElement createProfile() {
        ProfileGroupElement profile = new ProfileGroupElement();
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
        compositeDecisionMakingSystem = new CompositeDecisionMakingSystem2("composite", botBattle);
        botBattle.addWatchVariable("SAU", "SAUANT");
        try {
            compositeDecisionMakingSystem.setChangeCondition("((CSAUANT - CSAU) - (PSAUANT - PSAU) ) > 100");
            compositeDecisionMakingSystem.setAdjustExpression("((PSAUANT - PSAU) - (CSAUANT - CSAU))/NLPT");
            compositeDecisionMakingSystem.setEvaluationExpression("(PSAUANT - PSAU) - (CSAUANT - CSAU)");
        }
        catch(Exception e) {
            e.printStackTrace();
            setStatus(e.getMessage());
        }
        DecisionMakingSystem rangedRuleBasedSystem = new RuleBasedSystem2("rangedRules", botBattle, createRangedRules());
        rangedRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        DecisionMakingSystem recklessMeleeRuleBasedSystem = new RuleBasedSystem2("recklessMeleeRules", botBattle, createRecklessPhysicalRules());
        recklessMeleeRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        DecisionMakingSystem defensiveRuleBasedSystem = new RuleBasedSystem2("defensiveRules", botBattle, createDefensiveRules());
        defensiveRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        DecisionMakingSystem strategicRangedRuleBasedSystem = new RuleBasedSystem2("strategicRangedRules", botBattle, createStrategicRangedRulesComputer());
        strategicRangedRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        DecisionMakingSystem meleeRuleBasedSystem = new RuleBasedSystem2("meleeRules", botBattle, createPhysicalRulesComputer());
        meleeRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        compositeDecisionMakingSystem.addDecisionMakingSystem(rangedRuleBasedSystem, 1000);
        compositeDecisionMakingSystem.addDecisionMakingSystem(recklessMeleeRuleBasedSystem, 999);
        compositeDecisionMakingSystem.addDecisionMakingSystem(defensiveRuleBasedSystem, 998);
        compositeDecisionMakingSystem.addDecisionMakingSystem(strategicRangedRuleBasedSystem, 997);
        compositeDecisionMakingSystem.addDecisionMakingSystem(meleeRuleBasedSystem, 996);
        compositeDecisionMakingSystem.getPlayerProfile().setProfile(createProfile());

    }
    
    private void createDynamicScripting() {
        dynamicScripting = new DynamicScripting2("dynamicScripting", botBattle);
        dynamicScripting.setRules(createRuleBase());
        dynamicScripting.setCharacter(world.getPlayerCharacter().getName());
        try {
            Parser parser = new Parser();
            dynamicScripting.addDefaultRule(new Rule(parser.parse("1"), "ATF", 100L, 10, 100f));
            dynamicScripting.addDefaultRule(new Rule(parser.parse("1"), "ATL", 101L, 10, 100f));
            dynamicScripting.setAdjustExpression("((1000 - CSAU) - (1000 - PSAU))/NTRN");
        }
        catch(Exception e) {
            e.printStackTrace();
            setStatus(e.getMessage());
        }
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
            rules.add(new Rule(parser.parse("1"), "ATF"));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }

    /**
     * Cria regras de ataque fisico para computador
     * @return Lista de regras
     */
    private List<Rule> createPhysicalRulesComputer() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (NRAND > 0.3)"), "ABS"));           
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
    
    /**
     * Cria regras de ataque de longa distancia estrategico para computador
     * @return Lista de regras
     */
    private List<Rule> createStrategicRangedRulesComputer() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE > 0) & ((CCOO < 5) | (!PENE)) & (NRAND > 0.1)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (PENE < 3) & (PSAU < 90)"), "ALC"));
            rules.add(new Rule(parser.parse("!PCAO  & (PCOO >= 5) & (CENE >= 5)"), "CAR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ATL"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PPRO) & (CENE >= 6)"), "PRE"));
            rules.add(new Rule(parser.parse("CCOO < 5"), "CON"));
            rules.add(new Rule(parser.parse("1"), "ATF"));            
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
            rules.add(new Rule(parser.parse("1"), "ATL"));            
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
            rules.add(new Rule(parser.parse("1"), "ATF"));
            rules.add(new Rule(parser.parse("1"), "ATL"));            
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

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO = 1) & (CENE > 5)"), "ABS", 25L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE >= 3)"), "ABS", 26L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE < 3)"), "DEF", 27L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & CENE"), "ABS", 28L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & CCOO"), "AFC", 29L, 5, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ALC", 30L, 6, 100f));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }        

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getGameActive() {
        return gameActive;
    }

    public void setGameActive(Boolean gameActive) {
        this.gameActive = gameActive;
    }
    
    public Character getPlayer() {
        return world.getPlayerCharacter();
    }
    
    public Character getComputer() {
        return world.getNonPlayerCharacter();
    }

    /**
     * @return the playerAction
     */
    public String getPlayerAction() {
        return playerAction;
    }

    /**
     * @param playerAction the playerAction to set
     */
    public void setPlayerAction(String playerAction) {
        this.playerAction = playerAction;
    }

    /**
     * @return the computerAction
     */
    public String getComputerAction() {
        return computerAction;
    }

    /**
     * @param computerAction the computerAction to set
     */
    public void setComputerAction(String computerAction) {
        this.computerAction = computerAction;
    }
    
}
