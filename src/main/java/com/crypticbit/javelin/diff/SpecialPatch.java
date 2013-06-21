package com.crypticbit.javelin.diff;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class SpecialPatch {

    SortedMap<Date,ListDelta> deltas = new TreeMap<>();
    
    public void add(Date date, ListDelta delta) {
	deltas.put(date, delta);
    }
    
    public void apply(List list) {
	List working = null;
	for(ListDelta d : deltas.values()) {
	    if(working == null)
		working = d.wrap(list);
	    d.preprocess(working);
	}
	for(ListDelta d : deltas.values()) {
	    d.apply(working);
	}
    }

}
