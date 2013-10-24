package com.crypticbit.javelin.js.convert;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.common.base.Function;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class JsonVisitorCasAdapter implements JsonVisitorSource<Identity, JsonElement>,
	JsonVisitorDestination<Identity, Identity, Object> {

    private ContentAddressableStorage cas;
    private Gson gson;

    public JsonVisitorCasAdapter(ContentAddressableStorage cas, Gson gson) {
	this.cas = cas;
	this.gson = gson;
    }

    @Override
    public Function<Object, Identity> getTransform(VisitorContext<Object, Identity> context) {
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
    public JsonElement parse(Identity digest) throws VisitorException {
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
    public List<Identity> parseList(JsonElement in) {
	return gson.fromJson(in, new TypeToken<List<Identity>>() {
	}.getType());
    }

    @Override
    public Map<String, Identity> parseMap(JsonElement in) {
	return gson.fromJson(in, new TypeToken<Map<String, Identity>>() {
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
    public Identity writeList(Object source, List<Identity> list) throws VisitorException {
	return write(list);
    }

    @Override
    public Identity writeMap(Object source, Map<String, Identity> map) throws VisitorException {
	return write(map);
    }

    @Override
    public Identity writeNull(Object source) throws VisitorException {
	return write(null);
    }

    @Override
    public Identity writeValue(Object source, Object value) throws VisitorException {
	return write(value);
    }

    // FIXME if already exists
    Identity write(Object value) throws VisitorException {
	try {
	    return cas.store(new GeneralPersistableResource(gson.toJson(value)));
	}
	catch (StoreException e) {
	    throw new VisitorException("Unable to write to CAS store " + cas.getName(), e);
	}
    }

}