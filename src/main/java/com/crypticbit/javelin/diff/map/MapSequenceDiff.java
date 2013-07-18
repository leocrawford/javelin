package com.crypticbit.javelin.diff.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class MapSequenceDiff<T> extends SequenceDiff<Map<String, T>, MapDelta> {
    @Override
    public Map<String, T> apply(Map<String, T> value) {
	
	Map<String, T> result = new HashMap<>(value);
	
	// if there is a change we're going to need to apply a recursive diff
	Map<String, ThreeWayDiff<T>> recursiveDiffs = new HashMap<>();

	for (MapDelta d : getListOfDeltaInOrder()) {
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