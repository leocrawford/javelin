package com.crypticbit.javelin.js.convert;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.store.Identity;
import com.google.common.base.Function;

public class JsonVisitorKeyAdapter implements
		JsonVisitorDestination<Set<Identity>, Set<Identity>, Identity> {

	@Override
	public Set<Identity> writeList(Identity source, List<Set<Identity>> list)
			throws VisitorException {
		return collapseSet(source, list);
	}

	private Set<Identity> collapseSet(Identity source,
			Collection<Set<Identity>> collection) {
		Set<Identity> result = new HashSet<>();
		result.add(source);
		for (Set<Identity> e : collection) {
			result.addAll(e);
		}
		return result;
	}

	@Override
	public Set<Identity> writeMap(Identity source,
			Map<String, Set<Identity>> map) throws VisitorException {
		return collapseSet(source, map.values());
	}

	@Override
	public Set<Identity> writeValue(Identity source, Object value)
			throws VisitorException {
		Set<Identity> result = new HashSet<Identity>(1);
		result.add(source);
		return result;
	}

	@Override
	public Set<Identity> writeNull(Identity source) throws VisitorException {
		return writeValue(source, null);
	}

	@Override
	public Function<Identity, Set<Identity>> getTransform(
			VisitorContext<Identity, Set<Identity>> context)
			throws VisitorException {
		return context.getRecurseFunction();
	}

}
