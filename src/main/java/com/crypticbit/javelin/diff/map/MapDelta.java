package com.crypticbit.javelin.diff.map;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ItemDelta;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.google.common.base.Function;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

public class MapDelta<T> implements ItemDelta {

    private MapDifference<String, T> diff;
    private Object branch;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");

    public MapDelta(MapDifference<String, T> diff, Object branch) {
	this.diff = diff;
	this.branch = branch;
    }

    public void apply(Map<String, T> object, Map<String, ThreeWayDiff> recursiveDiffs) {

	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Merge Map = " + diff + " to " + object);
	}

	for (String key : getRemoved().keySet()) {
	    object.remove(key);
	}
	for (String key : getAdded().keySet()) {
	    putWithrecursive(key, object, getAdded(), recursiveDiffs);
	}
	for (String key : diff.entriesDiffering().keySet()) {
	    putWithrecursive(key, object, Maps.transformValues(diff.entriesDiffering(),
		    new Function<ValueDifference<T>, T>() {
			@Override
			public T apply(ValueDifference<T> input) {
			    return input.rightValue();
			}

			@Override
			public boolean equals(Object object) {
			    throw new Error();
			}

		    }), recursiveDiffs);
	}

	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Merge Map Result = " + object);
	}

    }

    public Map<String, T> getAdded() {
	return diff.entriesOnlyOnRight();
    }

    @Override
    public Object getBranch() {
	return branch;
    }

    public Map<String, T> getRemoved() {
	return diff.entriesOnlyOnLeft();
    }

    @Override
    public String toString() {
	return diff + " [" + branch + "]";
    }

    private void handleRecursiveDiff(String key, T value, Map<String, ThreeWayDiff> recursiveDiffs) {
	if (recursiveDiffs.containsKey(key)) {
	    recursiveDiffs.get(key).addBranchSnapshot(value, getBranch());
	}
	else {
	    recursiveDiffs.put(key, new ThreeWayDiff<>(value));
	}

    }

    private void putWithrecursive(String key, Map<String, T> existing, Map<String, T> replacement,
	    Map<String, ThreeWayDiff> recursiveDiffs) {
	if (existing.containsKey(key) && !recursiveDiffs.containsKey(key)) {
	    handleRecursiveDiff(key, existing.get(key), recursiveDiffs);
	}
	if (recursiveDiffs.containsKey(key)) {
	    handleRecursiveDiff(key, replacement.get(key), recursiveDiffs);
	}
	else {
	    existing.put(key, replacement.get(key));
	}
    }

}
