package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;


public class ProfileSimpleElement implements ProfileElement {

    private String key; 
    private BigDecimal value;
    
    public ProfileSimpleElement(String key, BigDecimal value) {
        this.setKey(key);
        this.setValue(value);
    }
    
    public ProfileSimpleElement(String key) {
        this.setKey(key);
        this.setValue(new BigDecimal(0));
    } 
    

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }    

    @Override
    public Boolean hasKey(String key) {
        if((key == null && getKey() == null) || (key != null && getKey() != null && getKey().equals(key)))
            return true;
        return false;
    }

    @Override
    public BigDecimal getValue(String key) {
        if(this.key.equals(key)) {
            return getValue();
        }
        return null;
    }

    @Override
    public void updateKey(String key, BigDecimal value) {
        setValue(value);
    }
    
    @Override
    public ProfileElement clone() {
        return new ProfileSimpleElement(getKey(), getValue());
    }

}
