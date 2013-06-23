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

    public List apply(List list) {
	List<ListDelta> l = getListOfDeltaInOrder();
	if(l.size() > 0)
	{
	    Applicator applicator = l.iterator().next().getApplicator(l);
	    applicator.apply(list);
	}
	return list;
    }

    private List<ListDelta> getListOfDeltaInOrder() {
	return useDates == null || useDates ? new LinkedList<>(deltasWithDate.values()) : deltasWithoutDate;
    }

    public String toString() {
	return deltasWithDate.toString();
    }

}
