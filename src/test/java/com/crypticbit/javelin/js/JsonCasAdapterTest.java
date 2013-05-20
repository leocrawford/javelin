package com.crypticbit.javelin.js;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonElement;

public class JsonCasAdapterTest {

    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,5]]";

    private void enableLog() {
	Logger LOG = Logger.getLogger("com.crypticbit");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.FINEST);
	LOG.addHandler(handler);
	LOG.setLevel(Level.FINEST);
    }

    @Test
    public void testBasicReadWrite() throws IOException, StoreException {
	JsonCasAdapter jca = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jca.setJson(JSON_EXAMPLE);
	jca.write();
	JsonElement x = jca.read();
	Assert.assertEquals(jca.getElement(), x);
    }

    @Test
    public void testReadWriteUsingTwoObjects() throws IOException, StoreException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	JsonCasAdapter jca = new JsonCasAdapter(store);
	jca.setJson(JSON_EXAMPLE);
	jca.write();
	byte[] id = jca.getAnchor().getDigestAsByte();

	JsonCasAdapter jca2 = new JsonCasAdapter(store, new Digest(id));
	JsonElement x = jca2.read();
	Assert.assertEquals(jca.getElement(), x);
    }

    @Test
    public void testConcurrentWriteUsingTwoObjects() throws IOException, StoreException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	JsonCasAdapter jca = new JsonCasAdapter(store);
	jca.setJson(JSON_EXAMPLE);
	jca.write();
	jca.setJson(JSON_EXAMPLE_2);

	JsonCasAdapter jca2 = new JsonCasAdapter(store, jca.getAnchor());
	jca2.read();
	jca2.setJson(JSON_EXAMPLE_3);

	jca.write();
	try {
	    jca2.write();
	    fail("Concurrent modification");
	}
	catch (StoreException e) {
	    // expected to fail
	}
	jca2.read();
	jca2.setJson(JSON_EXAMPLE_3);
	jca2.write();

    }

    private void dump(ContentAddressableStorage cas) throws StoreException {
	for (Identity d : cas.list()) {
	    System.out.println(d + "->" + cas.get(d));
	}
    }
}
