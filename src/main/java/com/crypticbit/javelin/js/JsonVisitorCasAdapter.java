package com.crypticbit.javelin.js;

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

class JsonVisitorCasAdapter implements
		JsonVisitorSource<Identity, JsonElement>,
		JsonVisitorDestination<Identity, Identity, Object> {

	private ContentAddressableStorage cas;
	private Gson gson;

	JsonVisitorCasAdapter(ContentAddressableStorage cas, Gson gson) {
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
	public JsonElement parse(Identity digest) throws JsonSyntaxException,
			StoreException {
		return new JsonParser().parse(cas.get(digest).getAsString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.javelin.js.SourceCallback#parsePrimitive(com.google
	 * .gson.JsonPrimitive)
	 */
	@Override
	public Object parsePrimitive(JsonElement element) {
		return JsonElementAdapter
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
		return JsonElementAdapter.getTypeStatic(in);
	}

	@Override
	public Identity arriveList(List<Identity> list) throws StoreException {
		return cas.store(new GeneralPersistableResource(gson.toJson(list)));
	}

	@Override
	public Identity arriveMap(Map<String, Identity> map) throws StoreException {
		return cas.store(new GeneralPersistableResource(gson.toJson(map)));
	}

	@Override
	public Identity arriveValue(Object value) throws StoreException {
		return cas.store(new GeneralPersistableResource(gson.toJson(value)));
	}

	@Override
	public Function<Object, Identity> getTransform(
			VisitorContext<Object, Identity> context) {
		return context.getRecurseFunction();
	}

}