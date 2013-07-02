package com.crypticbit.javelin.diff.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class MapDiffer<T> implements DifferFactoryElement {

    @Override
    public boolean supports(Object object) {
	return object instanceof Map;
    }

    @Override
    public SequenceDiff<Map<String, T>, MapDelta> createApplicator() {
	return new SequenceDiff<Map<String, T>, MapDelta>() {

	    @Override
	    public Map<String, T> apply(Map<String, T> value) {
		// if there is a change we're going to need to apply a recursive diff
		Map<String, ThreeWayDiff<T>> recursiveDiffs = new HashMap<>();

		for (MapDelta d : getListOfDeltaInOrder()) {
		    d.apply(value, recursiveDiffs);
		}

		// now catch up with the set of recursive diffs
		for (Entry<String, ThreeWayDiff<T>> twds : recursiveDiffs.entrySet()) {
		    value.put(twds.getKey(),  twds.getValue().apply());
		}

		return value;
	    }

	    @Override
	    protected MapDelta createDelta(Object parent, Object child, Object branch) {
		MapDifference<String, T> diff = Maps.difference((Map<String, T>) parent, (Map<String, T>) child);
		return new MapDelta(diff, branch);
	    }

	};
    }

}
