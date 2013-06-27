package com.crypticbit.javelin.diff.map;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.crypticbit.javelin.diff.ItemDelta;

public class MapDelta<T> implements ItemDelta {

    private Map<String, T> removed, added, changed;

    public MapDelta(Map<String, T> removed, Map<String, T> added,Map<String, T> changed) {
	this.removed = removed;
	this.added = added;
	this.changed = changed;
	System.out.println(this);
    }
    

    public String toString() {
	return "-"+removed+" +"+added+" ~"+changed;
    }

    public void apply(Map<String,T> object) {
	for(String key : removed.keySet())
	    object.remove(key);
	object.putAll(added);
	for(String key : changed.keySet())
	    object.remove(key);
	object.putAll(changed);
    }
    
    @Override
    public Object getBranch() {
	// TODO Auto-generated method stub
	return null;
    }

}
