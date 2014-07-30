package com.crypticbit.javelin.cas.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.KeyFactory.DigestMethod;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class MemoryCasTest {

    private StorageFactory cf = new StorageFactory();

    @Test
    public void testCheck() throws UnsupportedEncodingException, StoreException, IOException, NoSuchAlgorithmException {
	AddressableStorage cas = cf.createMemoryCas();
	Key md1 = cas.store(prFromString("\"message 1\""),JsonElement.class);
	assertTrue(cas.check(md1));
	assertFalse(cas.check(new Key(MessageDigest.getInstance("SHA-1"))));
    }

    @Test
    public void testGet() throws UnsupportedEncodingException, StoreException, IOException {
	AddressableStorage cas = cf.createMemoryCas();
	Key md1 = cas.store(prFromString("\"message 1\""),JsonElement.class);
	Key md2 = cas.store(prFromString("\"message 2\""),JsonElement.class);

	assertEquals("\"message 1\"", prToString(cas.get(md1,JsonElement.class)));
	assertEquals("\"message 2\"", prToString(cas.get(md2,JsonElement.class)));
    }

    @Test
    public void testList() throws UnsupportedEncodingException, StoreException, IOException {
	AddressableStorage cas = cf.createMemoryCas();
	Key md[] = new Key[10];
	for (int loop = 0; loop < 10; loop++) {
	    md[loop] = cas.store(prFromString("message" + loop),JsonElement.class);
	}
	List<Key> list = cas.list();
	assertEquals(10, list.size());
	// check they're in ascehnding order - and they exist
	for (int loop = 0; loop < 10; loop++) {
	    if (loop >= 1) {
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    }
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    @Test
    public void testListAfterStart() throws UnsupportedEncodingException, StoreException, IOException {
	AddressableStorage cas = cf.createMemoryCas();
	List<Key> createList = new LinkedList<>();
	for (int loop = 0; loop < 10; loop++) {
	    createList.add(cas.store(prFromString("message" + loop),JsonElement.class));
	}
	Collections.sort(createList);
	List<Key> list = cas.list(createList.get(5));
	assertEquals(5, list.size());
	// check they're in ascending order - and they exist
	for (int loop = 0; loop < 5; loop++) {
	    if (loop >= 1) {
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    }
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    private Gson gson = new Gson();

    private JsonElement prFromString(String string) throws UnsupportedEncodingException {
	return gson.fromJson(string, JsonElement.class);
    }

    private String prToString(JsonElement jsonElement) throws UnsupportedEncodingException, IOException {
	return jsonElement.toString();
    }

}
