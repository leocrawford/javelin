package com.crypticbit.javelin.diff;

import java.util.*;

/**
 * An abstract class that allows implementations to supports an ordered set of deltas
 * 
 * @author leo
 * @param <T>
 */
public abstract class SequenceDiff<T, I extends ItemDelta> {

    /**
     * The set of changes if explicitly ordered by passing a date
     */
    private SortedMap<Date, I> deltasWithDate = new TreeMap<>();
    /** The set of changes if implicitly ordered by the order in which they are added (oldest first) */
    private List<I> deltasWithoutDate = new LinkedList<>();
    /** Keep track of whether we expect dates. Null is undecided. Once chosen can't be changed/mixed */
    private Boolean useDates = null;

    public void add(Date date, T parent, T child, Object branch) {
	add(date, createDelta(parent, child, branch));
    }

    /** Apply the combined diff to the provided value */
    public abstract T apply(T value);

    @Override
    public String toString() {
	return getListOfDeltaInOrder().toString();
    }

    /**
     * Add a new delta at a given date, which can be dull. Once a date is provided it must always be provided, and once
     * not provided must never be provided. If dates aren't provided the adds must be given in the order they were
     * created
     */
    protected void add(Date date, I delta) {
	if (date == null) {
	    if (useDates != null && useDates) {
		throw new IllegalStateException("Can't mix date and non date patches");
	    }
	    else {
		deltasWithoutDate.add(delta);
	    }
	    useDates = false;
	}
	else {
	    if (useDates != null && !useDates) {
		throw new IllegalStateException("Can't mix date and non date patches");
	    }
	    else {
		deltasWithDate.put(date, delta);
	    }
	    useDates = true;

	}
    }

    protected abstract I createDelta(T parent, T child, Object branch);

    /** Get a list of all the deltas in the order they were produced */
    protected List<I> getListOfDeltaInOrder() {
	return useDates == null || useDates ? new LinkedList<>(deltasWithDate.values()) : deltasWithoutDate;
    }
}
