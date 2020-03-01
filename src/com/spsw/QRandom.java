package com.spsw; 

public class QRandom {

	public int Next(int min, int max) {
		
		// Return a random int between min and max
		int rval = (int) Math.floor((Math.random() * (max+1-min))+min);
		
		return rval;
		
	}
}
