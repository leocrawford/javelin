package com.crypticbit.javelin.js.convert;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.store.Key;
import com.google.common.base.Function;

public class JsonVisitorKeyAdapter implements JsonVisitorDestination<Set<Key>, Set<Key>, Key> {

    @Override
    public Function<Key, Set<Key>> getTransform(VisitorContext<Key, Set<Key>> context)
	    throws VisitorException {
	return context.getRecurseFunction();
    }

    @Override
    public Set<Key> writeList(Key source, List<Set<Key>> list) throws VisitorException {
	return collapseSet(source, list);
    }

    @Override
    public Set<Key> writeMap(Key source, Map<String, Set<Key>> map) throws VisitorException {
	return collapseSet(source, map.values());
    }

    @Override
    public Set<Key> writeNull(Key source) throws VisitorException {
	return writeValue(source, null);
    }

    @Override
    public Set<Key> writeValue(Key source, Object value) throws VisitorException {
	Set<Key> result = new HashSet<Key>(1);
	result.add(source);
	return result;
    }

    private Set<Key> collapseSet(Key source, Collection<Set<Key>> collection) {
	Set<Key> result = new HashSet<>();
	result.add(source);
	for (Set<Key> e : collection) {
	    result.addAll(e);
	}
	return result;
    }

}
