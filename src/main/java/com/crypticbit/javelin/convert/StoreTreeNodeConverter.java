package com.crypticbit.javelin.convert;

import java.util.*;

import com.crypticbit.javelin.convert.lazy.LazyArray;
import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.*;

public class StoreTreeNodeConverter implements AddressToJsonElementWrapper {

    private AddressableStorage store;
    private static Gson gson = new Gson();

    public StoreTreeNodeConverter(AddressableStorage store) {
	this.store = store;
    }

    public Key writeAsJsonElement(JsonElement element) throws TreeMapperException {
	try {
	    if (element.isJsonNull() || element.isJsonPrimitive())
		return save(element);
	    if (element.isJsonObject())
		return save(createJsonObject(element.getAsJsonObject().entrySet(),
			jsonElementToJsonKeyReferencesFunction));
	    if (element.isJsonArray())
		return save(createJsonArray(asArray(element.getAsJsonArray()), jsonElementToJsonKeyReferencesFunction));
	    throw new TreeMapperException("JsonElement(" + element.getClass() + ") was not a recognised type",
		    new IllegalStateException());
	}
	catch (StoreException se) {
	    throw new TreeMapperException("Can't write to store", se);
	}
    }

    public Key writeAsObject(Object unpackedElement) throws TreeMapperException {
	try {
	    if (unpackedElement == null)
		return save(JsonNull.INSTANCE);
	    if (unpackedElement instanceof Map)
		return save(createJsonObject(((Map) unpackedElement).entrySet(), objectToJsonKeyReferencesFunction));
	    if (unpackedElement instanceof List)
		return save(createJsonArray(((List) unpackedElement), objectToJsonKeyReferencesFunction));
	    else
		return save(gson.toJsonTree(unpackedElement));
	}
	catch (StoreException se) {
	    throw new TreeMapperException("Can't write to store", se);
	}
    }

    private <S> JsonObject createJsonObject(Set<Map.Entry<String, S>> entries,
	    Function<S, JsonElement> elementTransformer) {
	JsonObject result = new JsonObject();
	for (Map.Entry<String, S> entry : entries)
	    result.add(entry.getKey(), elementTransformer.apply(entry.getValue()));
	return result;
    }

    private <S> JsonArray createJsonArray(List<S> entries, Function<S, JsonElement> elementTransformer) {
	JsonArray result = new JsonArray();
	for (S entry : entries)
	    result.add(elementTransformer.apply(entry));
	return result;
    }

    private Key save(JsonElement toSave) throws StoreException {
	return store.store(toSave, JsonElement.class);
    }

    private final Function<Object, JsonElement> objectToJsonKeyReferencesFunction = new ConvertObjectToKeyFunction();
    private final Function<JsonElement, JsonElement> jsonElementToJsonKeyReferencesFunction = new ConvertJsonElementToKeyFunction();

    private final Function<JsonElement, Reference> jsonToIdentityReferenceFunction = new Function<JsonElement, Reference>() {
	@Override
	public Reference apply(JsonElement input) {
	    System.out.println("decoding " + input);
	    return new IdentityReference(new Key(input.getAsString()));
	}
    };

    private static Map<String, JsonElement> asMap(JsonObject jo) {
	Map<String, JsonElement> result = new HashMap<>();
	for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
	    result.put(entry.getKey(), entry.getValue());
	}
	return result;

    }

    private static List<JsonElement> asArray(JsonArray ja) {
	List<JsonElement> result = new ArrayList<>();
	for (JsonElement entry : ja) {
	    result.add(entry);
	}
	return result;
    }

    public JsonElement readAsJsonElement(Key element) throws TreeMapperException {
	try {
	    JsonElement input = store.getCas(element, JsonElement.class);
	    if (input.isJsonObject() || input.isJsonArray())
		return new LazyJsonElement(input, this);
	    else
		return input;
	}
	catch (StoreException e) {
	    throw new TreeMapperException("unable to copy the tree referenced by key " + element, e);
	}
    }

    public Object readAsObject(Key element) throws TreeMapperException {
	try {
	    JsonElement input = store.getCas(element, JsonElement.class);

	    if (input.isJsonObject()) {
		return new LazyMap(Maps
			.transformValues(asMap(input.getAsJsonObject()), jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonArray()) {
		return new LazyArray(com.google.common.collect.Lists.transform(asArray(input.getAsJsonArray()),
			jsonToIdentityReferenceFunction));
	    }
	    else if (input.isJsonPrimitive()) {
		return parsePrimitiveStatic(input.getAsJsonPrimitive());
	    }
	    else
		return null;
	}
	catch (StoreException e) {
	    throw new TreeMapperException("unable to copy the tree referenced by key " + element, e);
	}
    }

    static Object parsePrimitiveStatic(JsonPrimitive primitive) {
	if (primitive.isBoolean()) {
	    return primitive.getAsBoolean();
	}
	if (primitive.isNumber()) {
	    if (!primitive.getAsString().contains(".")) {
		return primitive.getAsInt();
	    }
	    else {
		return primitive.getAsFloat();
	    }
	}
	if (primitive.isString()) {
	    return primitive.getAsString();
	}
	throw new InternalError("illegal Json Type found: " + primitive);
    }

    private final class ConvertJsonElementToKeyFunction implements Function<JsonElement, JsonElement> {
	@Override
	public JsonElement apply(JsonElement input) {
	    try {
		return gson.toJsonTree(writeAsJsonElement(input).getKeyAsString());
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error();
	    }
	}
    }
    
    private final class ConvertObjectToKeyFunction implements Function<Object, JsonElement> {
	@Override
	public JsonElement apply(Object input) {
	    try {
		return gson.toJsonTree(writeAsObject(input).getKeyAsString());
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error();
	    }
	}
    }

    class IdentityReference implements Reference {

	private Key key;

	public IdentityReference(Key key) {
	    this.key = key;
	}

	@Override
	public Object getValue() {
	    try {
		return readAsObject(key);
	    }
	    catch (TreeMapperException e) {
		// FIXME
		throw new Error(e);
	    }
	}

    }

    @Override
    public JsonElement wrap(JsonElement input) {
	try {
	    return readAsJsonElement(new Key(input.getAsString()));
	}
	catch (TreeMapperException e) {

	    e.printStackTrace();
	    // FIXME
	    throw new Error();
	}
    }

}