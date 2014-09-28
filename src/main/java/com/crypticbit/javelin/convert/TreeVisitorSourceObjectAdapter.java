package com.crypticbit.javelin.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.js.IdentityReference;
import com.crypticbit.javelin.convert.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.convert.lazy.LazyArray;
import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.Key;
import com.google.common.base.Function;
import com.google.gson.internal.LinkedTreeMap;

public class TreeVisitorSourceObjectAdapter implements
		TreeVisitorBoth<Object, Reference> {

	private JsonStoreAdapterFactory jsa;

	public TreeVisitorSourceObjectAdapter(JsonStoreAdapterFactory jsa) {
		this.jsa = jsa;
	}

/*	@Override
	public Function<Key, Reference> getTransform(
			VisitorContext<Key, Object> context) {
		return new Function<Key, Reference>() {
			@Override
			public Reference apply(Key identity) {
				return new IdentityReference(jsa, identity);
			}
		};
	} */

	@Override
	public TreeVisitorBoth.ElementType getType(Object in) {
		if (in == null) {
			return ElementType.NULL;
		} else if (in instanceof List) {
			return ElementType.ARRAY;
		} else if (in instanceof Map) {
			return ElementType.OBJECT;
		} else {
			return ElementType.PRIMITIVE;
		}

	}

	@Override
	public Object parse(Object in) {
		return in;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> parseList(Object in) {
		return (List<Object>) in;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> parseMap(Object in) {
		return (Map<String, Object>) in;
	}

	@Override
	public Object parsePrimitive(Object in) {
		return in;
	}

	@Override
	public Object writeList(List<Reference> list) {
		// copy so writeable
		return new LazyArray(new ArrayList<>(list));
	}

	@Override
	public Object writeMap(Map<String, Reference> map) {
		// copy so writeable
		LinkedTreeMap<String, Reference> linkedTreeMap = new LinkedTreeMap<String, Reference>();
		linkedTreeMap.putAll(map);
		return new LazyMap(linkedTreeMap);

	}

	@Override
	public Object writeNull() {
		return null;
	}

	@Override
	public Object writeValue(Object value) {
		return value;
	}

}
