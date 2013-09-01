package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonElementAdapter implements
		JsonVisitorDestination<JsonElement, JsonElement, Identity>,
		JsonVisitorSource<JsonElement, JsonElement> {

	private JsonStoreAdapterFactory jsa;

	JsonElementAdapter(JsonStoreAdapterFactory jsa) {
		this.jsa = jsa;
	}

	@Override
	public JsonElement arriveList(List<JsonElement> list) {
		JsonArray r = new JsonArray();
		for (JsonElement e : list) {
			r.add(e);
		}
		return r;
	}

	@Override
	public JsonElement arriveMap(Map<String, JsonElement> map) {
		JsonObject o = new JsonObject();
		for (Entry<String, JsonElement> e : map.entrySet()) {
			o.add(e.getKey(), e.getValue());
		}
		return o;
	}

	@Override
	public JsonElement arriveValue(Object value) {
		return jsa.getGson().toJsonTree(value);
	}

	@Override
	public Function<Identity, JsonElement> getTransform(
			VisitorContext<Identity, JsonElement> context) {
		return context.getRecurseFunction();
	}

	@Override
	public com.crypticbit.javelin.js.JsonVisitorSource.ElementType getType(
			JsonElement in) {
		return getTypeStatic(in);
	}

	static com.crypticbit.javelin.js.JsonVisitorSource.ElementType getTypeStatic(
			JsonElement in) {
		if (in.isJsonArray())
			return ElementType.ARRAY;
		else if (in.isJsonObject())
			return ElementType.OBJECT;
		else if (in.isJsonPrimitive())
			return ElementType.PRIMITIVE;
		else if (in.isJsonNull())
			return ElementType.NULL;
		else
			throw new IllegalStateException();
	}

	@Override
	public JsonElement parse(JsonElement in) throws JsonSyntaxException,
			StoreException {
		return in;
	}

	@Override
	public Object parsePrimitive(JsonElement in) {
		return parsePrimitiveStatic((JsonPrimitive) in);
	}

	static Object parsePrimitiveStatic(JsonPrimitive primitive) {
		if (primitive.isBoolean()) {
			return primitive.getAsBoolean();
		}
		if (primitive.isNumber()) {
			if (!primitive.getAsString().contains(".")) {
				return primitive.getAsInt();
			} else {
				return primitive.getAsFloat();
			}
		}
		if (primitive.isString()) {
			return primitive.getAsString();
		}
		throw new InternalError("illegal Json Type found: " + primitive);
	}

	@Override
	public List<JsonElement> parseList(JsonElement in) {
		return jsa.getGson().fromJson(in, new TypeToken<List<JsonElement>>() {
		}.getType());
	}

	@Override
	public Map<String, JsonElement> parseMap(JsonElement in) {
		return jsa.getGson().fromJson(in,
				new TypeToken<Map<String, JsonElement>>() {
				}.getType());
	}

}
