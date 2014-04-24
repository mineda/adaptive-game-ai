package br.com.mineda.gameExperiment1.common;

import java.util.ArrayList;
import java.util.List;

public class RuleBasedSystem2 implements DecisionMakingSystem {
	
	protected Game game;
	protected List<Rule> rules;
	private String identifier;
	private String character;
	
	public RuleBasedSystem2(String identifier, Game game, List<Rule> rules) {
		setIdentifier(identifier);
	    setGame(game);
		setRules(rules);
	}
	
	public RuleBasedSystem2(String identifier, Game game) {
		this(identifier, game, new ArrayList<Rule>());
	}

	@Override
	public String evaluate() {
		for(Rule rule: getRules()) {
			if(rule.evaluate() && game.testAction(rule.getAction(), getCharacter())) {
				return rule.getAction();
			}
		}
		return null;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		if(rules == null) {
			throw new RuntimeException("Regras nao podem ser nulas");
		}
		for(Rule rule: rules) {
			rule.setSolver(game);
		}
		this.rules = rules;
	}
	
	public void addRule(Rule rule) {
		rule.setSolver(game);
		rules.add(rule);
	}

    @Override
	public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void begin() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void end() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public Character getPlayerCharacter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPlayerCharacter(Character playerCharacter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Character getNonPlayerCharacter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNonPlayerCharacter(Character nonPlayerCharacter) {
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
