package com.crypticbit.javelin.diff;

import java.util.LinkedList;
import java.util.List;

import com.crypticbit.javelin.diff.list.ListDiffer;
import com.crypticbit.javelin.diff.list.ListDelta;
import com.crypticbit.javelin.diff.map.MapDiffer;

public class DifferFactory {

    private static final List<DifferFactoryElement> applicators = new LinkedList<>();
   
    static {
	applicators.add(new ListDiffer());
	applicators.add(new MapDiffer());
    }

    
    public void addApplicator(DifferFactoryElement applicator) {
	applicators.add(0, applicator);
    }

    public CollectionDiffer createApplicator(Object object) {
	for (DifferFactoryElement a : applicators)
	    if (a.supports(object))
		return a.createApplicator();
	return null;
    }

}
