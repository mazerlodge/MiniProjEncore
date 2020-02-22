package com.spsw; 

public class MiniProjEncore {
	
	// Validate Point 2 is being managed correctly

	private final int passAmount = 10; // 15
	private final int oddsAmount = 20; // 30
	private final int secondsPerRoll = 42; // 22
	private final boolean isSecondPointEnabled = true;

	private final int MAX_HOURS = 4; // -1 for no limit
	private final int START_BALANCE = 100; // 100
	private final int START_POCKET = 200; // 710
	private final int TARGET_BALANCE = 550; // 1215

	private int hour = 0;
	private int rollCount = 0;

	private int balanceLow = 999; // Set to extremes to force init
	private int balanceHigh = -1;
	private int balance = START_BALANCE;
	private int pocket = START_POCKET;

	public static void main(final String[] args) {

		final MiniProjEncore mpe = new MiniProjEncore();

		mpe.doMiniProjEncore();
		// mpe.TestQRandom();

	}

	public void TestQRandom() {

		final QRandom r = new QRandom();

		final int[] results = new int[6];

		for (int idx = 0; idx < results.length; idx++)
			results[idx] = 0;

		for (int idx = 0; idx < 1000; idx++)
			results[r.Next(0, 5)]++;

		// print results
		for (int idx = 0; idx < results.length; idx++)
			System.out.printf("%d=%d  ", idx, results[idx]);

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

		System.out.printf("Startup with pass/odds=%d/%d, balance/pocket=%d/%d, %d rolls per hour\n", passAmount,
				oddsAmount, balance, pocket, 3600 / secondsPerRoll);

		for (int idx = 0; idx < 13; idx++) {
			maxLoops[idx] = 0;
			hitCount[idx] = 0;
			missCount[idx] = 0;
			pointSetCount[idx] = 0;

		}

		while ((balance >= (passAmount + oddsAmount)) && (balance + pocket < TARGET_BALANCE)) {
			hour++;

			if ((MAX_HOURS > -1) && (hour > MAX_HOURS))
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

						// set second point if not already set
						// and permitted by member variable
						if ((isSecondPointEnabled) && (point2 == -1)) {
							pointSetCount[point]++;
							point2 = t;
						}

					}

				} // end Button ON

				// If balance is too low, stop for-loop.
				if (balance < (passAmount + oddsAmount)) {
					System.out.printf("\n***LOW BALANCE, Walk away with bal/pocket=%d/%d after %2.1f hours\n\n",
							balance, pocket, (rollCount * 22) / 3600.0);
					break;
				}

			} // for x

			// System.out.println("Max Loops at each point (4-6:8-10)");
			for (int idx = 4; idx <= 6; idx++)
				System.out.print(maxLoops[idx] + "\t");

			System.out.print(" : ");

			for (int idx = 8; idx <= 10; idx++)
				System.out.print(maxLoops[idx] + "\t");

			System.out.println("  loop at point.");

			// output hits
			// System.out.println("Hits at each point (4-6:8-10)");
			for (int idx = 4; idx <= 6; idx++)
				System.out.print(hitCount[idx] + "\t");

			System.out.print(" : ");

			for (int idx = 8; idx <= 10; idx++)
				System.out.print(hitCount[idx] + "\t");

			System.out.printf("  hits.  FYI, 7/11=%d\n", sevenElevenCount);

			// output misses
			// System.out.println("Misses at each point (4-6:8-10)");
			for (int idx = 4; idx <= 6; idx++)
				System.out.print(missCount[idx] + "\t");

			System.out.print(" : ");

			for (int idx = 8; idx <= 10; idx++)
				System.out.print(missCount[idx] + "\t");

			System.out.printf("  misses. FYI, 2/3/12=%d\n", crapCount);

			// output avg to hit
			// System.out.println("Avg to hit at each point (4-6:8-10)");
			int av = 0;
			int denom = 0;
			for (int idx = 4; idx <= 6; idx++) {
				denom = hitCount[idx] + missCount[idx];
				av = (denom > 0) ? maxLoops[idx] / denom : 0;
				System.out.print(av + "\t");

			}

			System.out.print(" : ");

			for (int idx = 8; idx <= 10; idx++) {
				denom = hitCount[idx] + missCount[idx];
				av = (denom > 0) ? maxLoops[idx] / denom : 0;
				System.out.print(av + "\t");
			}
			System.out.println("  avg to hit");

			System.out.printf("End hour=%d bal/pocket=%d/%d (%d) hi/low bal=%d/%d rollCount=%d\n\n", hour, balance,
					pocket, balance + pocket, balanceHigh, balanceLow, rollCount);

			// adjust balance and pocket amounts
			balanceHigh = balance;
			balanceLow = balance;
			if (balance > START_BALANCE) {
				final int overage = balance - START_BALANCE;
				pocket += overage;
				balance = START_BALANCE;
				System.out.printf("XFR TO Pocket %d, balance/pocket=%d/%d\n\n", overage, balance, pocket);
			} else {

				final int shortage = START_BALANCE - balance;
				if (pocket > shortage) {
					balance += shortage;
					pocket -= shortage;
					System.out.printf("XFR From Pocket %d, balance now %d\n", shortage, balance);

				} else {
					balance += pocket;
					if (pocket != 0)
						System.out.printf("XFR From Pocket %d, balance now %d\n", pocket, balance);
					pocket = 0;
				}

			}

		} // while balance > 0

		if (balance + pocket >= TARGET_BALANCE)
			System.out.printf("\n***HIGH BALANCE, Walk away with bal/pocket=%d/%d after %2.1f hours\n\n", balance,
					pocket, (rollCount * secondsPerRoll) / 3600.0);

		// output avg to hit
		System.out.println("Hit-to-Total % at points (4-6:8-10)");
		double htt = 0;
		int denom = 0;
		for (int idx = 4; idx <= 6; idx++) {
			denom = hitCount[idx] + missCount[idx];
			htt = (denom > 0) ? (1.0 * hitCount[idx]) / denom : 0;
			System.out.printf("%3.2f \t", htt);

		}

		System.out.print(" : ");

		for (int idx = 8; idx <= 10; idx++) {
			denom = hitCount[idx] + missCount[idx];
			htt = (denom > 0) ? (1.0 * hitCount[idx]) / denom : 0;
			System.out.printf("%3.2f \t", htt);
		}
		System.out.println("  hit to total");

		// output point set count
		System.out.println("Point set count at each point (4-6:8-10)");
		for (int idx = 4; idx <= 6; idx++)
			System.out.print(pointSetCount[idx] + "\t");

		System.out.print(" : ");

		for (int idx = 8; idx <= 10; idx++)
			System.out.print(pointSetCount[idx] + "\t");

		System.out.printf("  point Ct w/ 7-11=%d 2-3-12=%d\n", sevenElevenCount, crapCount);
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
			System.out.printf("Borrowed %d from pocket, new balance/pocket = %d/%d on roll %d\n",
							  pocketLoanAmount, balance, pocket, rollCount);		
	
		}
				
		if (balance < balanceLow)
			balanceLow = balance;
		
		if (balance > balanceHigh)
			balanceHigh = balance;

	}
}


