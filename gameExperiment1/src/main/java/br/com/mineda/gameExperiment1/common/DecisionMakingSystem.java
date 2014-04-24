package br.com.mineda.gameExperiment1.common;


public interface DecisionMakingSystem {
	
	public String evaluate ();
	public void setGame(Game game);
	public String getCharacter();
	public void setCharacter(String character);
	public Character getPlayerCharacter();
	public void setPlayerCharacter (Character playerCharacter);
	public Character getNonPlayerCharacter();
	public void setNonPlayerCharacter(Character nonPlayerCharacter);
	public String getIdentifier();
	public void begin();
	public void end();

}
