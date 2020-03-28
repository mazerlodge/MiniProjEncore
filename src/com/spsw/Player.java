package com.spsw;

public class Player {

    public int ID;
    public int bankroll;
	public int targetPoints; 

    public Player(int ID, int bankroll, int targetPoints) {
		this.ID = ID; 
		this.bankroll = bankroll;
		this.targetPoints = targetPoints;

	}
	
	public Bet placeBet(int amount, BetType type) {

		bankroll -= amount;
		Bet aBet = new Bet(amount, type, this.ID);

		return aBet;

	}

	public void addOdds(int amount, Bet theBet) { 

		bankroll -= amount; 
		theBet.oddsAmount += amount; 

	}

	public void collectWinnings(int amount) {
		bankroll += amount; 
		
	}


}