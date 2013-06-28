package com.crypticbit.javelin.diff.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.ThreeWayDiff;

import difflib.DiffUtils;

public final class ListDiffer<T> implements DifferFactoryElement {

    @Override
    public boolean supports(Object object) {
	return object instanceof List;
    }

    public SequenceDiff<List<T>, ListDelta> createApplicator() {
	return new SequenceDiff<List<T>, ListDelta>() {

	    public List<T> apply(List<T> list) {
		// well access the list through a clever adapter than allows each write to use indexes that assume
		// changes in other branches haven't been made
		UnorderedIndexedWritesListDecorator<T> workingList = new UnorderedIndexedWritesListDecorator<T>(list);

		// but first we need to set up the clever list adapter so it knows about each branch before we start using it.
		for (ListDelta d : getListOfDeltaInOrder()) {
		    workingList.addMode(d.getBranch());
		}
		// if there is a change we're going to need to apply a recursive diff 
		Map<Integer, ThreeWayDiff> recursiveDiffs = new HashMap<>();
		
		// call the apply method
		for (ListDelta ld : getListOfDeltaInOrder()) {
		    ld.apply(workingList, recursiveDiffs);
		}
		
		// now catch up with the set of recursive diffs
		for (Entry<Integer, ThreeWayDiff> twds : recursiveDiffs.entrySet()) {
		    workingList.set(twds.getKey(), ((SequenceDiff<T, ListDelta>) twds.getValue().getPatch())
			    .apply((T) workingList.get(twds.getKey())));
		}
		return list;
	    }

	    protected ListDelta createDelta(Object parent, Object child, Object branch) {
		return new ListDelta(DiffUtils.diff((List<?>) parent, (List<?>) child), branch);
	    }
	};
    }

}