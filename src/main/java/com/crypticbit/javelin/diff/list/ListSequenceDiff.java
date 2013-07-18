package com.crypticbit.javelin.diff.list;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.ThreeWayDiff;

import difflib.DiffUtils;

public class ListSequenceDiff<T> extends SequenceDiff<List<T>, ListDelta> {
    public List<T> apply(List<T> list) {

	// well access the list through a clever adapter than allows each write to use indexes that assume
	// changes in other branches haven't been made
	UnorderedIndexedWritesListDecorator<T> workingList = new UnorderedIndexedWritesListDecorator<T>(list);

	// if there is a change we're going to need to apply a recursive diff
	Set<ThreeWayDiff> recursiveDiffs = new HashSet<>();

	// call the apply method
	for (ListDelta ld : getListOfDeltaInOrder()) {
	    ld.apply(workingList, recursiveDiffs);
	}

	for (int loop = 0; loop < workingList.size(); loop++) {
	    if (recursiveDiffs.contains(workingList.get(loop))) {
		ThreeWayDiff<T> threeWayDiff = (ThreeWayDiff<T>) workingList.get(loop);
		T processedRecursiveDiff = threeWayDiff.apply();
		workingList.set(loop, processedRecursiveDiff);
	    }
	}

	return list;
    }

    public ListDelta createDelta(List<T> parent, List<T> child, Object branch) {
	return new ListDelta(DiffUtils.diff(parent, child), branch);
    }
}