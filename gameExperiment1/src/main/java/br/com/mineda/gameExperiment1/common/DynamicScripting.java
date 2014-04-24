package br.com.mineda.gameExperiment1.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicScripting extends RuleBasedSystem {

    private List<Rule> chosenRules = new ArrayList<Rule>();
    private Map<String, Attribute> controlAttributes = new HashMap<String, Attribute>();    
    private Integer maxTries = 5;
    private Integer ruleBaseSize = 6;
    private Clausule adjustExpression;
    private Float minWeight = 0f;
    private Float maxWeight = 1000f;
    private Float sumWeights;
    private List<Rule> defaultRules = new ArrayList<Rule>();
    
    public DynamicScripting(String identifier, Character playerCharacter,
            Character nonPlayerCharacter) {
        super(identifier, playerCharacter, nonPlayerCharacter);
        getControlAttributes().put("TRN", new Attribute("TRN", "Turn", 0f));
    }
    
    @Override
    public String evaluate() {
        setTurn(getTurn()+1f);
        for(Rule rule: getChosenRules()) {
            if(rule.evaluate() && testAction(rule.getAction(), getNonPlayerCharacter(), getPlayerCharacter())) {
                rule.setUsed(true);
                return rule.getAction();
            }
        }
        return null;
    }
    
    @Override
    public Float solveVariable(String variableName, String variableOwner) {
        Float variableValue = super.solveVariable(variableName, variableOwner);
        if(variableValue != null) {
            return variableValue;
        }
        // Adicionalmente procura a variavel nos atributos de controle locais
        Attribute attribute = getControlAttributes().get(variableName);
        // Se achou
        if(attribute != null) {
            // Retorna o valor
            return attribute.getValue();
        }
        return null;
    }
    
    @Override
    public void begin() {
        // Sort rules by priority
        Collections.sort(getRules(), 
                new Comparator<Rule>() {
                    public int compare(Rule r1, Rule r2) {
                        return r1.getPriority().compareTo(r2.getPriority());
                    }
                });        
        // Clear script
        chosenRules = new ArrayList<Rule>();
        sumWeights = 0f;
        for(Rule rule: getRules()) {
            sumWeights += rule.getWeight();
            rule.setUsed(false);
        }
        for(Integer i=0; i<ruleBaseSize; i++) {
            Integer tryNumber = 0;
            Boolean lineAdded = false;
            while(tryNumber < maxTries && !lineAdded) {
                Integer j = 0;
                Float sum = 0f;
                Integer selected = -1;
                Integer fraction = (int) (Math.random()*sumWeights);
                while(selected < 0) {
                    Rule rule = getRules().get(j);
                    sum += rule.getWeight();
                    if(sum > fraction) {
                        selected = j;
                    }
                    else {
                        j++;
                    }
                }
                lineAdded = chooseRule(getRules().get(j));
                tryNumber++;
            }
        }
        // Include mandatory rules if needed
        for(Rule rule: getDefaultRules()) {
            chooseRule(rule);
        }
    }
    
    @Override
    public void end() {
        Integer active = 0;
        for(Rule rule: getRules()) {
            if(rule.getUsed()) {
                active++;
            }
        }
        if(active <= 0 && active >= getRules().size()) {
            return;
        }
        Integer nonactive = getRules().size() - active;
        Float adjustment = getAdjustExpression().evaluate();
        Float compensation = -active*adjustment/nonactive;
        Float newSumWeights = 0f;
        Float remainder = 0f;
        Float amount = new Float(getRules().size());
        for(Rule rule: getRules()) {
            if(rule.getUsed()) {
                rule.setWeight(rule.getWeight() + adjustment);
            }
            else {
                rule.setWeight(rule.getWeight() + compensation);
            }
            if(rule.getWeight() < minWeight) {
                rule.setWeight(minWeight);
                amount--;
            }
            else if(rule.getWeight() > maxWeight) {
                rule.setWeight(maxWeight);
                amount--;
            }
            newSumWeights += rule.getWeight();
        }
        remainder = sumWeights - newSumWeights;
        while(remainder > 0) {
            Float remainderPerCapta = new Float(remainder/amount);
            for(Rule rule: getRules()) {
                if(remainder > 0) {
                    if(remainderPerCapta > remainder) {
                        remainderPerCapta = remainder;
                    }                    
                    if((rule.getWeight() + remainderPerCapta) <= maxWeight) {
                        rule.setWeight(rule.getWeight() + remainderPerCapta);
                        remainder -= remainderPerCapta;
                    }
                }
            }
        }
    }
    
    /**
     * Choose a rule from the rule base
     * @param rule Rule to be chosen
     * @return true if the rule was not chosen yet
     */
    private Boolean chooseRule(Rule rule) {
        for(Rule chosenRule: chosenRules) {
            if(chosenRule.getId().equals(rule.getId())) {
                return false;
            }
        }
        chosenRules.add(rule);
        return true;
    }
    
    @Override
    public void setRules(List<Rule> rules) {
        if(rules == null) {
            throw new RuntimeException("Regras nao podem ser nulas");
        }
        Integer count = 0;
        for(Rule rule: rules) {
            if(rule.getPriority() == null) {
                rule.setPriority(count++);
            }
            addRule(rule);
        }
        this.rules = rules;
    }
    
    @Override
    public void addRule(Rule rule) {
        rule.setSolver(this);
        if(rule.getWeight() == null) {
            rule.setWeight(100f);
        }        
        rules.add(rule);
    }    

    public List<Rule> getChosenRules() {
        return chosenRules;
    }

    public void setChosenRules(List<Rule> chosenRules) {
        this.chosenRules = chosenRules;
    }

    public Integer getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(Integer maxTries) {
        this.maxTries = maxTries;
    }

    public Integer getRuleBaseSize() {
        return ruleBaseSize;
    }

    public void setRuleBaseSize(Integer ruleBaseSize) {
        this.ruleBaseSize = ruleBaseSize;
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

    public Float getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(Float minWeight) {
        this.minWeight = minWeight;
    }

    public Float getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Float maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Map<String, Attribute> getControlAttributes() {
        return controlAttributes;
    }

    public void setControlAttributes(Map<String, Attribute> controlAttributes) {
        this.controlAttributes = controlAttributes;
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

    public List<Rule> getDefaultRules() {
        return defaultRules;
    }

    public void setDefaultRules(List<Rule> defaultRules) {
        this.defaultRules = defaultRules;
    }
    
    public void addDefaultRule(Rule rule) {
        defaultRules.add(rule);
    }

}
