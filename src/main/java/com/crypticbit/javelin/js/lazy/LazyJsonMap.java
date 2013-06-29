package com.crypticbit.javelin.js.lazy;

import java.util.*;

import com.crypticbit.javelin.js.BackedElement;
import com.google.common.collect.AbstractIterator;

public class LazyJsonMap extends AbstractMap<String, Object> implements BackedElement {
    private Map<String, Reference> backingMap;

    public LazyJsonMap(Map<String, Reference> backingMap) {
	this.backingMap = backingMap;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
	return new AbstractSet() {

	    @Override
	    public Iterator iterator() {
		final Iterator<Map.Entry<String, Reference>> x = backingMap.entrySet().iterator();
		return new AbstractIterator<Map.Entry<String, Object>>() {

		    @Override
		    protected Map.Entry<String, Object> computeNext() {
			while (x.hasNext()) {
			    final Map.Entry<String, Reference> s = x.next();
			    return new Map.Entry<String, Object>() {

				@Override
				public String getKey() {
				    return s.getKey();
				}

				@Override
				public Object getValue() {
				    return s.getValue().getValue();
				}

				@Override
				public Object setValue(Object value) {
				    return s.setValue(new ValueReference(value)).getValue();
				}
			    };
			}
			return endOfData();
		    }
		};
	    }

	    @Override
	    public int size() {
		return backingMap.size();
	    }
	};
    }

    @Override
    public Object getBackedValue() {
	return backingMap;
    }

}
