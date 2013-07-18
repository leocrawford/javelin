package com.crypticbit.javelin.diff.list;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ItemDelta;
import com.crypticbit.javelin.diff.ThreeWayDiff;

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
	UnorderedIndexedWritesListDecorator unorderedIndexedWriter = (UnorderedIndexedWritesListDecorator) list;
	try {
	    for (Delta d : patch.getDeltas()) {
		if(LOG.isLoggable(Level.FINEST))
		    LOG.log(Level.FINEST, "Merge List @ "+d.getOriginal().getPosition()+" = "+d.getType());
		if (d.getType() != Delta.TYPE.CHANGE) {
		    d.applyTo(unorderedIndexedWriter.chooseMode(branch));
		}
		else {
		    unorderedIndexedWriter.chooseMode(branch);
		    // FIXME assumes both change are of same length
		    for (int loop = 0; loop < d.getOriginal().size() && loop < d.getRevised().size(); loop++) {
			if(d.getRevised().getLines().get(loop).toString().equals("h"))
			System.out.println("nn");
			int key = d.getOriginal().getPosition() + loop;
			// FIXME - use the filter
			int transformedIndex = unorderedIndexedWriter.transformIndexForAccess(key);
			Object o = unorderedIndexedWriter.get(transformedIndex);
			// we use the set rather than <code>instanceof</code> because it is legal to add any type of
			// object, including ThreeWayDiff.
			if (!recursiveDiffs.contains(o)) {
			    // FIXME copy state into new three way diff if required
			    ThreeWayDiff twd = new ThreeWayDiff(d.getOriginal().getLines().get(loop));
			    unorderedIndexedWriter.set(transformedIndex, twd);
			    recursiveDiffs.add(twd);
			    o = twd;
			}
			((ThreeWayDiff)o).addBranchSnapshot(d.getRevised().getLines().get(loop), branch);
			System.out.println("-->"+list+","+d+","+loop);

		    }
		    // if the original was longer..
		    for(int loop=d.getRevised().size(); loop< d.getOriginal().size(); loop++) {
			System.out.println("deleting "+loop+", "+d.getOriginal().getPosition());
			unorderedIndexedWriter.remove(loop+d.getOriginal().getPosition());
		    }
		    // if the original was shorter..
		    for(int loop=d.getOriginal().size(); loop< d.getRevised().size(); loop++) {
			System.out.println("adding "+loop+", "+d.getOriginal().getPosition());
			unorderedIndexedWriter.add(loop+d.getOriginal().getPosition(), d.getRevised().getLines().get(loop));
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
