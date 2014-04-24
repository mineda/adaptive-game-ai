package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PlayerProfile {
    
    private ProfileGroupElement profile = new ProfileGroupElement();
    private List<RankedDecisionMakingSystem> decisionMakingSystems = new ArrayList<RankedDecisionMakingSystem>();
    private Integer profiledActionsExecuted = 0;
    private BigDecimal[] statistics;
    private String name;
    
    public PlayerProfile() {
    }
    
    public ProfileGroupElement getProfile() {
        return profile;
    }
    
    public void setProfile(ProfileGroupElement profile) {
        this.profile = profile;
    }
    
    public List<RankedDecisionMakingSystem> getDecisionMakingSystems() {
        return decisionMakingSystems;
    }
    
    public void setDecisionMakingSystems(List<RankedDecisionMakingSystem> decisionMakingSystems) {
        this.decisionMakingSystems = decisionMakingSystems;
    }
    
    public void addDecisionMakingSystem(DecisionMakingSystem decisionMakingSystem, Integer ranking) {
        getDecisionMakingSystems().add(new RankedDecisionMakingSystem(decisionMakingSystem, ranking));
    }
    
    public Integer getHighestRankedIndex() {
        if(getDecisionMakingSystems().size() > 0) {
            RankedDecisionMakingSystem firstDms = getDecisionMakingSystems().iterator().next();
            Integer highestRank = firstDms.getRanking();
            Integer highestIndex = 0;
            for(Integer index=1; index < getDecisionMakingSystems().size(); index++) {
                RankedDecisionMakingSystem dms = getDecisionMakingSystems().get(index);
                if(dms.getRanking() > highestRank) {
                    highestRank = dms.getRanking();
                    highestIndex = index;
                }
            }
            return highestIndex;
        }
        throw new RuntimeException("No DMS to rank");
    }
    
    public DecisionMakingSystem getHighestRanked() {
        return getDecisionMakingSystems().get(getHighestRankedIndex()).getDecisionMakingSystem();
    } 
    
    public RankedDecisionMakingSystem getHighestRankedWithRank() {
        return getDecisionMakingSystems().get(getHighestRankedIndex());
    }     
    
    public void updateProfile(String action) {
        if(getProfile().hasKey(action)) {
            BigDecimal value = getProfile().getValue(action);
            getProfile().updateKey(action, value.add(new BigDecimal(1)));
            profiledActionsExecuted++;
        }
    }
    
    public BigDecimal[] generateStatistics() {
        statistics = getProfile().toArray();
        if(profiledActionsExecuted > 0) {
            for(Integer i=0; i<statistics.length; i++) { 
                statistics[i] = statistics[i].divide(new BigDecimal(profiledActionsExecuted), 10, RoundingMode.HALF_UP);
            }
            profiledActionsExecuted = 0;
        }
        return statistics;
    }
    
    public BigDecimal[] getStatistics() {
        return statistics;
    }
    
    public void setStatistics(BigDecimal[] statistics) {
        this.statistics = statistics;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resetProfile() {
        resetProfileGroup(this.getProfile());
    }
    
    private void resetProfileGroup(ProfileGroupElement profileGroup) {
        for(ProfileElement profile: profileGroup.getProfileElements()) {
            if(profile instanceof ProfileSimpleElement) {
                ProfileSimpleElement simpleElement = (ProfileSimpleElement)profile;
                simpleElement.setValue(new BigDecimal(0));
            }
            else {
                resetProfileGroup((ProfileGroupElement)profile);
            }
        }
    }
    
    public void resetWeights() {
        for(RankedDecisionMakingSystem r: getDecisionMakingSystems()) {
            r.reset();
        }
    }
    
    @Override
    public PlayerProfile clone() {
        PlayerProfile clone = new PlayerProfile();
        clone.setProfile((ProfileGroupElement)getProfile().clone());
        clone.setStatistics(getStatistics());
        clone.setName(getName());
        for(RankedDecisionMakingSystem r: getDecisionMakingSystems()) {
            clone.addDecisionMakingSystem(r.getDecisionMakingSystem(), r.getRanking());
        }
        return clone;
    }
}
