package com.crypticbit.javelin.js;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.js.lazy.IdentityReference;
import com.crypticbit.javelin.js.lazy.LazyJsonArray;
import com.crypticbit.javelin.js.lazy.LazyJsonMap;
import com.crypticbit.javelin.js.lazy.Reference;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

public class JsonObjectStoreAdapter extends DataAccessInterface<Object>
		implements JsonVisitorDestinationCallback<Object, Reference, Identity>,
		JsonVisitorSource<Object, Object> {

	JsonObjectStoreAdapter(ContentAddressableStorage cas,
			JsonStoreAdapterFactory jsa) {
		super(cas, jsa);
	}

	@Override
	public Object arriveList(List<Reference> list) {
		// copy so writeable
		return new LazyJsonArray(new ArrayList<>(list));
	}

	@Override
	public Object arriveMap(Map<String, Reference> map) {
		// copy so writeable 
		LinkedTreeMap<String, Reference> linkedTreeMap = new LinkedTreeMap<String,Reference>();
		linkedTreeMap.putAll(map);
		return new LazyJsonMap(linkedTreeMap);

	}

	@Override
	public Object arriveValue(Object value) {
		return value;
	}

	@Override
	public Object read(Identity commitId) throws StoreException,
			JsonSyntaxException {
		StoreVisitor<Object, Reference, Identity, JsonElement> sv = new StoreVisitor<>(
				cas, this, new JsonVisitorCasAdapter(cas, jsa.getGson()),
				jsa.getGson());
		return sv.visit(commitId);
	}

	// FIXME if already exists
	@Override
	public Identity write(Object object) throws StoreException {
		StoreVisitor<Identity, Identity, Object, Object> sv = new StoreVisitor<>(
				cas, new JsonVisitorCasAdapter(cas, jsa.getGson()), this,
				jsa.getGson());
		return sv.visit(object);

	}

	@Override
	public com.crypticbit.javelin.js.JsonVisitorSource.ElementType getType(
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
	public Object parse(Object in) throws JsonSyntaxException, StoreException {
		return in;
	}

	@Override
	public Object parsePrimitive(Object in) {
		return in;
	}

	@Override
	public List<Object> parseList(Object in) {
		return (List<Object>) in;
	}

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

}
