package br.com.mineda.gameExperiment1.common;

import java.util.ArrayList;
import java.util.List;

public class RuleBasedSystem implements DecisionMakingSystem, Solver {
	
	private Character playerCharacter;
	private Character nonPlayerCharacter;
	protected List<Rule> rules;
	private GameController gameController;
	private String identifier;
	
	public RuleBasedSystem(String identifier, Character playerCharacter, Character nonPlayerCharacter, List<Rule> rules, GameController gameController) {
		setIdentifier(identifier);
	    setPlayerCharacter(playerCharacter);
		setNonPlayerCharacter(nonPlayerCharacter);
		setRules(rules);
		setGameController(gameController);
	}
	
	public RuleBasedSystem(String identifier, Character playerCharacter, Character nonPlayerCharacter) {
		this(identifier, playerCharacter, nonPlayerCharacter, new ArrayList<Rule>(), null);
	}

	@Override
	public Float solveVariable(String variableName, String variableOwner) {
		Character character = null;
		if(variableOwner.equals(Character.PLAYER)) {
			character = getPlayerCharacter();
		}
		else if(variableOwner.equals(Character.COMPUTER)) {
			character = getNonPlayerCharacter();
		}
		if(character != null) {
			for(Attribute attribute: character.getAttributesAsList()) {
				if(attribute.getName().equals(variableName)) {
					return attribute.getValue();
				}
			}
		}
		else if(getGameController() != null){
		    return getGameController().solveVariable(variableName, variableOwner);
		}
		return null;
	}

	@Override
	public String evaluate() {
		for(Rule rule: getRules()) {
			if(rule.evaluate() && testAction(rule.getAction(), getNonPlayerCharacter(), getPlayerCharacter())) {
				return rule.getAction();
			}
		}
		return null;
	}

	@Override
	public Character getPlayerCharacter() {
		return playerCharacter;
	}

	@Override
	public void setPlayerCharacter(Character playerCharacter) {
		if(playerCharacter == null) {
			throw new RuntimeException("Personagem do jogador nao pode ser nulo");
		}	
		this.playerCharacter = playerCharacter;
	}

	@Override
	public Character getNonPlayerCharacter() {
		return nonPlayerCharacter;
	}

	@Override
	public void setNonPlayerCharacter(Character nonPlayerCharacter) {
		if(nonPlayerCharacter == null) {
			throw new RuntimeException("Personagem do computador nao pode ser nulo");
		}		
		this.nonPlayerCharacter = nonPlayerCharacter;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		if(rules == null) {
			throw new RuntimeException("Regras nao podem ser nulas");
		}
		for(Rule rule: rules) {
			rule.setSolver(this);
		}
		this.rules = rules;
	}
	
	public void addRule(Rule rule) {
		rule.setSolver(this);
		rules.add(rule);
	}

	public Boolean testAction(String action, Character actor, Character adversary) {
		if(getGameController() == null) {
			return true;
		}
		else {
			return getGameController().testAction(action, actor, adversary);
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCharacter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCharacter(String character) {
        // TODO Auto-generated method stub
        
    }

}
