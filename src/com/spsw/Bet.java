package com.spsw;

public class Bet {

    public int amount;
    public int onPoint;
    public int oddsAmount;
	public int ownerID;
	public BetType type;
	public boolean bDeleteEligible; 
	
    public Bet(int amount, BetType type, int ownerID) {
		this.amount = amount; 
		onPoint = -1;  // used to indicate newly placed bet
		oddsAmount = 0;
		this.ownerID = ownerID;
		this.type = type;
		bDeleteEligible = false; 
		
	}

	public void payWinner(Player thePlayer) { 

		int payAmount = this.amount; 

		if (oddsAmount > 0) {
			// Calc odds payout 
			switch (onPoint) {
				case 4:
				case 10:
					// pays 2:1
					payAmount += oddsAmount + oddsAmount*2;
				break;

				case 5:
				case 9:
					// pays 3:2
					payAmount += oddsAmount + (oddsAmount/2)*3;
				break;

				case 6:
				case 8:
					// pays 6:5
					payAmount += oddsAmount + (oddsAmount/5)*6;
				break;

			} // case 

		} // oddsAmount > 0 

		thePlayer.collectWinnings(payAmount);
		bDeleteEligible = true;

	}
	


}