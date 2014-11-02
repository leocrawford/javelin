package com.crypticbit.javelin.convert.lazy;

import java.util.Map;

import com.google.common.collect.HackedTransformedEntriesMap;
import com.google.common.collect.Maps.EntryTransformer;

public class LazyMap extends HackedTransformedEntriesMap<String, Reference, Object> {

    public LazyMap(Map<String, Reference> backingMap) {
	super(backingMap, new EntryTransformer<String, Reference, Object>() {

	    @Override
	    public Object transformEntry(String key, Reference value) {
		return value.getValue();
	    }
	});

    }

    @Override
    public Object put(String key, Object value) {
	return getBackedValue().put(key, new ValueReference(value));
    }

}
