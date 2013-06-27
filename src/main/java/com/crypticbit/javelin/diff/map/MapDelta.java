package com.crypticbit.javelin.diff.map;

import java.util.Map;

import com.crypticbit.javelin.diff.ItemDelta;
import com.crypticbit.javelin.diff.ThreeWayDiff;

public class MapDelta<T> implements ItemDelta {

    private Map<String, T> removed, added, changed;
    private Object branch;

    public MapDelta(Map<String, T> removed, Map<String, T> added, Map<String, T> changed, Object branch) {
	this.removed = removed;
	this.added = added;
	this.changed = changed;
	this.branch = branch;
    }

    public String toString() {
	return "-" + removed + " +" + added + " ~" + changed + " [" + branch + "]";
    }

    public void apply(Map<String, T> object, Map<String, ThreeWayDiff> recursiveDiffs) {
	for (String key : removed.keySet())
	    object.remove(key);
	for (String key : added.keySet())
	    putWithrecursive(key, object, added, recursiveDiffs);
	for (String key : changed.keySet()) {
	    putWithrecursive(key, object, changed, recursiveDiffs);
	}
    }

    private void putWithrecursive(String key, Map<String, T> existing, Map<String, T> replacement,
	    Map<String, ThreeWayDiff> recursiveDiffs) {
	if (existing.containsKey(key) && !recursiveDiffs.containsKey(key)) {
	    handleRecursiveDiff(key, existing.get(key), recursiveDiffs);
	}
	if (recursiveDiffs.containsKey(key))
	    handleRecursiveDiff(key, replacement.get(key), recursiveDiffs);
	else
	    existing.put(key, replacement.get(key));
    }

    private void handleRecursiveDiff(String key, T value, Map<String, ThreeWayDiff> recursiveDiffs) {
	if (recursiveDiffs.containsKey(key)) {
	    recursiveDiffs.get(key).addBranchSnapshot(value, getBranch());
	}
	else {
	    recursiveDiffs.put(key, new ThreeWayDiff<>(value));
	}

    }

    @Override
    public Object getBranch() {
	return branch;
    }

}
