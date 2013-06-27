package com.crypticbit.javelin.diff.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.CollectionDiffer;
import com.crypticbit.javelin.diff.ThreeWayDiff;

import difflib.DiffUtils;

public final class ListApplicator implements DifferFactoryElement {

    @Override
    public boolean supports(Object object) {
	return object instanceof List;
    }

    public CollectionDiffer createApplicator() {
	return new CollectionDiffer<List,ListDelta>() {

	    public List apply(List list) {

		UnorderedIndexedWritesListDecorator workingList = new UnorderedIndexedWritesListDecorator(list);
		for (ListDelta d :  getListOfDeltaInOrder()) {
		    workingList.addMode(d.getBranch());
		}
		Map<Integer, ThreeWayDiff> recursiveDiffs = new HashMap<>();
		for (ListDelta ld : getListOfDeltaInOrder()) {
		    ld.apply(workingList, recursiveDiffs);
		}
		for (Entry<Integer, ThreeWayDiff> twds : recursiveDiffs.entrySet()) {
		    workingList.set(twds.getKey(), twds.getValue().getPatch().apply(
			    (List) workingList.get(twds.getKey())));
		}
		return list;
	    }

	    protected ListDelta createDelta(Object parent, Object child, Object branch) {
		return new ListDelta(DiffUtils.diff((List) parent, (List) child), branch);
	    }
	};
    }

}