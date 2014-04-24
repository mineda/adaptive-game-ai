package br.com.mineda.gameExperiment1.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProfileGroupElement implements ProfileElement {

    private List<ProfileElement> profileElements = new ArrayList<ProfileElement>();
    private String key;
    
    public ProfileGroupElement() {
        //Faz nada
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ProfileElement> getProfileElements() {
        return profileElements;
    }

    public void setProfileElements(List<ProfileElement> profileElements) {
        this.profileElements = profileElements;
    }      

    public void addProfileElement(ProfileElement profileElement) {
        getProfileElements().add(profileElement);
    }
    
    @Override
    public Boolean hasKey(String key) {
        for(ProfileElement profileElement: getProfileElements()) {
            if(profileElement.hasKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getValue(String key) {
        BigDecimal returnValue = null;
        for(ProfileElement profileElement: getProfileElements()) {
            returnValue = profileElement.getValue(key);
            if(returnValue != null) {
                return returnValue;
            }
        }        
        return returnValue;
    }

    @Override
    public void updateKey(String key, BigDecimal value) {
        for(ProfileElement profileElement: getProfileElements()) {
            if(profileElement.hasKey(key)) {
                profileElement.updateKey(key, value);
            }
        } 
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal total = new BigDecimal(0);
        for(ProfileElement profileElement: getProfileElements()) {
            total = total.add(profileElement.getValue());
        }
        return total;
    }
    
    public BigDecimal[] toArray() {
        BigDecimal[] array = new BigDecimal[getProfileElements().size()];
        for(Integer i = 0; i < getProfileElements().size(); i++)
            array[i] = getProfileElements().get(i).getValue();
        return array;
    }
    
    @Override
    public ProfileElement clone() {
        ProfileGroupElement newProfile = new ProfileGroupElement();
        newProfile.setKey(getKey());
        for(ProfileElement profileElement: getProfileElements())
            newProfile.getProfileElements().add(profileElement.clone());
        return newProfile;
    }

}
