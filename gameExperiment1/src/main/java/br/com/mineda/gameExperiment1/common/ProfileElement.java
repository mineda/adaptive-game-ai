package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;


public interface ProfileElement {
    
    public Boolean hasKey(String key);
    public BigDecimal getValue(String key);
    public void updateKey(String key, BigDecimal value);
    public BigDecimal getValue();
    public ProfileElement clone();
    
}
