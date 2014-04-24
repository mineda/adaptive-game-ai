package br.com.mineda.gameExperiment1.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.mineda.gameExperiment1.common.Action;
import br.com.mineda.gameExperiment1.common.BotBattle;
import br.com.mineda.gameExperiment1.common.BotBattleWorld;
import br.com.mineda.gameExperiment1.common.Character;
import br.com.mineda.gameExperiment1.common.Clausule;
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

public class ExperimentoController  implements Serializable {

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
    private DecisionMakingSystem decisionMakingSystem;
    private DecisionMakingSystem playerDecisionMakingSystem;
    private CompositeDecisionMakingSystem2 compositeDecisionMakingSystem = null;
    private CompositeDecisionMakingSystem2 compositeDecisionMakingSystem2 = null;
    private DynamicScripting2 dynamicScripting = null;  
    private DynamicScripting2 dynamicScriptingComputer = null;
    private Integer experimentNumber = 0;
    private Integer rbsNumber = 0;
    private Integer maxTurns = 1000;
    private Integer games = 1000;
    private Clausule evaluationFunction;
    private Integer playerVictoriesWithoutChange;
    private Integer playerVictoriesWithChange;
    private Integer playerDefeatsWithoutChange;
    private Integer playerDefeatsWithChange;
    private Boolean change;
    private Integer playerVictoriesIn10;
    private Boolean playerVictory;

    /**
     * Inicializa o controller
     */
    public void init() {
        if(dynamicScripting == null) {
            setStatus("");
            botBattle.reset();
            createDynamicScripting();
            createDynamicScriptingComputer();
            createCompositeDecisionMakingSystem();
            createCompositeDecisionMakingSystem2();
            setGameActive(true);
        }
    }
    
