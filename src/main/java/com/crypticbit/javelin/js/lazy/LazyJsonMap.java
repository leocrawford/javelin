package com.crypticbit.javelin.js.lazy;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.js.BackedElement;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.HackedTransformedEntriesMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

public class LazyJsonMap extends HackedTransformedEntriesMap<String, Reference, Object> implements BackedElement {

    public LazyJsonMap(Map<String, Reference> backingMap) {
	super(backingMap, new EntryTransformer<String, Reference, Object>() {

	    @Override
	    public Object transformEntry(String key, Reference value) {
		return value.getValue();
	    }});
	
	
    }
    
    @Override
    public Object put(String key, Object value) {
 	return getBackedValue().put(key, new ValueReference(value));
    }

   

}
