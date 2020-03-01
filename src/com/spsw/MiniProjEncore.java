package com.spsw; 

public class MiniProjEncore {
	
	// Validate Point 2 is being managed correctly
	private ArgParser ap;

	private int maxMsgLevel = -1;
	private int passAmount = 10; // 15
	private int oddsAmount = 20; // 30
	private int secondsPerRoll = 42; // 22
	private boolean isSecondPointEnabled = true;
	private boolean bStopOnTarget = false; 
	private boolean bRetainBalance = false; 

	private int DEFAULT_MAX_HOURS = 1; // -1 for no limit
	private int START_BALANCE = 300; // 100
	private int START_POCKET = 700; // 710

	private int hour = 0;
	private int rollCount = 0;
	private int runCount = 1;

	private int balanceLow = 999; // Set to extremes to force init
	private int balanceHigh = -1;

	private int balance = START_BALANCE;
	private int pocket = START_POCKET;
	private int targetBalance = 1130; // 1215
	private int startupBankroll = balance + pocket;
	private int startupTarget = targetBalance;
	private int maxHours = DEFAULT_MAX_HOURS;

	private String startupMode = "RUN_SIM"; 
	private String msg = ""; // reusable string for message printing.

	public static void main(final String[] args) {

		final MiniProjEncore mpe = new MiniProjEncore(args);
		mpe.go();
		// mpe.TestQRandom();

	}

	public MiniProjEncore(String[] args) {

		ap = new ArgParser(args);

	}

	public void TestQRandom() {

		final QRandom r = new QRandom();

		final int[] results = new int[7];

		for (int idx = 0; idx < results.length; idx++)
			results[idx] = 0;

		for (int idx = 0; idx < 1000; idx++)
			results[r.Next(1, 6)]++;

		// print results
		msg = "";
		for (int idx = 0; idx < results.length; idx++)
			msg += String.format("%d=%d\t", idx, results[idx]);

		msg += "\n";
		showMsg(msg, 2);

	}

	private boolean parseArgs() {
		boolean bRval = true; 

		// -pass, -odds, -sec, -secondPoint {T|F} -hours 
		// -startbalance -startpocket -target -msglevel -showdefaults
		/*
			-pass = pass line amount
			-odds = odds bet amount (usually 2x pass) 
			-sec  = seconds per roll (used to determine rolls per hour)
			-secondPoint = T if second point should be enabled 
			-hours = hours limit or -1 for unlimited 
			-startbalance -startpocket = starting amounts on table and in pocket 
			-target = hitting this amount will stop the sim (combined balance + pocket)
			-msglevel = messages up to this level will be shown, 10 or 20 (20=detail) 
			-showdefaults = output the settings w/out consideration of parameters passed in. 
			-startup = run | dotest 
			-msglevel = max message level (higher = more detail), default is to show all.
			-bulkrun = number of times to repeat run (good w/ -msglevel 1)
			-stopontarget = if present sim run should stop when target is hit, if not run for full hour.
			-retainbalance = if present sim should use ending bal from previous hour as start balance and target set to percent increase based on original params.
		*/

		if(ap.isInArgs("-pass", true))
			this.passAmount = Integer.parseInt(ap.getArgValue("-pass"));

		if(ap.isInArgs("-odds", true))
			this.oddsAmount = Integer.parseInt(ap.getArgValue("-odds"));

		if(ap.isInArgs("-target", true))
			this.targetBalance = Integer.parseInt(ap.getArgValue("-target"));

		if(ap.isInArgs("-sec", true))
			this.secondsPerRoll = Integer.parseInt(ap.getArgValue("-sec"));

		if(ap.isInArgs("-hours", true))
			this.maxHours = Integer.parseInt(ap.getArgValue("-hours"));

		if(ap.isInArgs("-startbalance", true))
			this.balance = Integer.parseInt(ap.getArgValue("-startbalance"));

		if(ap.isInArgs("-startpocket", true))
			this.pocket = Integer.parseInt(ap.getArgValue("-startpocket"));

		if(ap.isInArgs("-msglevel", true))
			this.maxMsgLevel = Integer.parseInt(ap.getArgValue("-msglevel"));

		if(ap.isInArgs("-bulkrun", true))
			this.runCount = Integer.parseInt(ap.getArgValue("-bulkrun"));

		if(ap.isInArgs("-startup", true)) 
			this.startupMode = ap.getArgValue("-startup");

		if(ap.isInArgs("-stopontarget", false)) 
			bStopOnTarget = true;

		if(ap.isInArgs("-retainbalance", false)) 
			bRetainBalance = true;

		// Preserve key startup values 
		startupBankroll = balance + pocket;
		startupTarget = targetBalance;
	
		return bRval; 

	}

