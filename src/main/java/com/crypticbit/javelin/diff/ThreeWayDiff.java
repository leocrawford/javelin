package com.crypticbit.javelin.diff;

import java.util.*;

/** Performa three way diff for a range of inbuilt types, which can be extended by adding them to the DifferFactory */
public class ThreeWayDiff<T> {

    private Object commonAncestor;
    private List<Snapshot<T>> list = new LinkedList<>();
    private DifferFactory applicatorFactory;

    public ThreeWayDiff(Object commonAncestor, DifferFactory applicatorFactory) {
	this.commonAncestor = commonAncestor;
	this.applicatorFactory = applicatorFactory;
    }

    public ThreeWayDiff(Object commonAncestor) {
	this(commonAncestor, new DifferFactory());
    }

    /** Add a snapshot with a specified date - can be out of order */
    public void addBranchSnapshot(Date date, T object, Object branch) {
	list.add(new Snapshot<T>(date, object, branch));
    }

    /** Add a snapshot with no specified date, assumes they are given in order from oldest to youngest */
    public void addBranchSnapshot(Object object, Object branch) {
	list.add(new Snapshot(null, object, branch));
    }

    public SequenceDiff<T,?> getPatch() {
	List<Snapshot<T>> workingList = list;
	SequenceDiff<T,?> result = applicatorFactory.createApplicator(commonAncestor);
	Map<Object, Object> parents = new HashMap<>();
	for (Snapshot<T> snapshot : workingList) {
	    Object parent = parents.get(snapshot.getBranch());
	    if (parent == null) {
		parent = commonAncestor;
	    }
	    result.add(snapshot.getDate(), parent, snapshot.getObject(), snapshot.getBranch());
	    parents.put(snapshot.getBranch(), snapshot.getObject());
	}
	return result;
    }

}
