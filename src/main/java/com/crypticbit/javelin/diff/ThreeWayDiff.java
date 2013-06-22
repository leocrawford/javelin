package com.crypticbit.javelin.diff;

import java.util.*;

import com.crypticbit.javelin.js.LazyJsonArray;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class ThreeWayDiff {

    private Object commonAncestor;
    private List<Snapshot> list = new LinkedList<>();

    public ThreeWayDiff(Object commonAncestor) {
	this.commonAncestor = commonAncestor;
    }

    /** Add a snapshot with a specified date - can be out of order */
    public void addBranchSnapshot(Date date, Object object, Object branch) {
	list.add(new Snapshot(date,object, branch));
    }

    /** Add a snapshot with no specified date, assumes they are given in order from oldest to youngest */
    public void addBranchSnapshot(Object object, Object branch) {
	list.add(new Snapshot(null,object, branch));
    }

    public SpecialPatch getPatch() {
	List<Snapshot> workingList = list;
	SpecialPatch result = new SpecialPatch();
	Map<Object, Object> parents = new HashMap<>();
	for (Snapshot snapshot : workingList) {
	    Object parent = parents.get(snapshot.getBranch());
	    if (parent == null)
		parent = commonAncestor;
	    result.add(snapshot.getDate(),createDelta(parent,snapshot.getObject(),snapshot.getBranch()));
	    parents.put(snapshot.getBranch(), snapshot.getObject());
	}
	return result;
    }

    // FIXME - maybe an interface that stuff can provide their own implementation
    private ListDelta createDelta(Object parent, Object child, Object branch) {
	if (parent instanceof List && child instanceof List)
//	    return new ListDelta(((List) parent).diff((List) child),branch);
		return new ListDelta(DiffUtils.diff((List)parent, (List)child),branch);	
	else
	    return null;
    }

/*    public void patch(SpecialPatch patch) throws PatchFailedException {
	System.out.println("Old: " + backingList);
	patch.apply(backingList);
	System.out.println("New: " + backingList);
    } */

    
}