	private void evalTarget() {
		// Used when retain balance option is used, calculated between each hour of sim run time. 

		// Temp hold of balance information in case retain balance option is used. 
		int currBalance = balance;
		int currPocket = pocket; 

		// Calc original target percentage of bankroll
		double targetPercent = 1.0 * (startupTarget - startupBankroll) / startupBankroll;

		// If using Retain Balance option, calc new target if bankroll is below original amount.
		boolean bAdjustTarget = false;
		if ((currBalance + currPocket) < startupBankroll) 
			bAdjustTarget = true; 

		if (bRetainBalance && bAdjustTarget) {
			float newTarget = Math.round((balance + pocket) * (1.0 + targetPercent));
			targetBalance = Math.round(newTarget);

		}

	}

	private void resetSim() { 
		// Reset the simulator to startup position. 

		// Get balances back to where they were at the start 
		parseArgs();
		
		hour = 0;
		rollCount = 0;
		balanceHigh = -1;
		balanceLow = 9999;
	
	}

	private void showUsage() { 

		showMsg("Options are -pass, -odds -target -startup {run | dotest} -msglevel n  -stopontarget -retainBalance.\n", -1);

	}

	public void go() {

		if (!parseArgs()) {
			showUsage();
			return;
		}		

		if(startupMode.equals("dotest")) {
			for (int x=0; x<10; x++) 
				this.TestQRandom();
		}
		else {
			int winRunCount = 0;
			int loseRunCount = 0;
			int lowBalanceCount = 0;
			for (int x=0; x<runCount; x++) {
				resetSim();
				this.doMiniProjEncore();
				if ((balance + pocket) > targetBalance)
					winRunCount++;
				else
					loseRunCount++;
					if (balance < (passAmount + oddsAmount))
						lowBalanceCount++;
				
			}
			msg = String.format("Run level wins/loses = %d & %d  low_bal=%d rat= %f", 
									winRunCount, loseRunCount, lowBalanceCount, (1.0 * winRunCount/runCount));
			showMsg(msg, 1);
		}

	}

