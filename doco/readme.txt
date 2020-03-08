
Command Line: 
java -cp /Users/mazerlodge/Documents/XProjects/Java/MiniProjEncore/bin com.spsw.MiniProjEncore -pass 10 -odds 20 -sec 42 -hours 1 -startbalance 300 -startpocket 700 -target 1130



Goal: 
	Target 13% increase over 1 hour at 10/20 w/ start total 1k. 

	Strategy 0: two point 10/20 1h survival 
		Just observe how often can retain bankroll after 1 hour 
		Starting bankroll 1k, pass/odds amounts at 10/20.
		Target 1001, stop on target turned on.
		42 seconds per roll (85 rolls per hour) 
		Two points turned on. 
		Run 1,200 times.
		Result:
			= 38% hit or exceeded retain bankroll target
			= avg end bankroll was 945 

	Strategy 1: 
		Starting bankroll 1k, pass/odds amounts at 10/20.
		Target 1130, stop on target turned on.
		42 seconds per roll (85 rolls per hour) 
		Two points turned on. 
		Run 1,200 times.
		Result:
			1h limit 
				= 38% (462/1200), hit target 1130, 
				= avg time was 44min
				= avg bal was 955
			2h limit (w/ retain balance on) 
				= 25% (258/1014) hit in first hour, avg time was 24 min 
				= 13% (52/379) entered second hour w/ target of 1130
				= 8% (33/379) exceeded 1130 tgt in second hour, avg time was 71 min
				= 22% (87/379) hit or exceeded 1k bankroll at end of second hour, avg time was 82 min
				= 48% (185/379) hit target in second hour, avg target was 919

		Slowing roll to 84 secs per roll lowers 1h win rate to 25%, and 2h rate goes down to 44% (w/ lower avg tgt)
			= 19% (444/2073) hit in first hour, avg time was 44min
			= 29% 259/873 hour 2 rows hit or exceeded 1k bankroll at end of second hour, avg time was 106 min 

	Strategy 2: (alt strategy, on points 4/10, place 6/8 and single odds on 4/10)
		Starting bankroll 1k, pass/odds amounts at 10/20, unless point 4/10, then 10/10 w/ place 6/8.
		Target 1130, stop on target turned on.
		42 seconds per roll (85 rolls per hour) 
		Two points turned on. 
		Run 1,200 times.
		Result:
			=30% (372/1200)

	Strategy 2S: alt strategy 10/20, 2 points, 1k, survival (no stop on target) 
		Result: 
			= 31% 
			= avg balance 907 (wins and loses)

	Strategy 3: Single Point 10/20, 1k survival
		Starting bankroll 1k, pass/odds amounts at 10/20
		Target 1000, stop on target turned off.
		42 seconds per roll (85 rolls per hour) 
		Single Point only. 
		Run 1,200 times.
		Result:
			=40% (476/1200) 
			= Avg bankroll 964


	Strategy 4: Single Point 10/40, 1k survival 
		Result: 
			=43% (521/1200) 
			= Avg bankroll 968

	Strategy 5: Single Point 10/40, 1030 stop on target  
		Result: 
			=43% (521/1200) 
			= Avg bankroll 968

	Strategy 6: Single Point 10/50, 1030 stop on target 
		Result:
			=46% (560/1200) 
			= Avg bankroll 982
			= Avg time was 38 mins 

Issues:
	X-- Determine if target is reached before 1h limit does scenario stop? 
		N-- Resolved, added command line param -stopontarget

	/-- Confirm accurate processing of two points at a time 
		N-- ID'd potentional problem with standing pass/come bet odds off on come out roll.

	
