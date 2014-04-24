package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeDecisionMakingSystem implements DecisionMakingSystem, GameController, Solver{
    
    private DecisionMakingSystem activeDecisionMakingSystem;
    private Character playerCharacter;
    private Character nonPlayerCharacter;
    private String identifier;
    private Map<String, Attribute> controlAttributes = new HashMap<String, Attribute>();
    private GameController gameController;
    private Clausule changeCondition;
    private String watchVariable;
    private Clausule adjustExpression;
    private Clausule evaluationExpression;    
    private BigDecimal similarityThreshold = new BigDecimal(0.5);
    private Integer safetyThreshold = 5;
    private PlayerProfile playerProfile = new PlayerProfile();
    private List<PlayerProfile> previousProfiles = new ArrayList<PlayerProfile>();
    private Integer profileCount = 0;
    private String character;
    
    public CompositeDecisionMakingSystem(String identifier, Character playerCharacter, Character nonPlayerCharacter, GameController gameController) {
        if(identifier == null || identifier.isEmpty() || playerCharacter == null || nonPlayerCharacter == null) {
            throw new RuntimeException("Cannot instantiate with null or empty parameters");
        }
        setIdentifier(identifier);
        setPlayerCharacter(playerCharacter);
        setNonPlayerCharacter(nonPlayerCharacter);
        setGameController(gameController);
        getControlAttributes().put("TRN", new Attribute("TRN", "Turn", 0f));
        getPlayerProfile().setName("P");
    }
    
    public CompositeDecisionMakingSystem(String identifier, Character playerCharacter, Character nonPlayerCharacter) {
        this(identifier, playerCharacter, nonPlayerCharacter, null);
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
            if(getChangeCondition() != null && getChangeCondition().isTrue() && getTurn() > getSafetyThreshold()) {
                updateProfiles();
            }
            // Decrementa o cooldown da alteracao de DMS
            setTurn(getTurn() + 1f);
            // Avalia a nova acao usando o novo DMS
            String action = getActiveDecisionMakingSystem().evaluate();
            return action;
        }
    }
    
    /**
     * Metodo que atualiza os perfis
     */
    private void updateProfiles() {
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
        }
        // Senao
        else {
            // Perfil utilizado passa a ser o similar
            setPlayerProfile(similar.clone());
            originalProfile = similar;
        }
        // Se existe uma expressao de ajuste de ranking
        if(getAdjustExpression() != null) {
            // Procura o DMS que estava sendo utilizado e atualiza seu ranking no profile atual
            for(RankedDecisionMakingSystem r: getPlayerProfile().getDecisionMakingSystems()) {
                if(r.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
                    r.setRanking(r.getRanking() + (int)Math.floor(getAdjustExpression().evaluate()));
                }
            }
            // Procura o DMS que estava sendo utilizado e atualiza seu ranking no profile original armazenado
            for(RankedDecisionMakingSystem r: originalProfile.getDecisionMakingSystems()) {
                if(r.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
                    r.setRanking(r.getRanking() + (int)Math.floor(getAdjustExpression().evaluate()));
                }
            }            
        }
        RankedDecisionMakingSystem candidate = getPlayerProfile().getHighestRankedWithRank();
        // Se o melhor DMS para o perfil era o que estava sendo utilizado
        /*if(candidate.getDecisionMakingSystem().getIdentifier().equals(getActiveDecisionMakingSystem().getIdentifier())) {
            // Troca para o segundo melhor, se existir
            RankedDecisionMakingSystem betterCandidate = null;
            for(RankedDecisionMakingSystem r: getPlayerProfile().getDecisionMakingSystems()) {
                if(!r.getDecisionMakingSystem().getIdentifier().equals(candidate.getDecisionMakingSystem().getIdentifier())) {
                    if(betterCandidate == null) {
                        betterCandidate = r;
                    }
                    else if(r.getRanking() <= candidate.getRanking() && r.getRanking() > betterCandidate.getRanking()){
                        betterCandidate = r;
                    }
                }
            }
            setActiveDecisionMakingSystem(betterCandidate.getDecisionMakingSystem());
        }
        // Senao troca para o melhor DMS
        else {*/
            setActiveDecisionMakingSystem(candidate.getDecisionMakingSystem());
        /*}*/
        // Reseta o cooldown da alteracao de DMS
        setTurn(0f);
        // Reseta o perfil do jogador
        getPlayerProfile().resetProfile();
        // Salva valor da variavel a ser mantida
        getControlAttributes().put(Character.PLAYER + getWatchVariable() + "ANT", getPlayerCharacter().getAttribute(getWatchVariable()).clone());
        getControlAttributes().put(Character.COMPUTER + getWatchVariable() + "ANT", getNonPlayerCharacter().getAttribute(getWatchVariable()).clone());
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
    public Boolean testAction(String action, Character actor, Character adversary) {
        if(getGameController() != null)
            return getGameController().testAction(action, actor, adversary);
        return true;
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
        // Se existe um controlador de jogo
        if(getGameController() != null){
            // Retorna o resultado da pesquisa utilizando-o
            Float result = getGameController().solveVariable(variableName, variableOwner);
            if(result != null) {
                return result;
            }
        }
        // Adicionalmente procura a variavel nos atributos de controle locais
        Attribute attribute = getControlAttributes().get(variableName);
        // Se achou
        if(attribute != null) {
            // Retorna o valor
            return attribute.getValue();
        }
        // Se nao achou nada retorna null
        return null;
    }
    
    @Override
    public void begin() {
        getControlAttributes().put(Character.PLAYER + getWatchVariable() + "ANT", getPlayerCharacter().getAttribute(getWatchVariable()).clone());
        getControlAttributes().put(Character.COMPUTER + getWatchVariable() + "ANT", getNonPlayerCharacter().getAttribute(getWatchVariable()).clone());
        setTurn(0f);
        setActiveDecisionMakingSystem(getPlayerProfile().getHighestRanked());
    }

    @Override
    public void end() {
        updateProfiles();
    }       
    
    /**
     * Metodo que retorna o valor do atributo de controle local que armazena o turno atual
     * @return O turno atual com o Decision Making System selecionado
     */
    private Float getTurn() {
        return getControlAttributes().get("TRN").getValue();
    }
    
    /**
     * Metodo que altera o valor do atributo de controle local que armazena o turno atual 
     * @param value Novo valor para o turno
     */
    private void setTurn(Float value) {
        getControlAttributes().get("TRN").setValue(value);
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
    public Character getPlayerCharacter() {
        return this.playerCharacter;
    }

    @Override
    public void setPlayerCharacter(Character playerCharacter) {
        if(playerCharacter == null) {
            throw new RuntimeException("Personagem do jogador nao pode ser nulo");
        }   
        this.playerCharacter = playerCharacter;
        for(RankedDecisionMakingSystem dms: this.getPlayerProfile().getDecisionMakingSystems()) {
            dms.getDecisionMakingSystem().setPlayerCharacter(playerCharacter);
        }
    }

    @Override
    public Character getNonPlayerCharacter() {
        return this.nonPlayerCharacter;
    }

    @Override
    public void setNonPlayerCharacter(Character nonPlayerCharacter) {
        if(nonPlayerCharacter == null) {
            throw new RuntimeException("Personagem do computador nao pode ser nulo");
        }       
        this.nonPlayerCharacter = nonPlayerCharacter;
        for(RankedDecisionMakingSystem dms: this.getPlayerProfile().getDecisionMakingSystems()) {
            dms.getDecisionMakingSystem().setNonPlayerCharacter(nonPlayerCharacter);
        }
    }    

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
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
            changeCondition.setSolver(this);
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
            adjustExpression.setSolver(this);
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
            evaluationExpression.setSolver(this);
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

    public Map<String, Attribute> getControlAttributes() {
        return controlAttributes;
    }
    
    public List<Attribute> getControlAttributesAsList() {
        return new ArrayList<Attribute>(controlAttributes.values());
    }    
    
    public void addControlAttribute(String name, Float value) {
        getControlAttributes().put(name, new Attribute(name, name, value));
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

    public String getWatchVariable() {
        return this.watchVariable;
    }

    /**
     * Metodo que indica qual variavel deve ser armazenada ao inicio de cada turno
     * @param watchVariable Nome da variavel a ser armazenada ao inicio de cada turno
     * @return Nome da variavel como deve ser referenciado em regras
     */
    public String setWatchVariable(String watchVariable) {
        this.watchVariable = watchVariable;
        getControlAttributes().put(Character.PLAYER + watchVariable + "ANT", getPlayerCharacter().getAttribute(watchVariable).clone());
        getControlAttributes().put(Character.COMPUTER + watchVariable + "ANT", getNonPlayerCharacter().getAttribute(watchVariable).clone());
        return watchVariable + "ANT";
    }
    
    public Attribute getPlayerCharacterWatch() {
        return getControlAttributes().get(Character.PLAYER + getWatchVariable() + "ANT");
    }
    
    public Attribute getNonPlayerCharacterWatch() {
        return getControlAttributes().get(Character.COMPUTER + getWatchVariable() + "ANT");
    }

    @Override
    public void setGame(Game game) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCharacter() {
        return this.character;
    }

    @Override
    public void setCharacter(String character) {
        this.character = character;
    }

}
