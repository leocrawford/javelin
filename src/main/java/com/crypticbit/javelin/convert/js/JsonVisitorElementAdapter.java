package com.crypticbit.javelin.convert.js;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.convert.JsonVisitorDestination;
import com.crypticbit.javelin.convert.VisitorContext;
import com.crypticbit.javelin.convert.VisitorInterface;
import com.crypticbit.javelin.store.Key;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class JsonVisitorElementAdapter implements
		JsonVisitorDestination<JsonElement, JsonElement, Key>,
		VisitorInterface<JsonElement, JsonElement> {

	private static final Gson gson = new Gson();

	public JsonVisitorElementAdapter() {
	}

	@Override
	public Function<Key, JsonElement> getTransform(
			VisitorContext<Key, JsonElement> context) {
		return context.getRecurseFunction();
	}

	@Override
	public com.crypticbit.javelin.convert.VisitorInterface.ElementType getType(
			JsonElement in) {
		return getTypeStatic(in);
	}

	@Override
	public JsonElement parse(JsonElement in) {
		return in;
	}

	@Override
	public List<JsonElement> parseList(JsonElement in) {
		return gson.fromJson(in, new TypeToken<List<JsonElement>>() {
		}.getType());
	}

	@Override
	public Map<String, JsonElement> parseMap(JsonElement in) {
		return gson.fromJson(in, new TypeToken<Map<String, JsonElement>>() {
		}.getType());
	}

	@Override
	public Object parsePrimitive(JsonElement in) {
		return parsePrimitiveStatic((JsonPrimitive) in);
	}

	@Override
	public JsonElement writeList(Key source, List<JsonElement> list) {
		JsonArray r = new JsonArray();
		for (JsonElement e : list) {
			r.add(e);
		}
		return r;
	}

	@Override
	public JsonElement writeMap(Key source, Map<String, JsonElement> map) {
		JsonObject o = new JsonObject();
		for (Entry<String, JsonElement> e : map.entrySet()) {
			o.add(e.getKey(), e.getValue());
		}
		return o;
	}

	@Override
	public JsonElement writeNull(Key source) {
		return writeValue(source, null);
	}

	@Override
	public JsonElement writeValue(Key source, Object value) {
		// Hack to get unit tests to pass. Did fail because a converting from
		// JsonElement to Object and back gave a different type for same value
		return new JsonParser().parse(gson.toJson(value));
		// return jsa.getGson().toJsonTree(value);
	}

	static com.crypticbit.javelin.convert.VisitorInterface.ElementType getTypeStatic(
			JsonElement in) {
		if (in.isJsonArray()) {
			return ElementType.ARRAY;
		} else if (in.isJsonObject()) {
			return ElementType.OBJECT;
		} else if (in.isJsonPrimitive()) {
			return ElementType.PRIMITIVE;
		} else if (in.isJsonNull()) {
			return ElementType.NULL;
		} else {
			throw new IllegalStateException();
		}
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

}
