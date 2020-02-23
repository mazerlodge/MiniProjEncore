package com.spsw;

public class ArgParser {

    private String[] args;

    public ArgParser(String[] argsArray) {

	args = argsArray;

    }

    private int getArgIndex(String argName) {
	// Return the index of the *first* occurrence of the specified 
	//  argName, -1 if not found.

	int rval = -1;

	for (int idx=0; idx<args.length; idx++) 
	    if (argName.equalsIgnoreCase(args[idx])) {
		// get the index where the arg was found
		rval = idx;
		break;
	    }

	return rval;

    } // getArgIndex

    public boolean isInArgs(String argName, boolean bWithValue) {
	// Returns true if the specified arg is in the args array,
	//  with value if specified.

	boolean bRval = false;

	// Check the index of the argName, -1 means not found.
	int idx = getArgIndex(argName);
	if (idx != -1) {
	    if (!bWithValue) {
		bRval = true;
	    }
	    else {
		// there must be something after the arg
		if (args.length > idx+1)
		    bRval = true;
		
	    }
	} // for idx

	return bRval;

    } // isInArgs

    public String getArgValue(String argName) {
	// Return the value associated with argName.
	//  Returns "NOT_SET" if not found.

	String rval = "NOT_SET";

	// argName must exist w/ a value to continue.
	if (!isInArgs(argName, true)) 
	    return rval;

	// Check the index of the argName, -1 means not found.
	int idx = getArgIndex(argName);
	if (idx != -1) 
	    // get the value after the current index
	    rval = args[idx+1];

	return rval;

    } // getArgValue

    public boolean isArgWithValue(String argName, String value) {

	boolean bRval = isInArgs(argName, true);

	// argName must be found w/ a value to continue.
	if (!bRval)
	    return bRval;

	// Check the index of the argName, -1 means not found.
	int idx = getArgIndex(argName);
	if (idx != -1) 
	    // Check the value after the current index
	    if (args[idx+1] == value)
		bRval = true;
      
	return bRval;

    } //isArgWithValue 

} // ArgParser

    