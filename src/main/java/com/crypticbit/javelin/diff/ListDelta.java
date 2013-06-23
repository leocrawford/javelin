package com.crypticbit.javelin.diff;

import java.util.List;
import java.util.Map;

import difflib.Delta;
import difflib.Patch;
import difflib.PatchFailedException;

public class ListDelta {

    private Patch patch;
    Object branch;

    public ListDelta(Patch patch, Object branch) {
	this.patch = patch;
	this.branch = branch;
    }

    /**
     * It's not a simple matter of applying diffs. There is pre and post processing, which can be done here in a type
     * independant way
     * 
     * @param deltas
     * @return
     */
    public static Applicator<List> getApplicator() {
	// FIXME should on create in one way
	return new ListApplicator();
    }

    public void apply(List list, Map<Integer, ThreeWayDiff> recursiveDiffs) {
	try {
	    for (Delta d : patch.getDeltas()) {
		System.out.println("Patch "+d.getType()+", " + d.getOriginal().getPosition());
		if (d.getType() != Delta.TYPE.CHANGE)
		    d.applyTo(((UnorderedIndexedWritesListDecorator) list).chooseMode(branch));
		else {
		     // FIXME - add deal with change
		    // FIXME assumes both change are of same length
		    for (int loop = 0; loop < d.getOriginal().size(); loop++) {
			int key = d.getOriginal().getPosition() + loop;
			if (!recursiveDiffs.containsKey(key)) {
			    // FIXME copy state into new three way diff if required
			    recursiveDiffs.put(key, new ThreeWayDiff(d.getOriginal().getLines().get(loop)));
			}
			recursiveDiffs.get(key).addBranchSnapshot(d.getRevised().getLines().get(loop), branch);

		    }
		}
	    }
	}
	catch (PatchFailedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new Error();
	}
    }

    public String toString() {
	StringBuffer p = new StringBuffer();
	for (Delta d : patch.getDeltas())
	    p.append(d);
	return branch + ":" + p;
    }

}