	public void doMiniProjEncore() {
		// Goal get Max test runs until point hit or lost


		int point = -1;
		int loopCount = 0;
		final int maxLoops[] = new int[13];
		final int hitCount[] = new int[13];
		final int missCount[] = new int[13];
		final int pointSetCount[] = new int[13];

		int point2 = -1;

		// count of craps or win on come out
		int crapCount = 0;
		int sevenElevenCount = 0;

		final QRandom r = new QRandom();

		msg = String.format("Startup with pass/odds=%d/%d, balance/pocket=%d/%d, %d rolls per hour\n", 
									passAmount,oddsAmount, balance, pocket, 3600 / secondsPerRoll);
		showMsg(msg,2);									

		for (int idx = 0; idx < 13; idx++) {
			maxLoops[idx] = 0;
			hitCount[idx] = 0;
			missCount[idx] = 0;
			pointSetCount[idx] = 0;

		}

		while ((balance >= (passAmount + oddsAmount)) && (balance + pocket < targetBalance)) {
			hour++;

			if ((maxHours > -1) && (hour > maxHours))
				break;

			// Based on rolls per hour
			for (int x = 0; x < (3600 / secondsPerRoll); x++) {

				final int d1 = r.Next(1, 6);
				final int d2 = r.Next(1, 6);
				final int t = d1 + d2;
				loopCount++;
				rollCount++;

				if (point == -1) {
					// Button is OFF

					if (((t >= 4) && (t <= 6)) || ((t >= 8) && (t <= 10))) {
						point = t;
						pointSetCount[point]++;
					}

					if ((t == 7) || (t == 11)) {
						sevenElevenCount++;
						adjustBalance(passAmount, t);
					}

					if ((t == 2) || (t == 3) || (t == 12)) {
						crapCount++;
						adjustBalance((-1 * passAmount), t);
					}

				} else {
					// Button is already ON

					if ((t == point) || (t == 7) || (t == point2)) {
						// point or 7 hit

						// add to loop count at this point
						maxLoops[t] += loopCount;

						if ((t != 7)) {
							hitCount[t]++;

							// see if point hit
							if (t == point) {
								adjustBalance(oddsAmount, t);
								point = -1;
							}

							// see if point2 hit
							if (t == point2) {
								adjustBalance(oddsAmount, t);
								point2 = -1;

							}
						} else {
							// t == 7, craps out pass line away.
							if (point != -1)
								missCount[point]++;

							if (point2 != -1)
								missCount[point2]++;

							// see if point lost
							if (point != -1) {
								adjustBalance((-1 * oddsAmount), point);
								point = -1;
							}

							// see if point 2 lost
							if (point2 != -1) {
								adjustBalance((-1 * oddsAmount), point2);
								point2 = -1;
							}

						}

						loopCount = 0;

					} // if point or 7
					else {
						// neither point nor 7, set second point?
						boolean bComeBetResolved = false;

						// COME bet pays instead of setting second point.
						if ((t == 7) || (t == 11)) {
							sevenElevenCount++;
							adjustBalance(passAmount, t);
							bComeBetResolved = true;
						}
	
						// COME bet looses instead of setting second point.
						if ((t == 2) || (t == 3) || (t == 12)) {
							crapCount++;
							adjustBalance((-1 * passAmount), t);
							bComeBetResolved = true;
						}						

						// set second point if not already set
						// and permitted by member variable
						if ((isSecondPointEnabled) && (point2 == -1) && !bComeBetResolved) {
							pointSetCount[point]++;
							point2 = t;
						}

					}

				} // end Button ON

				// If balance is too low, stop for-loop.
				if (balance < (passAmount + oddsAmount)) {
					 msg = String.format("\n***LOW BALANCE, Walk away with bal/pocket=%d/%d after %2.1f hours", balance, pocket, (rollCount * 22) / 3600.0);
					showMsg(msg, 1);
					break;
				}

				// If target hit during hour, stop. 
				if (balance+pocket >= targetBalance) {
					msg = String.format("\n***TARGET HIT, bal+pocket=%d\n", (balance+pocket));
					showMsg(msg,2);
					if (bStopOnTarget)
						break;
				}

			} // for x

			// Max Loops at each point (4-6:8-10)
			msg = "";
			for (int idx = 4; idx <= 6; idx++)
				msg += maxLoops[idx] + "\t";
			msg += " : ";

			for (int idx = 8; idx <= 10; idx++)
				msg += maxLoops[idx] + "\t";
			msg += "  loop at point.";

			showMsg(msg, 2);

			// output hits
			// Hits at each point (4-6:8-10)
			msg = "";
			for (int idx = 4; idx <= 6; idx++)
				msg += hitCount[idx] + "\t";
			msg += " : ";

			for (int idx = 8; idx <= 10; idx++)
				msg += hitCount[idx] + "\t";
			msg += String.format("  hits.  FYI, 7/11=%d\n", sevenElevenCount);
			showMsg(msg, 2); 

			// output misses
			// Misses at each point (4-6:8-10)
			msg = "";
			for (int idx = 4; idx <= 6; idx++)
				msg += missCount[idx] + "\t";
			msg += " : ";

			for (int idx = 8; idx <= 10; idx++)
				msg += missCount[idx] + "\t";
			msg += String.format("  misses. FYI, 2/3/12=%d\n", crapCount);

			showMsg(msg, 2);

			// output avg to hit
			//Avg to hit at each point (4-6:8-10)
			int av = 0;
			int denom = 0;
			msg = "";
			for (int idx = 4; idx <= 6; idx++) {
				denom = hitCount[idx] + missCount[idx];
				av = (denom > 0) ? maxLoops[idx] / denom : 0;
				msg += av + "\t";

			}
			msg += " : ";

			for (int idx = 8; idx <= 10; idx++) {
				denom = hitCount[idx] + missCount[idx];
				av = (denom > 0) ? maxLoops[idx] / denom : 0;
				msg += av + "\t";
			}
			msg += "  avg to hit";

			showMsg(msg, 2);

			double elapsedTime = (rollCount * secondsPerRoll)/60.0;
			msg = String.format("End hour=%d bal/pocket=%d/%d (%d) T=%d hi/low bal=%d/%d rollCount=%d time=%3.1f mins", 
					hour, balance, pocket, balance + pocket, targetBalance, balanceHigh, balanceLow, rollCount, elapsedTime);
			showMsg(msg, 1);

			// adjust balance and pocket amounts
			balanceHigh = balance;
			balanceLow = balance;
			if (balance > START_BALANCE) {
				final int overage = balance - START_BALANCE;
				pocket += overage;
				balance = START_BALANCE;

				msg = String.format("XFR TO Pocket %d, balance/pocket=%d/%d\n\n", overage, balance, pocket);
				showMsg(msg, 2);
			} else {

				final int shortage = START_BALANCE - balance;
				if (pocket > shortage) {
					balance += shortage;
					pocket -= shortage;
					msg = String.format("XFR From Pocket %d, balance now %d\n", shortage, balance);
					showMsg(msg, 2);

				} else {
					balance += pocket;
					if (pocket != 0) {
						msg = String.format("XFR From Pocket %d, balance now %d\n", pocket, balance);
						showMsg(msg, 2);
					}
					pocket = 0;
				}

			}

			// evaluate and adjust target for next hour. 
			evalTarget();

		} // while balance > 0
		
		if (balance + pocket >= targetBalance) {
			msg = String.format("\n***HIGH BALANCE, Walk away with bal/pocket=%d/%d after %2.1f hours\n\n", balance,
					pocket, (rollCount * secondsPerRoll) / 3600.0);
			showMsg(msg, 2);
		}

		// output avg to hit
		showMsg("Hit-to-Total % at points (4-6:8-10)",2);
		double htt = 0;
		int denom = 0;
		msg = "";
		for (int idx = 4; idx <= 6; idx++) {
			denom = hitCount[idx] + missCount[idx];
			htt = (denom > 0) ? (1.0 * hitCount[idx]) / denom : 0;
			msg += String.format("%3.2f \t", htt);

		}
		msg += " : ";

		for (int idx = 8; idx <= 10; idx++) {
			denom = hitCount[idx] + missCount[idx];
			htt = (denom > 0) ? (1.0 * hitCount[idx]) / denom : 0;
			msg += String.format("%3.2f \t", htt);
		}
		msg += "  hit to total";
		showMsg(msg, 2);

		// output point set count
		showMsg("Point set count at each point (4-6:8-10)", 2);
		msg = "";
		for (int idx = 4; idx <= 6; idx++)
			msg += pointSetCount[idx] + "\t";
		msg += " : ";

		for (int idx = 8; idx <= 10; idx++)
			msg += pointSetCount[idx] + "\t";

		msg += String.format("  point Ct w/ 7-11=%d 2-3-12=%d\n", sevenElevenCount, crapCount);
		showMsg(msg, 2);

	} // doMiniProjEncore()

