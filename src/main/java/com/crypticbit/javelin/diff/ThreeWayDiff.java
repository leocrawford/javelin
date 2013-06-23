package com.crypticbit.javelin.diff;

import java.util.*;

public class ThreeWayDiff {

    private Object commonAncestor;
    private List<Snapshot> list = new LinkedList<>();
    private static final List<Applicator> applicators = new LinkedList<>();
    static {
	applicators.add(new ListApplicator());
    }

    public ThreeWayDiff(Object commonAncestor) {
	this.commonAncestor = commonAncestor;
    }

    /** Add a snapshot with a specified date - can be out of order */
    public void addBranchSnapshot(Date date, Object object, Object branch) {
	list.add(new Snapshot(date, object, branch));
    }

    /** Add a snapshot with no specified date, assumes they are given in order from oldest to youngest */
    public void addBranchSnapshot(Object object, Object branch) {
	list.add(new Snapshot(null, object, branch));
    }

    public SpecialPatch getPatch() {
	List<Snapshot> workingList = list;
	SpecialPatch result = new SpecialPatch();
	Map<Object, Object> parents = new HashMap<>();
	for (Snapshot snapshot : workingList) {
	    Object parent = parents.get(snapshot.getBranch());
	    if (parent == null)
		parent = commonAncestor;
	    result.add(snapshot.getDate(), createDelta(parent, snapshot.getObject(), snapshot.getBranch()));
	    parents.put(snapshot.getBranch(), snapshot.getObject());
	}
	return result;
    }

    private ListDelta createDelta(Object parent, Object child, Object branch) {
	for (Applicator a : applicators)
	    if (a.supports(parent, child))
		return a.getDelta(parent, child, branch);
	return null;
    }

    /*
     * public void patch(SpecialPatch patch) throws PatchFailedException { System.out.println("Old: " + backingList);
     * patch.apply(backingList); System.out.println("New: " + backingList); }
     */

}
