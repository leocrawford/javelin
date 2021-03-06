package com.crypticbit.javelin.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LazyJsonElement;

public class JsonElementTreeNodeConverterTest {

    private static final Gson GSON = new Gson();

    @Test
    public void testConvertPrimitive() throws JsonSyntaxException, StoreException {
	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(new StorageFactory().createMemoryCas());
	TreeMapper<Key, JsonElement> jsonElementAdapter = store.getJsonElementAdapter();

	final String jsonFloat = "2.1";
	final JsonElement json = GSON.fromJson(jsonFloat, JsonElement.class);
	Key floatIdentity = jsonElementAdapter.write(json);
	assertEquals(json, ((LazyJsonElement) jsonElementAdapter.read(floatIdentity)).deepCopy());

    }

    @Test
    public void testReadWriteJsonElement() throws JsonSyntaxException, StoreException, IOException {

	AddressableStorage memoryStore = new StorageFactory().createMemoryCas();
	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(memoryStore);

	TreeMapper<Key, JsonElement> jsonElementAdapter = store.getJsonElementAdapter();
	Key stringIdentity = jsonElementAdapter.write(GSON.fromJson("\"String\"", JsonElement.class));
	Key nullIdentity = jsonElementAdapter.write(GSON.fromJson("null", JsonElement.class));
	Key integerIdentity = jsonElementAdapter.write(GSON.fromJson("100", JsonElement.class));
	Key floatIdentity = jsonElementAdapter.write(GSON.fromJson("2.1", JsonElement.class));
	Key booleanIdentity = jsonElementAdapter.write(GSON.fromJson("TRUE", JsonElement.class));
	Key arrayIdentity = jsonElementAdapter.write(GSON.fromJson("[1,2,3]", JsonElement.class));
	Key mapIdentity = jsonElementAdapter
		.write(GSON.fromJson("{\"a\":1,\"b\":null,\"c\":FALSE}", JsonElement.class));

	assertTrue(jsonElementAdapter.read(stringIdentity).getAsJsonPrimitive().isString());
	assertTrue(jsonElementAdapter.read(nullIdentity).isJsonNull());
	assertTrue(jsonElementAdapter.read(integerIdentity).getAsJsonPrimitive().getAsJsonPrimitive().isNumber());
	assertEquals(100, jsonElementAdapter.read(integerIdentity).getAsJsonPrimitive().getAsInt());
	assertTrue(jsonElementAdapter.read(floatIdentity).getAsJsonPrimitive().isNumber());
	assertEquals(2.1f, jsonElementAdapter.read(floatIdentity).getAsJsonPrimitive().getAsFloat(), 0.001);
	assertTrue(jsonElementAdapter.read(booleanIdentity).getAsJsonPrimitive().isBoolean());
	assertEquals(true, jsonElementAdapter.read(booleanIdentity).getAsJsonPrimitive().getAsBoolean());

	assertTrue(jsonElementAdapter.read(arrayIdentity).isJsonArray());
	assertEquals(3, jsonElementAdapter.read(arrayIdentity).getAsJsonArray().size());
	assertEquals(1, jsonElementAdapter.read(arrayIdentity).getAsJsonArray().get(0).getAsInt());

	assertTrue(jsonElementAdapter.read(mapIdentity).isJsonObject());
	assertEquals(3, jsonElementAdapter.read(mapIdentity).getAsJsonObject().entrySet().size());
	assertTrue(jsonElementAdapter.read(mapIdentity).getAsJsonObject().get("b").isJsonNull());

    }

}
