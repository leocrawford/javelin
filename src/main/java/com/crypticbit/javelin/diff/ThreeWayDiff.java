package com.crypticbit.javelin.diff;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performa three way diff for a range of inbuilt types, which can be extended by adding them to the DifferFactory
 */
public class ThreeWayDiff<T> {

    // FIXME - will this work if an element changes type?

    private T commonAncestor;
    private List<Snapshot<T>> list = new LinkedList<>();
    private DifferFactory applicatorFactory;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");

    public ThreeWayDiff(T commonAncestor) {
	this(commonAncestor, new DifferFactory());
    }

    public ThreeWayDiff(T commonAncestor, DifferFactory applicatorFactory) {
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Created ThreeWayDiff for ancestor" + commonAncestor);
	}
	this.commonAncestor = commonAncestor;
	this.applicatorFactory = applicatorFactory;
    }

    /** Add a snapshot - assumes they are given in order from oldest to youngest */
    public void addBranchSnapshot(Snapshot<T> snapshot) {
	list.add(snapshot);
    }

    /** Add a snapshot - assumes they are given in order from oldest to youngest */
    public void addBranchSnapshot(T object, Object branch) {
	addBranchSnapshot(new Snapshot<T>(object, branch));
    }

    public T apply() {
	return getPatch().apply(commonAncestor);
    }

    @Override
    public String toString() {
	return "CA: " + commonAncestor + "," + list.toString();
    }

    private SequenceDiff<T, ?> getPatch() {
	if (LOG.isLoggable(Level.FINE)) {
	    LOG.log(Level.FINE, "Creating patch for " + commonAncestor + "-> " + list.toString());
	}

	List<Snapshot<T>> workingList = list;
	SequenceDiff<T, ?> result = applicatorFactory.createApplicator(commonAncestor);
	Map<Object, T> lastSeenInBranch = new HashMap<>();
	for (Snapshot<T> snapshot : workingList) {
	    T parent = lastSeenInBranch.get(snapshot.getBranch());
	    if (parent == null) {
		parent = commonAncestor;
	    }
	    result.addDelta(parent, snapshot.getObject(), snapshot.getBranch());
	    lastSeenInBranch.put(snapshot.getBranch(), snapshot.getObject());
	}
	return result;
    }

}
