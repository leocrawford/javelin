package com.crypticbit.javelin.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.js.IdentityReference;
import com.crypticbit.javelin.convert.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.util.lazy.LazyJsonArray;
import com.crypticbit.javelin.util.lazy.LazyJsonMap;
import com.crypticbit.javelin.util.lazy.Reference;
import com.google.common.base.Function;
import com.google.gson.internal.LinkedTreeMap;

public class JsonVisitorObjectAdapter implements
		JsonVisitorDestination<Object, Reference, Key>,
		VisitorInterface<Object, Object> {

	private JsonStoreAdapterFactory jsa;

	public JsonVisitorObjectAdapter(JsonStoreAdapterFactory jsa) {
		this.jsa = jsa;
	}

	@Override
	public Function<Key, Reference> getTransform(
			VisitorContext<Key, Object> context) {
		return new Function<Key, Reference>() {
			@Override
			public Reference apply(Key identity) {
				return new IdentityReference(jsa, identity);
			}
		};
	}

	@Override
	public com.crypticbit.javelin.convert.VisitorInterface.ElementType getType(
			Object in) {
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
	public Object writeList(Key source, List<Reference> list) {
		// copy so writeable
		return new LazyJsonArray(new ArrayList<>(list));
	}

	@Override
	public Object writeMap(Key source, Map<String, Reference> map) {
		// copy so writeable
		LinkedTreeMap<String, Reference> linkedTreeMap = new LinkedTreeMap<String, Reference>();
		linkedTreeMap.putAll(map);
		return new LazyJsonMap(linkedTreeMap);

	}

	@Override
	public Object writeNull(Key source) {
		return null;
	}

	@Override
	public Object writeValue(Key source, Object value) {
		return value;
	}

}
