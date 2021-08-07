package com.spsw;
import java.util.ArrayList;

public class Game {

    private ArrayList<Bet> bets;
	private boolean buttonOn; 
	private int buttonPoint;
	private Player thePlayer; // initial version has one player 

	private ArgParser ap; // just bring the arg parser in for now.
	private QRandom r;

	private int rollsPerHour;
	private GameState gameState;

	private String msg; 
	int maxMsgLevel = -1;

    public Game(ArgParser ap) {

		bets = new ArrayList<Bet>();
		buttonOn = false; 
		buttonPoint = -1;

		parseArgs(ap); 

		r = new QRandom();
		gameState = GameState.ComeOutRoll;

	}

	private boolean parseArgs(ArgParser tap) { 

		boolean bRval = true; 

		this.ap = tap;

		// Make a Player
		int balance = 0;
		int pocket = 0;
		int targetPoints = 0;
		
		if(ap.isInArgs("-startbalance", true))
			balance = Integer.parseInt(ap.getArgValue("-startbalance"));
		if(ap.isInArgs("-startpocket", true))
			pocket = Integer.parseInt(ap.getArgValue("-startpocket"));
		int bankroll = balance + pocket;

		if(ap.isInArgs("-targetpoints", true))
			targetPoints = Integer.parseInt(ap.getArgValue("-targetpoints"));

		Player player = new Player(42, bankroll, targetPoints); 
		this.thePlayer = player;

		if(ap.isInArgs("-rollsperhour", true))
			rollsPerHour = Integer.parseInt(ap.getArgValue("-rollsperhour"));

		return bRval;

	}
	
	public void go() {

		msg = String.format("Startup with static pass/odds=10/20, bankroll=%d, %d rolls per hour\n", 
							thePlayer.bankroll, rollsPerHour);
		showMsg(msg,2);	

		for (int rollNumber=0; rollNumber<rollsPerHour; rollNumber++) {

			// If the player has less than the target number of points, place a bet. 
			if (bets.size() < thePlayer.targetPoints) {
				BetType betType = BetType.Pass;
				if (gameState == GameState.RollAtPoint)
					betType = BetType.Come;

				Bet aBet = thePlayer.placeBet(10, betType);
				bets.add(aBet);

			} 

			// Roll 
			int d1 = r.Next(1, 6);
			int d2 = r.Next(1, 6);
			int t = d1+d2;
			RollType rollType = evaluateRoll(d1, d2); 

			// Determine next action based on roll (e.g. addOdds) 
			if (!buttonOn) {
				// Come out roll...
				if (rollType == RollType.SevenEleven) {
					// Pass and come bets win 
					for(Bet aBet : bets) {
						if ((aBet.type == BetType.Pass) || (aBet.type == BetType.Come)) {
							// Payoff owning player 
							aBet.payWinner(thePlayer);
							aBet.bDeleteEligible = true;

						}  // type = Pass || Come 

					} // for aBet

				} // rollType = SevenEleven

				if (rollType == RollType.Craps) {
					// Pass and come bets loose b/c come out roll
					for(Bet aBet : bets) {
						if ((aBet.type == BetType.Pass) || (aBet.type == BetType.Come)) {
							// Note bet to be disposed
							aBet.bDeleteEligible = true;

						}

					} // for aBet 

				} // roll type = Craps

				if (rollType == RollType.Point) {
					// Pass and come bets establish their point 
					for(Bet aBet : bets) {
						if ((aBet.type == BetType.Pass) || (aBet.type == BetType.Come)) {
							// Set point for the bet
							aBet.onPoint = t;

							// Add odds to the bet 
							thePlayer.addOdds(20, aBet);

						}
					} // for aBet

					// Set game state and button 
					buttonOn = true;
					buttonPoint = t;
					gameState = GameState.PointEstablished;

				} // rollType = point 

			} // Button OFF 
			else {
				// Button ON...
				if (rollType == RollType.SevenEleven) {
					// Pass and come bets lose 
					for(Bet aBet : bets) {
						if ((aBet.type == BetType.Pass) || (aBet.type == BetType.Come)) {
							if (aBet.onPoint == -1) {
								// Newly established pass/come wins on 7/11 
								// Payoff owning player 
								aBet.payWinner(thePlayer);
								aBet.bDeleteEligible = true; // not needed, done inside payWinner() 
							}
							else {
								// Point established bets loose on 7/11
								// Note bet to be disposed
								aBet.bDeleteEligible = true; 
							}

						}  // type = Pass || Come 

					} // for aBet

					buttonOn = false;
					buttonPoint = -1;
					gameState = GameState.ComeOutRoll;

				} // rollType = SevenEleven

				if (rollType == RollType.Craps) {
					// newly placed pass and come bets loose 
					for(Bet aBet : bets) {
						if (((aBet.type == BetType.Pass) || (aBet.type == BetType.Come))
							&& (aBet.onPoint == -1)) {
							// Note bet to be disposed
							aBet.bDeleteEligible = true; 

						}

					} // for aBet 					

				} // roll type = Craps

				if (rollType == RollType.Point) {
					// Newly placed Pass and come bets establish their point.
					// Established bets on the point pay off.
					for(Bet aBet : bets) {
						if ((aBet.type == BetType.Pass) || (aBet.type == BetType.Come)) {
							if (aBet.onPoint == -1) {
							// Establish point
							aBet.onPoint = t;
							thePlayer.addOdds(20, aBet);

							}
							else {
								// Point was hit, pay and dispose 
								aBet.payWinner(thePlayer);
								aBet.bDeleteEligible = true;

							}
						}

					} // for aBet

					// Set game state and button 
					buttonOn = true;
					buttonPoint = t;
					gameState = GameState.PointEstablished;

				} // rollType = point 

			} // if else button

			// Remove bets marked as delete eligible by keeping the others
			ArrayList<Bet> keepBets = new ArrayList<Bet>(); 
			for(Bet aBet : bets) {
				if (!aBet.bDeleteEligible)
					keepBets.add(aBet); 
			}

			bets = keepBets;

		} // for rollNumber

		msg = String.format("%d end bankroll", thePlayer.bankroll);
		showMsg(msg, 1);


	}

	private RollType evaluateRoll(int d1, int d2) {

		int t = d1 + d2;
		RollType rollType = RollType.Point; 

		// Evaluate Roll
		if ((t == 7) || (t == 11)) {
			rollType = RollType.SevenEleven;

		}

		if ((t == 2) || (t == 3) || (t == 12)) {
			rollType = RollType.Craps;

		}

		return rollType; 

	}

	private void showMsg(String msg, int level) { 

		if ((maxMsgLevel == -1) || (level <= maxMsgLevel)) 
			System.out.println(msg);

	}

}