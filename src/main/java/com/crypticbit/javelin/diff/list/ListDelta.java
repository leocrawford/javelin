package com.crypticbit.javelin.diff.list;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.diff.ItemDelta;
import com.crypticbit.javelin.diff.ThreeWayDiff;

import difflib.Delta;
import difflib.Patch;
import difflib.PatchFailedException;

public class ListDelta implements ItemDelta {

    private Patch patch;
    Object branch;

    public ListDelta(Patch patch, Object branch) {
	this.patch = patch;
	this.branch = branch;
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


    public Object getBranch() {
	return branch;
    }

}
