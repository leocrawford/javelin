package com.crypticbit.javelin.js;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class DataStructureTest {

    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,5]]";
    private static final String JSON_EXAMPLE_4 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7]]";
    private static final String JSON_EXAMPLE_5 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7,8]]";

    @Test
    public void testBasicBranch() throws IOException, StoreException, JsonSyntaxException, PatchFailedException,
	    VisitorException {
	String JSON_EXAMPLEa = "[\"foo\",{\"a\":1,\"b\":TRUE}]";
	String JSON_EXAMPLE_2a = "[\"foo\",{\"a\":2,\"b\":FALSE}]";
	String JSON_EXAMPLE_3a = "[\"a\",{\"a\":1,\"b\":TRUE,\"c\":2.1}]";

	enableLog();
	CasKasStore memoryStore = new StorageFactory().createMemoryCas();
	DataStructure jca = new DataStructure(memoryStore);
	jca.write(JSON_EXAMPLEa).commit();
	DataStructure jca2 = jca.branch();

	jca.write(JSON_EXAMPLE_2a).commit();
	jca2.write(JSON_EXAMPLE_3a).commit();

	// System.out.println(jca.read());
	// System.out.println(jca.getCommit().getShortestHistory());
	//
	// System.out.println(jca2.read());
	// System.out.println(jca2.getCommit().getShortestHistory());
	//
	// System.out.println("JCA-LR" + jca.lazyRead());
	// System.out.println("JCS2-LR" + jca2.lazyRead());
	//
	// System.out.println("A="+jca.read());
	// System.out.println(jca.getCommit().getShortestHistory());
	jca.merge(jca2);
	// System.out.println("B="+jca.read());
	// System.out.println(jca.getCommit().getShortestHistory());

	// System.out.println(memoryStore);
	// jca.checkout();
	System.out.println("MERGE=" + jca.lazyRead());
	// Assert.assertEquals(new JsonParser().parse("[\"a\", {\"b\":FALSE, \"c\":2.1, \"a\":2}]"), jca.read());

    }

    @Test
    public void testBasicReadWrite() throws IOException, StoreException, JsonSyntaxException, VisitorException {
	enableLog();
	DataStructure jca = new DataStructure(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.checkout();
	Assert.assertEquals(jca.read(), jca.read());
    }

    @Test
    public void testCommitForComplexMultipleReadWrite() throws IOException, StoreException, JsonSyntaxException,
	    VisitorException {
	// enableLog();
	DataStructure jca = new DataStructure(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE).commit().getCommit();
	Commit c2 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE_3).commit().getCommit();

	Assert.assertEquals(2, c2.getShortestHistory().size());

	// use original value
	Commit c3 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE).commit().getCommit();

	Assert.assertEquals(3, c3.getShortestHistory().size());

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE), c3.getElement());
    }

    @Test
    public void testCommitForMultipleReadWrite() throws IOException, StoreException, VisitorException {
	// enableLog();
	DataStructure jca = new DataStructure(new StorageFactory().createMemoryCas());
	Commit c1 = jca.write(JSON_EXAMPLE).commit().getCommit();
	Commit c2 = jca.write(JSON_EXAMPLE_2).commit().getCommit();
	Commit c3 = jca.write(JSON_EXAMPLE_3).commit().getCommit();

	Assert.assertEquals(1, c1.getShortestHistory().size());
	Assert.assertEquals(3, c3.getShortestHistory().size());

	Assert.assertEquals(0, c1.getParents().size());
	Assert.assertEquals(1, c2.getParents().size());

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE), c3.getShortestHistory().get(2).getElement());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_3), c3.getShortestHistory().get(0).getElement());
    }

    @Test
    public void testConcurrentWriteUsingTwoObjects() throws IOException, StoreException, VisitorException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	DataStructure jca = new DataStructure(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.write(JSON_EXAMPLE_2);

	DataStructure jca2 = new DataStructure(store, jca.getLabelsAddress(), "HEAD");
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
    public void testMultiplBranches() throws StoreException, VisitorException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	DataStructure d1 = new DataStructure(store).write(JSON_EXAMPLE).commit();
	DataStructure d2 = d1.branch();
	d1.write(JSON_EXAMPLE_2).commit().saveLabel("Branch1");
	d2.write(JSON_EXAMPLE_3).commit().saveLabel("Branch2");
	DataStructure d3 = d2.branch();
	d3.write(JSON_EXAMPLE_4).commit().saveLabel("Branch3");
	d2.write(JSON_EXAMPLE_5).commit();

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_2), new DataStructure(store, d1.getLabelsAddress(), "Branch1")
		.checkout().read());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_5), new DataStructure(store, d1.getLabelsAddress(), "Branch2")
		.checkout().read());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_4), new DataStructure(store, d1.getLabelsAddress(), "Branch3")
		.checkout().read());

    }

    @Test
    public void testReadWriteUsingTwoObjects() throws IOException, StoreException, JsonSyntaxException,
	    VisitorException {
	CasKasStore store = new StorageFactory().createMemoryCas();
	DataStructure jca = new DataStructure(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	byte[] labelsAddress = SerializationUtils.serialize(jca.getLabelsAddress());

	DataStructure jca2 = new DataStructure(store, (Identity) SerializationUtils.deserialize(labelsAddress), "HEAD");
	jca2.checkout();
	Assert.assertEquals(jca.read(), jca2.read());
    }

    private void dump(ContentAddressableStorage cas) throws StoreException {
	for (Identity d : cas.list()) {
	    System.out.println(d + "->" + cas.get(d));
	}
    }

    private void enableLog() {
	Logger LOG = Logger.getLogger("com.crypticbit.javelin");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.FINEST);
	LOG.addHandler(handler);
	LOG.setLevel(Level.FINEST);
    }
}
