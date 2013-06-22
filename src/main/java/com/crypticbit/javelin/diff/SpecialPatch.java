package com.crypticbit.javelin.diff;

import java.util.*;

public class SpecialPatch {

    private SortedMap<Date, ListDelta> deltasWithDate = new TreeMap<>();
    private List<ListDelta> deltasWithoutDate = new LinkedList<>();
    private Boolean useDates = null;

    public void add(Date date, ListDelta delta) {
	if (date == null) {
	    if (useDates != null && useDates)
		throw new IllegalStateException("Can't mix date and non date patches");
	    else
		deltasWithoutDate.add(delta);
	    useDates = false;
	}
	else {
	    if (useDates != null && !useDates)
		throw new IllegalStateException("Can't mix date and non date patches");
	    else
		deltasWithDate.put(date, delta);
	    useDates = true;

	}
    }

    public void apply(List list) {
	List working = null;
	for (ListDelta d : getListOfDeltaInOrder()) {
	    if (working == null)
		working = d.wrap(list);
	    d.preprocess(working);
	}
	for (ListDelta d : getListOfDeltaInOrder()) {
	    d.apply(working);
	}
    }

    private Collection<ListDelta> getListOfDeltaInOrder() {
	return useDates == null || useDates ? deltasWithDate.values() : deltasWithoutDate;
    }

    public String toString() {
	return deltasWithDate.toString();
    }

}
