package com.crypticbit.javelin.js;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.DigestFactory;
import com.crypticbit.javelin.store.memory.MemoryCasKas;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class DereferencedCasAccessInterfaceTest {

    private static final Gson GSON = new Gson();

    @Test
    public void testReadWriteJsonElement() throws JsonSyntaxException, StoreException, IOException {
	JsonStoreAdapterFactory store = new JsonStoreAdapterFactory(new MemoryCasKas(new DigestFactory()),GSON);
	Identity stringIdentity = store.getJsonElementAdapter().write(GSON.fromJson("\"String\"",JsonElement.class));
//	Identity nullIdentity = store.write(GSON.fromJson("",JsonElement.class));
	Identity integerIdentity = store.getJsonElementAdapter().write(GSON.fromJson("100",JsonElement.class));
	Identity floatIdentity = store.getJsonElementAdapter().write(GSON.fromJson("2.1",JsonElement.class));
	Identity booleanIdentity = store.getJsonElementAdapter().write(GSON.fromJson("TRUE",JsonElement.class));

	
	System.out.println(store.getJsonElementAdapter().read(stringIdentity).getAsJsonPrimitive().isString());
//	System.out.println(store.read(nullIdentity).isJsonNull());
	System.out.println(store.getJsonElementAdapter().read(integerIdentity).getAsJsonPrimitive().getAsInt());
	System.out.println((Integer) store.getJsonObjectAdapter().read(integerIdentity) + 5);
	System.out.println(store.getJsonElementAdapter().read(floatIdentity).getAsJsonPrimitive().getAsFloat());
	System.out.println(store.getJsonElementAdapter().read(booleanIdentity).getAsJsonPrimitive().isBoolean());
	
	
    }

    @Test
    public void testWriteAsObjects() {
	fail("Not yet implemented");
    }

    @Test
    public void testReadAsObjects() {
	fail("Not yet implemented");
    }

    @Test
    public void testWriteJsonElement() {
	fail("Not yet implemented");
    }

}
