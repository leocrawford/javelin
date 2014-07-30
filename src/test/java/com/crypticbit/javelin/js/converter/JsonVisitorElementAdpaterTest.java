package com.crypticbit.javelin.js.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.js.DataAccessInterface;
import com.crypticbit.javelin.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class JsonVisitorElementAdpaterTest {

    private static final Gson GSON = new Gson();

    @Test
    public void testConvertPrimitive() throws JsonSyntaxException, StoreException, VisitorException {
	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(new StorageFactory().createMemoryCas());
	DataAccessInterface<JsonElement> jsonElementAdapter = store.getJsonElementAdapter();

	final String jsonFloat = "2.1";
	final JsonElement json = GSON.fromJson(jsonFloat, JsonElement.class);
	Key floatIdentity = jsonElementAdapter.write(json);
	assertEquals(json, jsonElementAdapter.read(floatIdentity));

    }

    @Test
    public void testReadWriteJsonElement() throws JsonSyntaxException, StoreException, IOException, VisitorException {

	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(new StorageFactory().createMemoryCas());

	DataAccessInterface<JsonElement> jsonElementAdapter = store.getJsonElementAdapter();
	Key stringIdentity = jsonElementAdapter.write(GSON.fromJson("\"String\"", JsonElement.class));
	Key nullIdentity = jsonElementAdapter.write(GSON.fromJson("null", JsonElement.class));
	Key integerIdentity = jsonElementAdapter.write(GSON.fromJson("100", JsonElement.class));
	Key floatIdentity = jsonElementAdapter.write(GSON.fromJson("2.1", JsonElement.class));
	Key booleanIdentity = jsonElementAdapter.write(GSON.fromJson("TRUE", JsonElement.class));
	Key arrayIdentity = jsonElementAdapter.write(GSON.fromJson("[1,2,3]", JsonElement.class));
	Key mapIdentity = jsonElementAdapter.write(GSON.fromJson("{\"a\":1,\"b\":null,\"c\":FALSE}",
		JsonElement.class));

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
