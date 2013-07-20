package com.crypticbit.javelin.diff.list;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ItemDelta;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.google.common.collect.Lists;

import difflib.Delta;
import difflib.Patch;
import difflib.PatchFailedException;

public class ListDelta implements ItemDelta {

    private Patch patch;
    private Object branch;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");

    public ListDelta(Patch patch, Object branch) {
	this.patch = patch;
	this.branch = branch;
    }

    public <T> void apply(List list, Set<ThreeWayDiff> recursiveDiffs) {
	MultiViewList<T> unorderedIndexedWriter = (MultiViewList) list;
	try {
	    MultiViewList branchView = unorderedIndexedWriter.getMode(branch);
	    for (Delta d : Lists.reverse(patch.getDeltas())) {
		if (LOG.isLoggable(Level.FINEST))
		    LOG.log(Level.FINEST, "Applying "+d+" to "+branchView);
		if (d.getType() != Delta.TYPE.CHANGE) {
		    d.applyTo(branchView);
		}
		else {
		    for (int loop = 0; loop < d.getOriginal().size() && loop < d.getRevised().size(); loop++) {
			int key = d.getOriginal().getPosition() + loop;
			Object o = branchView.get(key);
			// we use the set rather than <code>instanceof</code> because it is legal to add any type of
			// object, including ThreeWayDiff.
			if (!recursiveDiffs.contains(o)) {
			    // FIXME copy state into new three way diff if required
			    ThreeWayDiff twd = new ThreeWayDiff(d.getOriginal().getLines().get(loop));
			    branchView.set(key, twd);
			    recursiveDiffs.add(twd);
			    o = twd;
			}
			((ThreeWayDiff) o).addBranchSnapshot(d.getRevised().getLines().get(loop), branch);

		    }
		    // if the original was longer..
		    for (int loop = d.getRevised().size(); loop < d.getOriginal().size(); loop++) {
			branchView.remove(loop + d.getOriginal().getPosition());
		    }
		    // if the original was shorter..
		    for (int loop = d.getOriginal().size(); loop < d.getRevised().size(); loop++) {
			branchView.add(loop + d.getOriginal().getPosition(), d.getRevised().getLines().get(loop));
		    }

		}
	    }
	}
	catch (PatchFailedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new Error(e);
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
