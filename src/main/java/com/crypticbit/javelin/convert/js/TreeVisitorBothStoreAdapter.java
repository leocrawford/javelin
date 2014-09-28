package com.crypticbit.javelin.convert.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.convert.TreeVisitorBoth;
import com.crypticbit.javelin.convert.TreeVisitorBoth.ElementType;
import com.crypticbit.javelin.convert.VisitorContext;
import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TreeVisitorBothStoreAdapter implements
		
		TreeVisitorBoth<Key, JsonElement> {

	private AddressableStorage cas;
	private static Gson gson = new Gson();

	public TreeVisitorBothStoreAdapter(AddressableStorage cas) {
		this.cas = cas;
	}

	/* @Override
	public Function<Object, Key> getTransform(
			VisitorContext<Object, Key> context) {
		return context.getRecurseFunction();
	} */

	@Override
	public ElementType getType(JsonElement in) {
		return TreeVisitorBothElementAdapter.getTypeStatic(in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.crypticbit.javelin.js.SourceCallback#parse(com.crypticbit.javelin
	 * .store.Identity)
	 */
	@Override
	public JsonElement parse(Key digest) throws VisitorException {
		try {
			return cas.get(digest, JsonElement.class);
		} catch (JsonSyntaxException | StoreException e) {
			throw new VisitorException("Unable to read value from location "
					+ digest + " in store " + cas.getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.javelin.js.SourceCallback#parseList(com.google.gson
	 * .JsonElement)
	 */
	@Override
	public List<Key> parseList(JsonElement in) {
		return gson.fromJson(in, new TypeToken<List<Key>>() {
		}.getType());
	}

	@Override
	public Map<String, Key> parseMap(JsonElement in) {
		return gson.fromJson(in, new TypeToken<Map<String, Key>>() {
		}.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.javelin.js.SourceCallback#parsePrimitive(com.google
	 * .gson.JsonPrimitive)
	 */
	@Override
	public Object parsePrimitive(JsonElement element) {
		return TreeVisitorBothElementAdapter
				.parsePrimitiveStatic((JsonPrimitive) element);
	}

	@Override
	public Key writeList(List<Key> list) throws VisitorException {
		return write(list);
	}

	@Override
	public Key writeMap(Map<String, Key> map)
			throws VisitorException {
		return write(map);
	}

	@Override
	public Key writeNull() throws VisitorException {
		return write(null);
	}

	@Override
	public Key writeValue(Object value) throws VisitorException {
		return write(value);
	}

	// FIXME if already exists
	Key write(Object value) throws VisitorException {
		try {
			return cas.store(gson.toJsonTree(value), JsonElement.class);
		} catch (StoreException e) {
			throw new VisitorException("Unable to write to CAS store "
					+ cas.getName(), e);
		}
	}

}