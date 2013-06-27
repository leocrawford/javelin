package com.crypticbit.javelin.diff.map;

import java.util.*;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.CollectionDiffer;
import com.crypticbit.javelin.diff.DifferFactoryElement;

public class MapDiffer<T> implements DifferFactoryElement {

    @Override
    public boolean supports(Object object) {
	return object instanceof Map;
    }

    @Override
    public CollectionDiffer<Map<String,T>,MapDelta> createApplicator() {
	return new CollectionDiffer<Map<String,T>,MapDelta>(){

	    @Override
	    public Map<String, T> apply(Map<String, T> value) {
		for (MapDelta d :  getListOfDeltaInOrder()) {
		    d.apply(value);
		}
		return value;
	    }

	    @Override
	    protected MapDelta createDelta(Object parent, Object child, Object branch) {
		Map<String,T> p = (Map<String,T>) parent;
		Map<String,T> c = (Map<String,T>) child;

		
		Map<String, T> removed = diff(p,c);
		Map<String, T> added = diff(c,p);
		
		Set<Map.Entry<String, T>> changedEntries = new HashSet<Entry<String, T>>(
		        c.entrySet());
		changedEntries.removeAll(p.entrySet());
		changedEntries.removeAll(added.entrySet());
		Map<String,T> changedMap = new HashMap<String,T>();
		for(Entry<String, T> x : changedEntries)
		    changedMap.put(x.getKey(), x.getValue());
	

		return new MapDelta(removed,added,changedMap);
	    }

	    private Map<String, T> diff(Map<String, T> a, Map<String, T> b) {
		Map<String, T> result = new HashMap<String,T>(a);
		for(String key : b.keySet())
		    result.remove(key);
		return result;
	    }};
    }

}