    /**
     * Inicia um novo jogo
     */
    public void newGame() throws Exception{
        writeLogFile("C:\\BattleBotData\\gameLog.txt", "New Game");
        writeLogFile("C:\\BattleBotData\\gameActionLog.txt", "New Game");
        setGameActive(true);
        botBattle.reset();
        // Aloca os STDs de acordo com o experimento
        if(experimentNumber == 0) {
            decisionMakingSystem = compositeDecisionMakingSystem;
            if(rbsNumber == 0) {
                playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
                rbsNumber = 1;
            }
            else {
                playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
                rbsNumber = 0;
            }
        }
        else if(experimentNumber == 1) {
            decisionMakingSystem = dynamicScriptingComputer;
            if(rbsNumber == 0) {
                playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
                rbsNumber = 1;
            }
            else {
                playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
                rbsNumber = 0;
            }
        }
        else if (experimentNumber == 2) {
            decisionMakingSystem = compositeDecisionMakingSystem;
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 3) {
            decisionMakingSystem = new RuleBasedSystem2("rangedRules", botBattle, createRangedRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
        }
        else if (experimentNumber == 4) {
            decisionMakingSystem = new RuleBasedSystem2("meleeRules", botBattle, createRecklessPhysicalRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
        }
        else if (experimentNumber == 5) {
            decisionMakingSystem = new RuleBasedSystem2("defensiveRules", botBattle, createDefensiveRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
        }
        else if (experimentNumber == 6) {
            decisionMakingSystem = new RuleBasedSystem2("rangedRules", botBattle, createRangedRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
        }
        else if (experimentNumber == 7) {
            decisionMakingSystem = new RuleBasedSystem2("meleeRules", botBattle, createRecklessPhysicalRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
        }
        else if (experimentNumber == 8) {
            decisionMakingSystem = new RuleBasedSystem2("defensiveRules", botBattle, createDefensiveRules());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
        }
        else if (experimentNumber == 9) {
            decisionMakingSystem = new RuleBasedSystem2("rangedRules", botBattle, createRangedRules());
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 10) {
            decisionMakingSystem = new RuleBasedSystem2("meleeRules", botBattle, createRecklessPhysicalRules());
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 11) {
            decisionMakingSystem = new RuleBasedSystem2("defensiveRules", botBattle, createDefensiveRules());
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 12) {
            decisionMakingSystem = compositeDecisionMakingSystem2;
            playerDecisionMakingSystem = dynamicScripting;            
        }
        else if (experimentNumber == 13) {
            decisionMakingSystem = new RuleBasedSystem2("meleePlayer", botBattle, createPhysicalRulesComputer());
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 14) {
            decisionMakingSystem = new RuleBasedSystem2("rangedPlayer", botBattle, createStrategicRangedRulesComputer());
            playerDecisionMakingSystem = dynamicScripting;
        }
        else if (experimentNumber == 15) {
            decisionMakingSystem = new RuleBasedSystem2("meleePlayer", botBattle, createPhysicalRulesComputer());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
        }
        else if (experimentNumber == 16) {
            decisionMakingSystem = new RuleBasedSystem2("rangedPlayer", botBattle, createStrategicRangedRulesComputer());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerMelee", botBattle, createPhysicalRules());
        }
        else if (experimentNumber == 17) {
            decisionMakingSystem = new RuleBasedSystem2("meleePlayer", botBattle, createPhysicalRulesComputer());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
        }
        else {
            decisionMakingSystem = new RuleBasedSystem2("rangedPlayer", botBattle, createStrategicRangedRulesComputer());
            playerDecisionMakingSystem = new RuleBasedSystem2("playerStrategicRanged", botBattle, createStrategicRangedRules());
        }
        
        if((decisionMakingSystem != compositeDecisionMakingSystem) && (decisionMakingSystem != compositeDecisionMakingSystem2)) {
            Parser parser = new Parser();
            evaluationFunction = parser.parse("(1000 - PSAU) - (1000 - CSAU)");
            evaluationFunction.setSolver(botBattle);
        }
        decisionMakingSystem.setCharacter(world.getNonPlayerCharacter().getName());
        decisionMakingSystem.begin();
        playerDecisionMakingSystem.setCharacter(world.getPlayerCharacter().getName());
        playerDecisionMakingSystem.begin();
        setStatus("");
        Integer turns = 0;
        // Executa o jogo ate que ele finalize ou o numero maximo de turnos seja atingido
        while(gameActive && turns++ < maxTurns) {
            turn();
        }
    }
    
    /**
     * Novo experimento
     */
    public void newExperiment() throws Exception{
        // Inicializa variaves de experimento
        playerVictoriesWithoutChange = 0;
        playerVictoriesWithChange = 0;
        playerDefeatsWithoutChange = 0;
        playerDefeatsWithChange = 0;
        playerVictoriesIn10 = 0;
        // Inicializa logs de experimento
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "New Experiment");
        writeLogFile("C:\\BattleBotData\\experimentStatistics.txt", "New Experiment");
        // Executa jogos de acordo com configura
        for(Integer i=0; i < games; i++) {
            change = false;
            newGame();
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
            if((i+1)%10 == 0) {
                writeLogFile("C:\\BattleBotData\\experimentStatistics.txt", "Player victories: " + playerVictoriesIn10);
                playerVictoriesIn10 = 0;
            }
        }
        // Grava resultados do experimento
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player victories with change: " + playerVictoriesWithChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player victories without change: " + playerVictoriesWithoutChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player defeats with change: " + playerDefeatsWithChange);
        writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player defeats without change: " + playerDefeatsWithoutChange);
        // Se usando CDSMS
        
        if((decisionMakingSystem == compositeDecisionMakingSystem) || (decisionMakingSystem == compositeDecisionMakingSystem2)) {
            writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Player profiles");
            writeLogFile("C:\\BattleBotData\\experimentResults.txt", "Profile: {Ataque fisico, Ataque de longa distancia, Especial, Efeito adverso, Aperfeicoamento, Cura, Defesa, Concentracao, Energia, Limpeza}");
            for(PlayerProfile profile: ((CompositeDecisionMakingSystem2)decisionMakingSystem).getPreviousProfiles()) {
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
        setStatus("Experimento completo");
    }
    
    /**
     * Reseta experimento
     */
    public void reset() {
        // Renomeia arquivos
        resetFile("C:\\BattleBotData\\gameLog.txt");
        resetFile("C:\\BattleBotData\\gameActionLog.txt");
        resetFile("C:\\BattleBotData\\experimentResults.txt");
        resetFile("C:\\BattleBotData\\experimentStatistics.txt");
        resetFile("C:\\BattleBotData\\gameProcessingTimeLog.txt");
        // Recria STDs adaptativos
        createDynamicScripting();
        createDynamicScriptingComputer();
        createCompositeDecisionMakingSystem();
        createCompositeDecisionMakingSystem2();
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
     * Renomeia arquivo
     * @param fileName Arquivo
     */
    private void resetFile(String fileName) {
        String newFileName = fileName.substring(0, fileName.indexOf(".")) + (new Date()).getTime() + fileName.substring(fileName.indexOf("."));
        File file = new File(fileName);
        File newFile = new File(newFileName);
        file.renameTo(newFile);
    }
    
    /**
     * Executa um turno do jogo
     */
    public void turn() {
        Long tempoJogador = System.nanoTime();
        String playerAction = playerDecisionMakingSystem.evaluate();
        tempoJogador = System.nanoTime() - tempoJogador;
        Long tempoComputador = System.nanoTime(); 
        Boolean turnChange = false;
        // Se usando CDSMS
        if((decisionMakingSystem == compositeDecisionMakingSystem) || (decisionMakingSystem == compositeDecisionMakingSystem2)) {
            // Atualiza profile
            ((CompositeDecisionMakingSystem2)decisionMakingSystem).getPlayerProfile().updateProfile(playerAction);
            // Verifica mudanca
            if(((CompositeDecisionMakingSystem2)decisionMakingSystem).getChangeCondition().isTrue()) {
                change = true;
                turnChange = true;
            }
        }
        // Avalia acao do computador
        String computerAction = decisionMakingSystem.evaluate();
        tempoComputador = System.nanoTime() - tempoComputador;
        // Log de tempo de processamento
        writeLogFile("C:\\BattleBotData\\gameProcessingTimeLog.txt", "Tempo Jogador: " + tempoJogador + " - Tempo Computador: " + tempoComputador + " - Change: " + turnChange);
        // Carrega acao do jogador
        botBattle.setPlayerCharacterCurrentAction(playerAction);
        // Carrega acao do computador
        botBattle.setNonPlayerCharacterCurrentAction(computerAction);
        // Executa turno
        botBattle.turn();
        
        setStatus("Player 1: " + playerAction + " - Player 2: " + computerAction);
        writeLogFile("C:\\BattleBotData\\gameActionLog.txt", getStatus());

        Float performance = 0f;
        String mensagem = "";
        // Se usando CDSMS
        if((decisionMakingSystem == compositeDecisionMakingSystem) || (decisionMakingSystem == compositeDecisionMakingSystem2)) {
            performance = ((CompositeDecisionMakingSystem2)decisionMakingSystem).getEvaluationExpression().evaluate();
            mensagem = ((CompositeDecisionMakingSystem2)decisionMakingSystem).getPlayerProfile().getName() + " - " + ((CompositeDecisionMakingSystem2)decisionMakingSystem).getActiveDecisionMakingSystem().getIdentifier() + " - " + performance.toString();
        }
        else {
            performance = evaluationFunction.evaluate();
            mensagem = decisionMakingSystem.getIdentifier() + " - " + performance.toString();
        }
        writeLogFile("C:\\BattleBotData\\gameLog.txt", mensagem);
        
        if(botBattle.getEndGame()) {
            setGameActive(false);
            decisionMakingSystem.end();
            playerDecisionMakingSystem.end();
            if(botBattle.getPlayerHp() > botBattle.getComputerHp()) {
                setStatus("Jogador venceu");
                playerVictory = true;
            }
            else {
                setStatus("Computador venceu");
                playerVictory = false;
            }
            writeLogFile("C:\\BattleBotData\\gameLog.txt", getStatus());
            writeLogFile("C:\\BattleBotData\\gameActionLog.txt", getStatus());
        }
        else {
            setStatus("Acao do jogador: " + playerAction + " - Acao do computador: " + computerAction);
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
    
    /**
     * Recupera posicao do jogador
     * @return Posicao do jogador
     */
    public Float getPlayerPosition() {
        return world.getPlayerCharacter().getAttributeValue("POS");
    }
    
    /**
     * Recupera posicao do computador
     * @return Posicao do computador
     */
    public Float getComputerPosition() {
        return world.getNonPlayerCharacter().getAttributeValue("POS");
    }
    
    /**
     * Recupera saude do jogador
     * @return Saude do jogador
     */
    public Float getPlayerHp() {
        return botBattle.getPlayerHp();
    }
    
    /**
     * Recupera saude do computador
     * @return Saude do computador
     */
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
        defenseProfile.addProfileElement(new ProfileSimpleElement("ABS"));
        profile.addProfileElement(defenseProfile);         
        profile.addProfileElement(new ProfileSimpleElement("CON"));
        profile.addProfileElement(new ProfileSimpleElement("ENE"));
        profile.addProfileElement(new ProfileSimpleElement("LIM"));
        return profile;
    }
    
    /**
     * Cria um SC-TD
     */
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
        DecisionMakingSystem meleeRuleBasedSystem = new RuleBasedSystem2("meleeRules", botBattle, createRecklessPhysicalRules());
        meleeRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        DecisionMakingSystem defensiveRuleBasedSystem = new RuleBasedSystem2("defensiveRules", botBattle, createDefensiveRules());
        defensiveRuleBasedSystem.setCharacter(world.getNonPlayerCharacter().getName());
        compositeDecisionMakingSystem.addDecisionMakingSystem(rangedRuleBasedSystem, 1000);
        compositeDecisionMakingSystem.addDecisionMakingSystem(meleeRuleBasedSystem, 999);
        compositeDecisionMakingSystem.addDecisionMakingSystem(defensiveRuleBasedSystem, 998);
        compositeDecisionMakingSystem.getPlayerProfile().setProfile(createProfile());
    }
    
    /**
     * Cria um SC-TD com mais STDs
     */
    private void createCompositeDecisionMakingSystem2() {
        compositeDecisionMakingSystem2 = new CompositeDecisionMakingSystem2("composite", botBattle);
        botBattle.addWatchVariable("SAU", "SAUANT");
        try {
            compositeDecisionMakingSystem2.setChangeCondition("((CSAUANT - CSAU) - (PSAUANT - PSAU) ) > 100");
            compositeDecisionMakingSystem2.setAdjustExpression("((PSAUANT - PSAU) - (CSAUANT - CSAU))/NLPT");
            compositeDecisionMakingSystem2.setEvaluationExpression("(PSAUANT - PSAU) - (CSAUANT - CSAU)");
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
        compositeDecisionMakingSystem2.addDecisionMakingSystem(rangedRuleBasedSystem, 1000);
        compositeDecisionMakingSystem2.addDecisionMakingSystem(recklessMeleeRuleBasedSystem, 999);
        compositeDecisionMakingSystem2.addDecisionMakingSystem(defensiveRuleBasedSystem, 998);
        compositeDecisionMakingSystem2.addDecisionMakingSystem(strategicRangedRuleBasedSystem, 997);
        compositeDecisionMakingSystem2.addDecisionMakingSystem(meleeRuleBasedSystem, 996);
        compositeDecisionMakingSystem2.getPlayerProfile().setProfile(createProfile());
    }
    
    /**
     * Cria um DS para representar o jogador
     */
    private void createDynamicScripting() {
        dynamicScripting = new DynamicScripting2("dynamicScripting", botBattle);
        dynamicScripting.setRules(createPlayerRuleBase());
        dynamicScripting.setCharacter(world.getPlayerCharacter().getName());
        dynamicScripting.setMinWeight(1f);
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
    
    /**
     * Cria um DS para representar o computador
     */
    private void createDynamicScriptingComputer() {
        dynamicScriptingComputer = new DynamicScripting2("dynamicScripting", botBattle);
        dynamicScriptingComputer.setRules(createComputerRuleBase());
        dynamicScriptingComputer.setCharacter(world.getNonPlayerCharacter().getName());
        dynamicScriptingComputer.setMinWeight(1f);
        try {
            Parser parser = new Parser();
            dynamicScriptingComputer.addDefaultRule(new Rule(parser.parse("1"), "ATF", 100L, 10, 100f));
            dynamicScriptingComputer.addDefaultRule(new Rule(parser.parse("1"), "ATL", 101L, 10, 100f));
            dynamicScriptingComputer.setAdjustExpression("((1000 - PSAU) - (1000 - CSAU))/NTRN");
        }
        catch(Exception e) {
            e.printStackTrace();
            setStatus(e.getMessage());
        }
    }
    
    /**
     * Cria regras de ataque fisico para jogadores - Nao utilizar como STD de computador
     * @return Lista de regras
     */
    private List<Rule> createPhysicalRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (NRAND > 0.3)"), "ABS"));           
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!PPRO)"), "APR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= PENE) & (!PPRO)"), "COR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PENE >= 6) & (PPRO)"), "LIM"));          
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO < 5)"), "CON"));
            rules.add(new Rule(parser.parse("PCOO >= 5"), "AFC"));
            rules.add(new Rule(parser.parse("1"), "ENE"));
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
     * Cria regras de ataque de longa distancia para computador
     * @return Lista de regras
     */
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
     * Cria regras de ataque de longa distancia estrategico para jogadores - Nao utilizar como STD de computador
     * @return Lista de regras
     */
    private List<Rule> createStrategicRangedRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (PENE > 0) & ((PCOO < 5) | (!CENE)) & (NRAND > 0.1)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE < 3) & (CSAU < 90)"), "ALC"));
            rules.add(new Rule(parser.parse("!PCAO  & (PCOO >= 5) & (PENE >= 5)"), "CAR"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5)"), "ATL"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!CPRO) & (PENE >= 6)"), "PRE"));
            rules.add(new Rule(parser.parse("PCOO < 5"), "CON"));
            rules.add(new Rule(parser.parse("1"), "ATF"));            
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
    
    /**
     * Cria regras de ataque fisico para computador
     * @return Lista de regras
     */
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
    
    /**
     * Cria regras defensivas para computador
     * @return Lista de regras
     */
    private List<Rule> createDefensiveRules() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO = 1) & (CENE > 5) & (NRAND > 0.1)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE >= 3) & (NRAND > 0.1)"), "ABS"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE < 3) & (NRAND > 0.1)"), "DEF"));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & CENE & (NRAND > 0.1)"), "ABS"));
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
    
    /**
     * Cria base de regras para dynamic scripting interpretando computador
     * @return
     */
    private List<Rule> createComputerRuleBase() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (NRAND > 0.1)"), "ABS", 1L, 1, 100f));           
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

            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE > 0) & ((CCOO < 5) | (!PENE)) & (NRAND > 0.1)"), "ABS", 14L, 1, 100f));
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

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO = 1) & (CENE > 5) & (NRAND > 0.1)"), "ABS", 25L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE >= 3) & (NRAND > 0.1)"), "ABS", 26L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO >= 5) & (CENE < 3) & (NRAND > 0.1)"), "DEF", 27L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & CENE & (NRAND > 0.1)"), "ABS", 28L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & CCOO"), "AFC", 29L, 5, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5)"), "ALC", 30L, 6, 100f));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }
    
    /**
     * Cria base de regras para dynamic scripting interpretando jogador
     * @return
     */
    private List<Rule> createPlayerRuleBase() {
        List<Rule> rules = new ArrayList<Rule>();
        try {
            Parser parser = new Parser();
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (NRAND > 0.1)"), "ABS", 1L, 1, 100f));           
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!PPRO)"), "APR", 2L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= PENE) & (!PPRO)"), "COR", 3L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PENE >= 6) & (PPRO)"), "LIM", 4L, 4, 100f));          
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (PCOO < 5)"), "CON", 5L, 5, 100f));
            rules.add(new Rule(parser.parse("PCOO >= 5"), "AFC", 6L, 6, 100f));
            rules.add(new Rule(parser.parse("1"), "ENE", 7L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 1.5) & (!CPRO)"), "AFA", 8L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!CPRO) & (PENE >= 6)"), "PRE", 9L, 2, 100f));
            rules.add(new Rule(parser.parse("(PENE >= 6) & (PPRO)"), "LIM", 10L, 3, 100f));
            rules.add(new Rule(parser.parse("PCOO < 5"), "CON", 11L, 4, 100f));
            rules.add(new Rule(parser.parse("PCOO >= 5"), "ALC", 12L, 5, 100f));
            rules.add(new Rule(parser.parse("!PCOO"), "ATL", 13L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & (PENE > 0) & ((PCOO < 5) | (!CENE)) & (NRAND > 0.1)"), "ABS", 14L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5) & (CENE < 3) & (CSAU < 90)"), "ALC", 15L, 2, 100f));
            rules.add(new Rule(parser.parse("!PCAO  & (PCOO >= 5) & (PENE >= 5)"), "CAR", 16L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5)"), "ATL", 17L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!CPRO) & (PENE >= 6)"), "PRE", 18L, 5, 100f));
            rules.add(new Rule(parser.parse("1"), "CON", 19L, 7, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= 1.5) & (!PPRO)"), "APR", 20L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & ((PPOS + CPOS) <= PENE) & (!PPRO)"), "COR", 21L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PENE >= 6) & (PPRO)"), "LIM", 22L, 3, 100f));         
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (!PCOO)"), "CON", 23L, 4, 100f));
            rules.add(new Rule(parser.parse("PCOO"), "AFC", 24L, 5, 100f));

            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (CCOO = 1) & (PENE > 5) & (NRAND > 0.1)"), "ABS", 25L, 1, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (CCOO >= 5) & (PENE >= 3) & (NRAND > 0.1)"), "ABS", 26L, 2, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & (CCOO >= 5) & (PENE < 3) & (NRAND > 0.1)"), "DEF", 27L, 3, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (CCOO >= 5) & PENE & (NRAND > 0.1)"), "ABS", 28L, 4, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) <= 0.5) & PCOO"), "AFC", 29L, 5, 100f));
            rules.add(new Rule(parser.parse("((PPOS + CPOS) >= 0.5) & (PCOO >= 5)"), "ALC", 30L, 6, 100f));
        }
        catch(Exception e) {
            setStatus("Erro de parsing: " + e.getMessage());
        }       
        return rules;
    }        


    /**
     * Retorna o estado do jogo
     * @return Estado do jogo
     */
    public String getStatus() {
        return status;
    }

    /**
     * Atualiza o estado do jogo
     * @param status Estado do jogo
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Verifica se o jogo se encontra ativo
     * @return Flag que indica se o jogo se encontra ativo
     */
    public Boolean getGameActive() {
        return gameActive;
    }

    /**
     * Atualiza o flag que indica se o jogo se encontra ativo
     * @param gameActive Flag que indica se o jogo se encontra ativo
     */
    public void setGameActive(Boolean gameActive) {
        this.gameActive = gameActive;
    }
    
    /**
     * Recupera o jogoador
     * @return Jogador
     */
    public Character getPlayer() {
        return world.getPlayerCharacter();
    }
    
    /**
     * Recupera o computador
     * @return Computador
     */
    public Character getComputer() {
        return world.getNonPlayerCharacter();
    }

    /**
     * @return the experimentNumber
     */
    public Integer getExperimentNumber() {
        return experimentNumber;
    }

    /**
     * @param experimentNumber the experimentNumber to set
     */
    public void setExperimentNumber(Integer experimentNumber) {
        this.experimentNumber = experimentNumber;
    }

    /**
     * @return the games
     */
    public Integer getGames() {
        return games;
    }

    /**
     * @param games the games to set
     */
    public void setGames(Integer games) {
        this.games = games;
    }
    
}
