package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CompositeDecisionMakingSystem2 implements DecisionMakingSystem {
    
    private DecisionMakingSystem activeDecisionMakingSystem;
    private Game game;
    private String identifier;
    private Clausule changeCondition;
    private Clausule adjustExpression;
    private Clausule evaluationExpression;    
    private BigDecimal similarityThreshold = new BigDecimal(0.7);
    private Integer safetyThreshold = 5;
    private Integer defaultRanking = 1000;
    private PlayerProfile playerProfile = new PlayerProfile();
    private List<PlayerProfile> previousProfiles = new ArrayList<PlayerProfile>();
    private Integer profileCount = 0;
    private String character;
    
    /**
     * Construtor
     * @param identifier Identificador
     * @param game Mundo do jogo
     */
    public CompositeDecisionMakingSystem2(String identifier, Game game) {
        if(identifier == null || identifier.isEmpty() || game == null) {
            throw new RuntimeException("Cannot instantiate with null or empty parameters");
        }
        setIdentifier(identifier);
        setGame(game);
        game.getControlAttributes().put("LPT", new Attribute("LPT", "Last Profile Turns", 0f));
        getPlayerProfile().setName("P");
    }
    
    /**
     * Reseta
     */
    public void reset() {
        profileCount = 0;
        setPreviousProfiles(new ArrayList<PlayerProfile>());
        setLastProfileTurns(0f);
        setActiveDecisionMakingSystem(getPlayerProfile().getHighestRanked());
        getPlayerProfile().resetProfile();
        getPlayerProfile().resetWeights();
        getPlayerProfile().setName("P");
    }

    /**
     * Metodo que avalia a proxima acao a tomar
     */
    @Override
    public String evaluate() {
        // Se nao existe DMS nao faz nada
        if(getActiveDecisionMakingSystem() == null)
            return null;
        // Senao
        else {
            // Se condicao de alteracao de DMS foi satisfeita e acao de alteracao nao esta em cooldown
            if(monitor() && getLastProfileTurns() > getSafetyThreshold()) {
                profileManager();
            }
            // Decrementa o cooldown da alteracao de DMS
            setLastProfileTurns(getLastProfileTurns() + 1f);
            // Avalia a nova acao usando o novo DMS
            String action = getActiveDecisionMakingSystem().evaluate();
            return action;
        }
    }
    
    /**
     * Metodo que atualiza os perfis
     */
    private void profileManager() {
        // Gera estatisticas do jogador
        getPlayerProfile().generateStatistics();
        PlayerProfile similar = null;
        BigDecimal highestSimilarity = new BigDecimal(0);
        // Procura perfil similar na lista de perfis existentes
        if(!getPreviousProfiles().isEmpty()) {
            for(PlayerProfile p: getPreviousProfiles()) {
                BigDecimal cosineSimilarity = cosineSimilarity(getPlayerProfile().getStatistics(), p.getStatistics()); 
                if(cosineSimilarity.compareTo(highestSimilarity) > 0) {
                    highestSimilarity = cosineSimilarity;
                    similar = p;
                }
            }
        }
        PlayerProfile originalProfile = getPlayerProfile().clone();
        // Se o perfil mais similar nao e similar o suficiente
        if(highestSimilarity.compareTo(getSimilarityThreshold()) < 0) {
            // Se realmente existe perfil similar
            if(similar != null) {
                // Cria um novo perfil com os rankings do mais similar e as estatisticas do atual
                PlayerProfile newProfile = similar.clone();
                // Atualiza pesos conforme similaridade
                for(RankedDecisionMakingSystem dms: newProfile.getDecisionMakingSystems()) {
                    dms.setRanking((new Long(Math.round((((dms.getRanking() - defaultRanking)) * highestSimilarity.doubleValue() + defaultRanking)))).intValue());
                }
                newProfile.setProfile(getPlayerProfile().getProfile());
                newProfile.setStatistics(getPlayerProfile().getStatistics());
                originalProfile = newProfile;
                setPlayerProfile(newProfile.clone());
                // Armazena o novo perfil na lista de perfis existentes
                getPreviousProfiles().add(newProfile);
            }
            // Senao armazena o perfil atual na integra
            else {
                getPreviousProfiles().add(originalProfile);
            }
            originalProfile.setName("P" + profileCount++);
            getPlayerProfile().setName(originalProfile.getName());
        }
        // Senao
        else {
            // Perfil utilizado passa a ser o similar
            setPlayerProfile(similar.clone());
            originalProfile = similar;
        }
        // Se existe uma expressao de ajuste de ranking
        if(getAdjustExpression() != null) {
            Integer difference = 0;
            Integer newRanking = 0;
            // Calcula novo ranking e diferenca
            for(RankedDecisionMakingSystem r: getPlayerProfile().getDecisionMakingSystems()) {
                if(r.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
                    newRanking = r.getRanking() + (int)Math.floor(getAdjustExpression().evaluate());
                    if(newRanking < 0) {
                        difference = 0 - newRanking;
                        newRanking = 0;
                    }
                }
            }
            // Procura o DMS que estava sendo utilizado e atualiza seu ranking no profile atual
            for(RankedDecisionMakingSystem r: getPlayerProfile().getDecisionMakingSystems()) {
                if(r.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
                    r.setRanking(newRanking);
                }
                else if(difference != 0){
                    r.setRanking(r.getRanking() + difference);
                }
            }
            // Procura o DMS que estava sendo utilizado e atualiza seu ranking no profile original armazenado
            for(RankedDecisionMakingSystem r: originalProfile.getDecisionMakingSystems()) {
                if(r.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
                    r.setRanking(newRanking);
                }
                else if(difference != 0){
                    r.setRanking(r.getRanking() + difference);
                }
            }            
        }
        RankedDecisionMakingSystem candidate = getPlayerProfile().getHighestRankedWithRank();
        setActiveDecisionMakingSystem(candidate.getDecisionMakingSystem());
        // Reseta o cooldown da alteracao de DMS
        setLastProfileTurns(0f);
        // Reseta o perfil do jogador
        getPlayerProfile().resetProfile();
        // Salva variaveis
        game.save();
    }
    
    /**
     * Monitor do sistema
     * Avalia se a condicao de troca foi satisfeita
     * @return Resultado da avaliacao da condicao de troca
     */
    private Boolean monitor() {
        if(getChangeCondition() == null) {
            return false;
        }
        return getChangeCondition().isTrue();
    }
    
    /**
     * Metodo que calcula a similaridade entre dois vetores
     * @param v1 Primeiro vetor
     * @param v2 Segundo vetor
     * @return Similaridade entre os dois vetores
     */
    public BigDecimal cosineSimilarity (BigDecimal[] v1, BigDecimal[] v2) {
        if(v1 == null || v2 == null || v1.length != v2.length)
            throw new RuntimeException("Invalid array");
        BigDecimal dividend = new BigDecimal(0);
        BigDecimal divisorV1 = new BigDecimal(0);
        BigDecimal divisorV2 = new BigDecimal(0);
        for(Integer index=0; index < v1.length; index++) {
            dividend = dividend.add(v1[index].multiply(v2[index]));
            divisorV1 = divisorV1.add(v1[index].multiply(v1[index]));
            divisorV2 = divisorV2.add(v2[index].multiply(v2[index]));
        }
        divisorV1 = new BigDecimal(Math.sqrt(divisorV1.doubleValue()));
        divisorV2 = new BigDecimal(Math.sqrt(divisorV2.doubleValue()));
        if(divisorV1.equals(new BigDecimal(0)) && divisorV2.equals(new BigDecimal(0))) {
            return new BigDecimal(1);
        }
        if(divisorV1.equals(new BigDecimal(0)) || divisorV2.equals(new BigDecimal(0))) {
            return new BigDecimal(0);
        }
        return dividend.divide((divisorV1.multiply(divisorV2)), 10, RoundingMode.HALF_UP);
    }

    @Override
    public void begin() {
        game.save();
        setLastProfileTurns(0f);
        setActiveDecisionMakingSystem(getPlayerProfile().getHighestRanked());
    }

    @Override
    public void end() {
        profileManager();
    }       
    
    /**
     * Metodo que retorna o valor do atributo de controle local que armazena o turno atual
     * @return O turno atual com o Decision Making System selecionado
     */
    private Float getLastProfileTurns() {
        return game.getControlAttributes().get("LPT").getValue();
    }
    
    /**
     * Metodo que altera o valor do atributo de controle local que armazena o turno atual 
     * @param value Novo valor para o turno
     */
    private void setLastProfileTurns(Float value) {
        game.getControlAttributes().get("LPT").setValue(value);
    }    
    
    public DecisionMakingSystem getActiveDecisionMakingSystem() {
        return activeDecisionMakingSystem;
    }
    
    public void setActiveDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem) {
        activeDecisionMakingSystem = decisionMakingSystem;
    }    

    public void addDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem, Integer ranking) {
        getPlayerProfile().addDecisionMakingSystem(decisionMakingSystem, ranking);
        setActiveDecisionMakingSystem(getPlayerProfile().getHighestRanked());
    }
    
    public void addDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem) {
        getPlayerProfile().addDecisionMakingSystem(decisionMakingSystem, defaultRanking);
        setActiveDecisionMakingSystem(getPlayerProfile().getHighestRanked());
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public void setPlayerProfile(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    } 
    
    public void updatePlayerProfile(String action) {
        getPlayerProfile().updateProfile(action);
    }
   
    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Clausule getChangeCondition() {
        return changeCondition;
    }

    public void setChangeCondition(Clausule changeCondition) {
        if(changeCondition != null)
            changeCondition.setSolver(game);
        this.changeCondition = changeCondition;
    }
    
    public void setChangeCondition(String changeCondition) throws Exception{
        Parser parser = new Parser();
        setChangeCondition(parser.parse(changeCondition));
    }

    public Clausule getAdjustExpression() {
        return adjustExpression;
    }

    public void setAdjustExpression(Clausule adjustExpression) {
        if(adjustExpression != null)
            adjustExpression.setSolver(game);
        this.adjustExpression = adjustExpression;
    }
    
    public void setAdjustExpression(String adjustExpression) throws Exception{
        Parser parser = new Parser();
        setAdjustExpression(parser.parse(adjustExpression));
    }    

    public Clausule getEvaluationExpression() {
        return evaluationExpression;
    }

    public void setEvaluationExpression(Clausule evaluationExpression) {
        if(evaluationExpression != null)
            evaluationExpression.setSolver(game);
        this.evaluationExpression = evaluationExpression;
    }
    
    public void setEvaluationExpression(String evaluationExpression) throws Exception{
        Parser parser = new Parser();
        setEvaluationExpression(parser.parse(evaluationExpression));
    }     

    public List<PlayerProfile> getPreviousProfiles() {
        return previousProfiles;
    }

    public void setPreviousProfiles(List<PlayerProfile> previousProfiles) {
        this.previousProfiles = previousProfiles;
    }

    public BigDecimal getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(BigDecimal similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public Integer getSafetyThreshold() {
        return safetyThreshold;
    }

    public void setSafetyThreshold(Integer safetyThreshold) {
        this.safetyThreshold = safetyThreshold;
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }
    
    @Override
    public Character getPlayerCharacter() {
        return null;
    }

    @Override
    public void setPlayerCharacter(Character playerCharacter) {
    }

    @Override
    public Character getNonPlayerCharacter() {
        return null;
    }

    @Override
    public void setNonPlayerCharacter(Character nonPlayerCharacter) {
    }

    @Override
    public String getCharacter() {
        return this.character;
    }

    @Override
    public void setCharacter(String character) {
        this.character = character;
    }

    /**
     * @return the defaultRanking
     */
    public Integer getDefaultRanking() {
        return defaultRanking;
    }

    /**
     * @param defaultRanking the defaultRanking to set
     */
    public void setDefaultRanking(Integer defaultRanking) {
        this.defaultRanking = defaultRanking;
    }     

}
