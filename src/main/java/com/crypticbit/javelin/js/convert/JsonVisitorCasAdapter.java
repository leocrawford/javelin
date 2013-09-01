package com.crypticbit.javelin.js.convert;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonVisitorCasAdapter implements
		JsonVisitorSource<Identity, JsonElement>,
		JsonVisitorDestination<Identity, Identity, Object> {

	private ContentAddressableStorage cas;
	private Gson gson;

	public JsonVisitorCasAdapter(ContentAddressableStorage cas, Gson gson) {
		this.cas = cas;
		this.gson = gson;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.crypticbit.javelin.js.SourceCallback#parse(com.crypticbit.javelin
	 * .store.Identity)
	 */
	@Override
	public JsonElement parse(Identity digest) throws VisitorException {
		try {
			return new JsonParser().parse(cas.get(digest).getAsString());
		} catch (JsonSyntaxException | StoreException e) {
			throw new VisitorException("Unable to read value from location "+digest+" in store "+cas.getName(),e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.javelin.js.SourceCallback#parsePrimitive(com.google
	 * .gson.JsonPrimitive)
	 */
	@Override
	public Object parsePrimitive(JsonElement element) {
		return JsonVisitorElementAdapter
				.parsePrimitiveStatic((JsonPrimitive) element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.javelin.js.SourceCallback#parseList(com.google.gson
	 * .JsonElement)
	 */
	@Override
	public List<Identity> parseList(JsonElement in) {
		return gson.fromJson(in, new TypeToken<List<Identity>>() {
		}.getType());
	}

	public Map<String, Identity> parseMap(JsonElement in) {
		return gson.fromJson(in, new TypeToken<Map<String, Identity>>() {
		}.getType());
	}
	
	
	@Override
	public ElementType getType(JsonElement in) {
		return JsonVisitorElementAdapter.getTypeStatic(in);
	}

	@Override
	public Identity writeList(List<Identity> list) throws VisitorException {
		return write(list);
	}

	// FIXME if already exists
	Identity write(Object value) throws VisitorException {
		try {
			return cas
					.store(new GeneralPersistableResource(gson.toJson(value)));
		} catch (StoreException e) {
			throw new VisitorException("Unable to write to CAS store "
					+ cas.getName(), e);
		}
	}

	@Override
	public Identity writeMap(Map<String, Identity> map) throws VisitorException {
		return write(map);
	}

	@Override
	public Identity writeValue(Object value) throws VisitorException {
		return write(value);
	}

	@Override
	public Function<Object, Identity> getTransform(
			VisitorContext<Object, Identity> context) {
		return context.getRecurseFunction();
	}

	@Override
	public Identity writeNull() throws VisitorException {
		return write(null);
	}

}