package com.crypticbit.javelin.neo4j.strategies;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorComparison;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements a VectorClock that records the time stamps of all send and receive events. It contains functions to
 * compare and merge two VectorClocks.
 * <p>
 * Copied from http://tud-in4150-fp-ass2b.googlecode.com/svn/trunk /src/in4150/mutex/VectorClock.java
 * 
 * @author Frits de Nijs
 * @author Peter Dijkshoorn
 */
public class VectorClock extends HashMap<String, Integer> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4032596263224118604L;

    @Override
    public VectorClock clone() {
	return (VectorClock) super.clone();
    }

    @Override
    public Integer get(Object key) {
	Integer lResult = super.get(key);

	if (lResult == null) {
	    lResult = 0;
	}

	return lResult;
    }

    /**
     * GUI operation, returns the IDs in some neat order.
     * 
     * @return The IDs of the elements in the Clock.
     */
    public String[] getOrderedIDs() {
	String[] lResult = new String[this.size()];

	lResult = this.keySet().toArray(lResult);

	Arrays.sort(lResult);

	return lResult;
    }

    /**
     * GUI operation, returns the values in some neat order.
     * 
     * @return The Values of the elements in the Clock.
     */
    public Integer[] getOrderedValues() {
	Integer[] lResult = new Integer[this.size()];
	String[] lKeySet = this.getOrderedIDs();

	int i = 0;
	for (String lKey : lKeySet) {
	    lResult[i] = this.get(lKey);
	    i++;
	}

	return lResult;
    }

    /**
     * Increases the component of pUnit by 1.
     * 
     * @param pUnit
     *            - The ID of the vector element being increased.
     */
    public void incrementClock(String pUnit) {
	// If we have it in the vector, increment.
	if (this.containsKey(pUnit)) {
	    this.put(pUnit, this.get(pUnit).intValue() + 1);
	}
	// Else, store with value 1 (starts at 0, +1).
	else {
	    this.put(pUnit, 1);
	}
    }

    public String serializeToString() throws JsonProcessingException {
	ObjectMapper mapper = new ObjectMapper();
	return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
	String[] lIDs = this.getOrderedIDs();
	Integer[] lRequests = this.getOrderedValues();

	String lText = "(";

	for (int i = 0; i < lRequests.length; i++) {
	    lText += lIDs[i];
	    lText += " = ";
	    lText += lRequests[i].toString();

	    if (i + 1 < lRequests.length) {
		lText += ", ";
	    }
	}

	lText += ")";

	return lText;
    }

    /**
     * VectorClock compare operation. Returns one of four possible values indicating how clock one relates to clock two:
     * VectorComparison.GREATER If One > Two. VectorComparison.EQUAL If One = Two. VectorComparison.SMALLER If One <
     * Two. VectorComparison.SIMULTANEOUS If One <> Two.
     * 
     * @param pOne
     *            - First Clock being compared.
     * @param pTwo
     *            - Second Clock being compared.
     * @return VectorComparison value indicating how One relates to Two.
     */
    public static VectorComparison compare(VectorClock pOne, VectorClock pTwo) {
	// Initially we assume it is all possible things.
	boolean lEqual = true;
	boolean lGreater = true;
	boolean lSmaller = true;

	// Go over all elements in Clock one.
	for (String lEntry : pOne.keySet()) {
	    // Compare if also present in clock two.
	    if (pTwo.containsKey(lEntry)) {
		// If there is a difference, it can never be equal.
		// Greater / smaller depends on the difference.
		if (pOne.get(lEntry) < pTwo.get(lEntry)) {
		    lEqual = false;
		    lGreater = false;
		}
		if (pOne.get(lEntry) > pTwo.get(lEntry)) {
		    lEqual = false;
		    lSmaller = false;
		}
	    }
	    // Else assume zero (default value is 0).
	    else if (pOne.get(lEntry) != 0) {
		lEqual = false;
		lSmaller = false;
	    }
	}

	// Go over all elements in Clock two.
	for (String lEntry : pTwo.keySet()) {
	    // Only elements we have not found in One still need to be
	    // checked.
	    if (!pOne.containsKey(lEntry) && (pTwo.get(lEntry) != 0)) {
		lEqual = false;
		lGreater = false;
	    }
	}

	// Return based on determined information.
	if (lEqual) {
	    return VectorComparison.EQUAL;
	}
	else if (lGreater && !lSmaller) {
	    return VectorComparison.GREATER;
	}
	else if (lSmaller && !lGreater) {
	    return VectorComparison.SMALLER;
	}
	else {
	    return VectorComparison.SIMULTANEOUS;
	}
    }

    public static VectorClock deSerialize(String serialized) throws JsonParseException, JsonMappingException,
	    IOException {
	ObjectMapper mapper = new ObjectMapper();
	VectorClock vc = mapper.readValue(serialized, VectorClock.class);
	return vc;
    }

    /**
     * VectorClock merging operation. Creates a new VectorClock with the maximum for each element in either clock. Used
     * in Buffer and Process to manipulate clocks.
     * 
     * @param pOne
     *            - First Clock being merged.
     * @param pTwo
     *            - Second Clock being merged.
     * @return A new VectorClock with the maximum for each element in either clock.
     */
    public static VectorClock max(VectorClock pOne, VectorClock pTwo) {
	// Create new Clock.
	VectorClock lResult = new VectorClock();

	// Go over all elements in clock One, put them in the new clock.
	for (String lEntry : pOne.keySet()) {
	    lResult.put(lEntry, pOne.get(lEntry));
	}

	// Go over all elements in clock Two,
	for (String lEntry : pTwo.keySet()) {
	    // Insert the Clock Two value if it is not present in One, or if
	    // it is higher.
	    if (!lResult.containsKey(lEntry) || lResult.get(lEntry) < pTwo.get(lEntry)) {
		lResult.put(lEntry, pTwo.get(lEntry));
	    }
	}

	// Return the merged clock.
	return lResult;
    }

}