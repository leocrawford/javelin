package com.crypticbit.javelin.js.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.crypticbit.javelin.js.DataAccessInterface;
import com.crypticbit.javelin.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonVisitorObjectAdapterTest {

    private static final Gson GSON = new Gson();

    @SuppressWarnings("unchecked")
    @Test
    public void testReadWriteJsonElement() throws JsonSyntaxException, StoreException, IOException, VisitorException {

	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(new StorageFactory().createMemoryCas());

	DataAccessInterface<Object> jsonObjectAdapter = store.getJsonObjectAdapter();
	Key stringIdentity = jsonObjectAdapter.write("String");
	Key nullIdentity = jsonObjectAdapter.write(null);
	Key integerIdentity = jsonObjectAdapter.write(100);
	Key floatIdentity = jsonObjectAdapter.write(2.1);
	Key booleanIdentity = jsonObjectAdapter.write(true);
	Key arrayIdentity = jsonObjectAdapter.write(Arrays.asList(new Integer[] { 1, 2, 3 }));
	Map<String, Object> m = new HashMap<>();
	m.put("a", 1);
	m.put("b", null);
	m.put("c", false);
	Key mapIdentity = jsonObjectAdapter.write(m);

	assertTrue(jsonObjectAdapter.read(stringIdentity).getClass() == String.class);
	assertTrue(jsonObjectAdapter.read(nullIdentity) == null);
	assertTrue(jsonObjectAdapter.read(integerIdentity) instanceof Integer);
	assertEquals(100, jsonObjectAdapter.read(integerIdentity));
	assertTrue(jsonObjectAdapter.read(floatIdentity) instanceof Float);
	assertEquals(2.1f, (Float) jsonObjectAdapter.read(floatIdentity), 0.001);
	assertTrue(jsonObjectAdapter.read(booleanIdentity) instanceof Boolean);
	assertEquals(true, jsonObjectAdapter.read(booleanIdentity));

	assertTrue(jsonObjectAdapter.read(arrayIdentity) instanceof List);
	assertEquals(3, ((List<Object>) jsonObjectAdapter.read(arrayIdentity)).size());
	assertEquals(1, ((List<Object>) jsonObjectAdapter.read(arrayIdentity)).get(0));

	assertTrue(jsonObjectAdapter.read(mapIdentity) instanceof Map);
	assertEquals(3, ((Map<String, Object>) jsonObjectAdapter.read(mapIdentity)).size());
	assertTrue(((Map<String, Object>) jsonObjectAdapter.read(mapIdentity)).get("b") == null);

	Map<String, Object> x = (Map<String, Object>) jsonObjectAdapter.read(mapIdentity);
	x.remove("a");
	x.put("b", "B");
	jsonObjectAdapter.write(x);
	assertEquals(2, x.size());
	assertEquals("B", x.get("b"));

    }

}
