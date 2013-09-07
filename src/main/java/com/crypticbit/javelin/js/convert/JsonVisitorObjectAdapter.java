package com.crypticbit.javelin.js.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.js.lazy.IdentityReference;
import com.crypticbit.javelin.js.lazy.LazyJsonArray;
import com.crypticbit.javelin.js.lazy.LazyJsonMap;
import com.crypticbit.javelin.js.lazy.Reference;
import com.crypticbit.javelin.store.Identity;
import com.google.common.base.Function;
import com.google.gson.internal.LinkedTreeMap;

public class JsonVisitorObjectAdapter implements
		JsonVisitorDestination<Object, Reference, Identity>,
		JsonVisitorSource<Object, Object> {

	private JsonStoreAdapterFactory jsa;
	public JsonVisitorObjectAdapter(JsonStoreAdapterFactory jsa) {
	this.jsa = jsa;
	}

	@Override
	public Object writeList(Identity source, List<Reference> list) {
		// copy so writeable
		return new LazyJsonArray(new ArrayList<>(list));
	}

	@Override
	public Object writeMap(Identity source, Map<String, Reference> map) {
		// copy so writeable
		LinkedTreeMap<String, Reference> linkedTreeMap = new LinkedTreeMap<String, Reference>();
		linkedTreeMap.putAll(map);
		return new LazyJsonMap(linkedTreeMap);

	}

	@Override
	public Object writeValue(Identity source, Object value) {
		return value;
	}

	@Override
	public com.crypticbit.javelin.js.convert.JsonVisitorSource.ElementType getType(
			Object in) {
		if (in == null)
			return ElementType.NULL;
		else if (in instanceof List)
			return ElementType.ARRAY;
		else if (in instanceof Map)
			return ElementType.OBJECT;
		else
			return ElementType.PRIMITIVE;

	}

	@Override
	public Object parse(Object in)  {
		return in;
	}

	@Override
	public Object parsePrimitive(Object in) {
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
	public Function<Identity, Reference> getTransform(
			VisitorContext<Identity, Object> context) {
		return new Function<Identity, Reference>() {
			public Reference apply(Identity identity) {
				return new IdentityReference(jsa, identity);
			}
		};
	}

	@Override
	public Object writeNull(Identity source)  {
		return null;
	}

}
