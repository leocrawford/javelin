package com.crypticbit.javelin.js.convert;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.ContentAddressableStorage;
import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class JsonVisitorCasAdapter implements JsonVisitorSource<Key, JsonElement>,
	JsonVisitorDestination<Key, Key, Object> {

    private ContentAddressableStorage cas;
    private Gson gson;

    public JsonVisitorCasAdapter(ContentAddressableStorage cas, Gson gson) {
	this.cas = cas;
	this.gson = gson;
    }

    @Override
    public Function<Object, Key> getTransform(VisitorContext<Object, Key> context) {
	return context.getRecurseFunction();
    }

    @Override
    public ElementType getType(JsonElement in) {
	return JsonVisitorElementAdapter.getTypeStatic(in);
    }

    /*
     * (non-Javadoc)
     * @see com.crypticbit.javelin.js.SourceCallback#parse(com.crypticbit.javelin .store.Identity)
     */
    @Override
    public JsonElement parse(Key digest) throws VisitorException {
	try {
	    return new JsonParser().parse(cas.get(digest).getAsString());
	}
	catch (JsonSyntaxException | StoreException e) {
	    throw new VisitorException("Unable to read value from location " + digest + " in store " + cas.getName(), e);
	}
    }

    /*
     * (non-Javadoc)
     * @see com.crypticbit.javelin.js.SourceCallback#parseList(com.google.gson .JsonElement)
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
     * @see com.crypticbit.javelin.js.SourceCallback#parsePrimitive(com.google .gson.JsonPrimitive)
     */
    @Override
    public Object parsePrimitive(JsonElement element) {
	return JsonVisitorElementAdapter.parsePrimitiveStatic((JsonPrimitive) element);
    }

    @Override
    public Key writeList(Object source, List<Key> list) throws VisitorException {
	return write(list);
    }

    @Override
    public Key writeMap(Object source, Map<String, Key> map) throws VisitorException {
	return write(map);
    }

    @Override
    public Key writeNull(Object source) throws VisitorException {
	return write(null);
    }

    @Override
    public Key writeValue(Object source, Object value) throws VisitorException {
	return write(value);
    }

    // FIXME if already exists
    Key write(Object value) throws VisitorException {
	try {
	    return cas.store(new GeneralPersistableResource(gson.toJson(value)));
	}
	catch (StoreException e) {
	    throw new VisitorException("Unable to write to CAS store " + cas.getName(), e);
	}
    }

}