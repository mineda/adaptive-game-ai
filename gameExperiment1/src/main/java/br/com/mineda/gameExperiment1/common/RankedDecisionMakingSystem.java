package br.com.mineda.gameExperiment1.common;

public class RankedDecisionMakingSystem {

    private DecisionMakingSystem decisionMakingSystem;
    private Integer ranking;
    private Integer originalRanking;

    public RankedDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem, Integer ranking) {
        this.setDecisionMakingSystem(decisionMakingSystem);
        this.setRanking(ranking);
        this.originalRanking = ranking;
    }

    public DecisionMakingSystem getDecisionMakingSystem() {
        return decisionMakingSystem;
    }
    
    public void setDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem) {
        this.decisionMakingSystem = decisionMakingSystem;
    }
    
    public Integer getRanking() {
        return ranking;
    }
    
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    
    public void reset() {
        this.ranking = this.originalRanking;
    }
}