	private void adjustBalance(final int amount, final int point) {
		// if amount is negative, amount was lost
		// if point is (7, 11) or (2, 3, 12) result is from come out roll
		// Note: These are total balance changes after an outcome,
		// not raw amount moved on the felt.

		switch (point) {

			case 7:
			case 11:
				balance += passAmount;
				break;

			case 2:
			case 3:
			case 12:
				balance -= passAmount;
				break;

			case 4:
			case 10:
				if (amount > 0)
					balance += passAmount + oddsAmount * 2;
				else
					balance -= passAmount + oddsAmount;
				break;

			case 5:
			case 9:
				if (amount > 0)
					balance += passAmount + oddsAmount * (3.0 / 2);
				else
					balance -= passAmount + oddsAmount;
				break;

			case 6:
			case 8:
				if (amount > 0)
					balance += passAmount + oddsAmount * (6.0 / 5);
				else
					balance -= passAmount + oddsAmount;
				break;

		}

		if (balance < (passAmount + oddsAmount)) {
			final int betSize = passAmount + oddsAmount;
			final int pocketLoanAmount = (pocket < betSize) ? pocket : betSize;
			
			balance += pocketLoanAmount;
			pocket -= pocketLoanAmount;

			msg = String.format("Borrowed %d from pocket, new balance/pocket = %d/%d on roll %d\n",
							  pocketLoanAmount, balance, pocket, rollCount);	
			showMsg(msg,2);	
	
		}
				
		if (balance < balanceLow)
			balanceLow = balance;
		
		if (balance > balanceHigh)
			balanceHigh = balance;

	}

	private void showMsg(String msg, int level) { 

		if ((maxMsgLevel == -1) || (level <= maxMsgLevel)) 
			System.out.println(msg);

	}
}


