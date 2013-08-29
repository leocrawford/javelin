package com.crypticbit.javelin.js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.js.lazy.IdentityReference;
import com.crypticbit.javelin.js.lazy.LazyJsonArray;
import com.crypticbit.javelin.js.lazy.LazyJsonMap;
import com.crypticbit.javelin.js.lazy.Reference;
import com.crypticbit.javelin.store.GeneralPersistableResource;
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
	private StoreVisitor<Object, Reference, Identity, JsonElement> sv;

	JsonObjectStoreAdapter(ContentAddressableStorage cas,
			JsonStoreAdapterFactory jsa) {
		super(cas, jsa);
		sv = new StoreVisitor<>(cas, this, new JsonVisitorCasAdapter(cas,
				jsa.getGson()), jsa.getGson());
	}

	@Override
	public Object arriveList(List<Reference> list) {
		return new LazyJsonArray(new ArrayList<>(list));
	}

	@Override
	public Object arriveMap(Map<String, Reference> map) {
		return new LazyJsonMap(map);
	}

	@Override
	public Object arriveValue(Object value) {
		return value;
	}

	@Override
	public Function<Identity, Reference> getTransform() {
		return new Function<Identity, Reference>() {
			public Reference apply(Identity identity) {
				return new IdentityReference(jsa, identity);
			}
		};
	}

	@Override
	public Object read(Identity commitId) throws StoreException,
			JsonSyntaxException {
		return sv.visit(commitId);
	}

	// FIXME if already exists
	@Override
	public Identity write(Object object) throws StoreException {
		if (object instanceof List) {
			List<Identity> r = new LinkedList<>();
			for (Object o : (List<Object>) object) {
				r.add(write(o));
			}
			return cas
					.store(new GeneralPersistableResource(getGson().toJson(r)));
		} else if (object instanceof Map) {
			Map<String, Identity> r = new LinkedTreeMap<>();
			for (Map.Entry<String, Object> o : ((Map<String, Object>) object)
					.entrySet()) {
				r.put(o.getKey(), write(o.getValue()));
			}
			return cas
					.store(new GeneralPersistableResource(getGson().toJson(r)));
		} else {
			return cas.store(new GeneralPersistableResource(getGson().toJson(
					object)));
		}
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

}
