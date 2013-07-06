package com.crypticbit.javelin.diff;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.list.ListDiffer;
import com.crypticbit.javelin.diff.map.MapDiffer;

public class DifferFactory {

    private static final List<DifferFactoryElement> applicators = new LinkedList<>();
    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");
    
    static {
	applicators.add(new ListDiffer());
	applicators.add(new MapDiffer());
	applicators.add(new DefaultDiffer());
    }

    
    public void addApplicator(DifferFactoryElement applicator) {
	applicators.add(0, applicator);
    }

    public SequenceDiff createApplicator(Object object) {
	for (DifferFactoryElement a : applicators)
	    if (a.supports(object)) {
		if(LOG.isLoggable(Level.FINER))
		    LOG.log(Level.FINER, "Using "+a.getClass().getSimpleName()+" to diff "+object);
		return a.createApplicator();
	    }
	throw new java.lang.IllegalArgumentException(object.getClass()+" is not supported");
    }

}
