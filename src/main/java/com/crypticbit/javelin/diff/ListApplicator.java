package com.crypticbit.javelin.diff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import difflib.DiffUtils;

public final class ListApplicator implements Applicator<List> {
    
    public void apply(List list,  List<ListDelta> deltas) {
	UnorderedIndexedWritesListDecorator workingList = new UnorderedIndexedWritesListDecorator(list);
	for (ListDelta d : deltas) {
	    workingList.addMode(d.branch);
	}
	Map<Integer, ThreeWayDiff> recursiveDiffs = new HashMap<>();
	for (ListDelta ld : deltas) {
	    ld.apply(workingList, recursiveDiffs);
	}
	for (Entry<Integer, ThreeWayDiff> twds : recursiveDiffs.entrySet()) {
	    workingList.set(twds.getKey(), twds.getValue().getPatch().apply((List) workingList.get(twds.getKey())));
	}
    }

    public ListDelta getDelta(Object parent, Object child, Object branch) {
	return new ListDelta(DiffUtils.diff((List) parent, (List) child), branch);
    }

    @Override
    public boolean supports(Object a, Object b) {
	return a instanceof List && b instanceof List;
    }
}