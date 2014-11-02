package com.crypticbit.javelin.diff.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class MapSequenceDiff<T> extends SequenceDiff<Map<String, T>, MapDelta> {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");

    @Override
    public Map<String, T> apply(Map<String, T> value) {

	Map<String, T> result = new HashMap<>(value);

	// if there is a change we're going to need to apply a recursive diff
	Map<String, ThreeWayDiff<T>> recursiveDiffs = new HashMap<>();

	List<MapDelta> listOfDeltaInOrder = getListOfDeltaInOrder();
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Applying these " + listOfDeltaInOrder.size() + " map deltas in order "
		    + listOfDeltaInOrder);
	}

	for (MapDelta d : listOfDeltaInOrder) {
	    if (LOG.isLoggable(Level.FINEST)) {
		LOG.log(Level.FINEST, "Applying map delta " + d + " to " + result);
	    }
	    d.apply(result, recursiveDiffs);
	}

	// now catch up with the set of recursive diffs
	for (Entry<String, ThreeWayDiff<T>> twds : recursiveDiffs.entrySet()) {
	    result.put(twds.getKey(), twds.getValue().apply());
	}

	return result;
    }

    @Override
    protected MapDelta createDelta(Map<String, T> parent, Map<String, T> child, Object branch) {
	MapDifference<String, T> diff = Maps.difference(parent, child);
	return new MapDelta(diff, branch);
    }
}