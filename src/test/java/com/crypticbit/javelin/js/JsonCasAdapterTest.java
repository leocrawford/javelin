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
import com.google.gson.JsonParser;

public class JsonCasAdapterTest {

    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,5]]";

    @Test
    public void testBasicReadWrite() throws IOException, StoreException {
	enableLog();
	JsonCasAdapter jca = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.checkout();
	Assert.assertEquals(jca.read(), jca.read());
    }

    @Test
    public void testCommitForMultipleReadWrite() throws IOException, StoreException {
	// enableLog();
	JsonCasAdapter jca = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	jca.commit();
	Commit c1 = jca.getCommit();
	jca.write(JSON_EXAMPLE_2);
	jca.commit();
	Commit c2 = jca.getCommit();
	jca.write(JSON_EXAMPLE_3);
	jca.commit();
	Commit c3 = jca.getCommit();

	
	Assert.assertEquals(1, c1.getShortestHistory().size());
	Assert.assertEquals(3, c3.getShortestHistory().size());
	
	Assert.assertEquals(0, c1.getParents().size());
	Assert.assertEquals(1, c2.getParents().size());
	
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE), c3.getShortestHistory().get(2).getElement());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_3), c3.getShortestHistory().get(0).getElement());


    }

    @Test
    public void testConcurrentWriteUsingTwoObjects() throws IOException, StoreException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	JsonCasAdapter jca = new JsonCasAdapter(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.write(JSON_EXAMPLE_2);

	JsonCasAdapter jca2 = new JsonCasAdapter(store, jca.getAnchor());
	jca2.checkout();
	jca2.write(JSON_EXAMPLE_3);

	jca.commit();
	try {
	    jca2.commit();
	    fail("Concurrent modification");
	}
	catch (StoreException e) {
	    // expected to fail
	}
	jca2.checkout();
	jca2.write(JSON_EXAMPLE_3);
	jca2.commit();

    }

    @Test
    public void testReadWriteUsingTwoObjects() throws IOException, StoreException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	JsonCasAdapter jca = new JsonCasAdapter(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	byte[] id = jca.getAnchor().getDigestAsByte();

	JsonCasAdapter jca2 = new JsonCasAdapter(store, new Digest(id));
	jca2.checkout();
	Assert.assertEquals(jca.read(), jca2.read());
    }

    private void dump(ContentAddressableStorage cas) throws StoreException {
	for (Identity d : cas.list()) {
	    System.out.println(d + "->" + cas.get(d));
	}
    }

    private void enableLog() {
	Logger LOG = Logger.getLogger("com.crypticbit");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.FINER);
	LOG.addHandler(handler);
	LOG.setLevel(Level.FINER);
    }
}
