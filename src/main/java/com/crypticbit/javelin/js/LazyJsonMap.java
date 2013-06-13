package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.*;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class LazyJsonMap extends AbstractMap<String, Object> {

    private Map<String, Digest> backingMap;
    private DereferencedCasAccessInterface dereferencedCasAccessInterface;

    public LazyJsonMap(DereferencedCasAccessInterface dereferencedCasAccessInterface, Map<String, Digest> backingMap) {
	this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
	this.backingMap = backingMap;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
	return new AbstractSet<Map.Entry<String, Object>>() {

	    @Override
	    public Iterator<Entry<String, Object>> iterator() {
		final Iterator<Map.Entry<String, Digest>> i = backingMap.entrySet().iterator();
		return new Iterator<Map.Entry<String, Object>>() {

		    @Override
		    public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		    }

		    @Override
		    public Entry<String, Object> next() {
			return new Map.Entry<String, Object>() {
			    java.util.Map.Entry<String, Digest> e = i.next();

			    @Override
			    public String getKey() {
				return e.getKey();
			    }

			    @Override
			    public Object getValue() {
				try {
				    return dereferencedCasAccessInterface.readAsObjects(e.getValue());
				}
				catch (JsonSyntaxException | UnsupportedEncodingException | StoreException e) {
				    // FIXME
				    throw new Error();
				}
			    }

			    @Override
			    public Object setValue(Object value) {
				// FIXME
				throw new Error("Not implemented");
			    }

			};
		    }

		    @Override
		    public void remove() {
			// FIXME
			throw new Error("Not implemented");

		    }
		};
	    }

	    @Override
	    public int size() {
		return backingMap.size();
	    }
	};
    }

}
