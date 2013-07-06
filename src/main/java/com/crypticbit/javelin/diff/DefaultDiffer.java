package com.crypticbit.javelin.diff;

import java.util.List;

public class DefaultDiffer implements DifferFactoryElement {

    @Override
    public boolean supports(Object object) {
	return true;
    }

    @Override
    public SequenceDiff createApplicator() {
	return new SequenceDiff() {

	    @Override
	    public Object apply(Object value) {
		System.out.println("Returing last element of:" + getListOfDeltaInOrder());
		List<DefaultItemDelta> l = getListOfDeltaInOrder();
		return l.get(l.size() - 1).object;
	    }

	    @Override
	    protected ItemDelta createDelta(Object parent, Object child, Object branch) {
		System.out.println("Creating delta for:" + parent + " -> " + child);
		return new DefaultItemDelta(child, branch);
	    }
	};
    }

    static class DefaultItemDelta implements ItemDelta {
	private Object object, branch;

	DefaultItemDelta(Object object, Object branch) {
	    this.object = object;
	    this.branch = branch;
	}

	@Override
	public Object getBranch() {
	    return branch;
	}

	public String toString() {
	    return object + " [" + branch + "]";
	}
    }

}
