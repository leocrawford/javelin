package com.crypticbit.javelin.diff;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class that allows implementations to supports an ordered set of deltas
 * 
 * @author leo
 * @param <T>
 */
public abstract class SequenceDiff<T, I extends ItemDelta> {

    /** The set of implicitly ordered deltas (oldest first) */
    private List<I> deltas = new LinkedList<>();

    public void addDelta(T parent, T child, Object branch) {
	deltas.add(createDelta(parent, child, branch));
    }

    /** Apply the combined diff to the provided value */
    public abstract T apply(T value);

    @Override
    public String toString() {
	return getListOfDeltaInOrder().toString();
    }

    protected abstract I createDelta(T parent, T child, Object branch);

    /** Get a list of all the deltas in the order they were produced */
    protected List<I> getListOfDeltaInOrder() {
	return Collections.unmodifiableList(deltas);
    }
}
