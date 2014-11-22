package com.crypticbit.javelin.merkle;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.merkle.MerkleTree.MergeType;
import com.crypticbit.javelin.store.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class MarkleTreeTest extends TestUtils {
    // FIXME reinstate null
    /*
     * private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]"; private
     * static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,4]]"; private static
     * final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,5]]"; private static final
     * String JSON_EXAMPLE_4 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7]]"; private static final String
     * JSON_EXAMPLE_5 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7,8]]";
     */

    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,5]]";
    private static final String JSON_EXAMPLE_4 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,7]]";
    private static final String JSON_EXAMPLE_5 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,7,8]]";

    @Test
    public void testBasicBranch() throws IOException, StoreException, JsonSyntaxException, PatchFailedException,
	    MergeException, CorruptTreeException {
	String JSON_EXAMPLEa = "[\"foo\",{\"a\":1,\"b\":TRUE}]";
	String JSON_EXAMPLE_2a = "[\"foo\",{\"a\":2,\"b\":FALSE}]";
	String JSON_EXAMPLE_3a = "[\"a\",{\"a\":1,\"b\":TRUE,\"c\":2.1}]";

	enableLog();
	AddressableStorage memoryStore = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(memoryStore);
	jca.write(JSON_EXAMPLEa);

	MerkleTree jca2 = jca.branch();

	jca.write(JSON_EXAMPLE_2a);
	jca2.write(JSON_EXAMPLE_3a);

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
	System.out.println("MERGE=" + jca.getAsObject());
	// Assert.assertEquals(new JsonParser().parse("[\"a\", {\"b\":FALSE, \"c\":2.1, \"a\":2}]"), jca.read());

    }

    @Test
    public void testBasicReadWrite() throws IOException, StoreException, JsonSyntaxException, CorruptTreeException {
	enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	Assert.assertEquals(jca.read(), jca.read());
    }

    @Test
    public void testCommitForComplexMultipleReadWrite() throws IOException, StoreException, JsonSyntaxException,
	    CorruptTreeException {
	// enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	Commit c2 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE_3).getCommit();

	Assert.assertEquals(3, c2.getShortestHistory().size());

	// use original value
	Commit c3 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE).getCommit();

	Assert.assertEquals(5, c3.getShortestHistory().size());

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE).toString(), c3.getAsElement().toString());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_3).toString(), c3.getFirstParent().getFirstParent().getAsElement().toString());
    }

    @Test
    public void testCommitForMultipleReadWrite() throws IOException, StoreException, CorruptTreeException {
	enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
	Commit c1 = jca.write(JSON_EXAMPLE).getCommit();
	Commit c2 = jca.write(JSON_EXAMPLE_2).getCommit();
	Commit c3 = jca.write(JSON_EXAMPLE_3).getCommit();

	Assert.assertEquals(1, c1.getShortestHistory().size());
	Assert.assertEquals(3, c3.getShortestHistory().size());

	Assert.assertEquals(0, c1.getParents().size());
	Assert.assertEquals(1, c2.getParents().size());

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE).toString(), c3.getShortestHistory().get(2)
		.getAsElement().toString());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_3).toString(), c3.getShortestHistory().get(0)
		.getAsElement().toString());
    }

    @Test
    public void testConcurrentWriteUsingTwoObjects() throws IOException, StoreException, CorruptTreeException {
	enableLog();
	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(store);
	jca.write(JSON_EXAMPLE);
	jca.write(JSON_EXAMPLE_2);

	MerkleTree jca2 = new MerkleTree(store, jca.getLabelsAddress(), "HEAD");
	jca2.write(JSON_EXAMPLE_3);

	try {
	    fail("Concurrent modification");
	}
	// FIXME
	catch (Error e) {
	    // expected to fail
	}
	jca2.write(JSON_EXAMPLE_3);

    }

    @Test
    public void testImportExport() throws IOException, StoreException, JsonSyntaxException, ClassNotFoundException,
	    PatchFailedException, InterruptedException, CorruptTreeException {
	
	MemoryAddressableStorage store1 = (MemoryAddressableStorage) new StorageFactory().createMemoryCas();
	
	MerkleTree ds1 = new MerkleTree(store1);
	
	ds1.write(JSON_EXAMPLE);
	
	MerkleTree ds2 = new MerkleTree(store1.clone(),ds1.getLabelsAddress(),MerkleTree.HEAD);

	ds1.write(JSON_EXAMPLE_2);
	copy(ds2, ds1.getStore(), MerkleTree.HEAD, MergeType.OVERWRITE);
	
	Assert.assertEquals(ds1.read(), ds2.read());

	ds1.write(JSON_EXAMPLE_2);

	copy(ds2, ds1.getStore(),MerkleTree.HEAD, MergeType.MERGE);
	Assert.assertEquals(ds1.read(), ds2.read());

	ds2.write(JSON_EXAMPLE_3);

	ds1.write(JSON_EXAMPLE_5);

	copy(ds1, ds2.getStore(), MerkleTree.HEAD, MergeType.MERGE);

	System.out.println(ds1.getCommit().getAsGraphToRoot());
	System.out.println(ds2.getCommit().getAsGraphToRoot());

	// show(ds1.getCommit(),ds2.getCommit());
	// Thread.sleep(1000*1000);
    }

    
    @Test
    public void testMultiplBranches() throws StoreException, CorruptTreeException {
	enableLog();

	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree d1 = new MerkleTree(store).write(JSON_EXAMPLE);
	MerkleTree d2 = d1.branch();
	d1.write(JSON_EXAMPLE_2).createLabel("Branch1");
	d2.write(JSON_EXAMPLE_3).createLabel("Branch2");
	MerkleTree d3 = d2.branch();
	d3.write(JSON_EXAMPLE_4).createLabel("Branch3");
	d2.write(JSON_EXAMPLE_5);

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_2).toString(), new MerkleTree(store, d1
		.getLabelsAddress(), "Branch1").read().toString());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_5).toString(), new MerkleTree(store, d1
		.getLabelsAddress(), "Branch2").read().toString());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_4).toString(), new MerkleTree(store, d1
		.getLabelsAddress(), "Branch3").read().toString());

    }

    @Test
    public void testReadWriteUsingTwoObjects() throws IOException, StoreException, JsonSyntaxException,
	    CorruptTreeException {
	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(store);
	jca.write(JSON_EXAMPLE);
	byte[] labelsAddress = jca.getLabelsAddress().getKeyAsBytes();

	MerkleTree jca2 = new MerkleTree(store, new Key(labelsAddress), "HEAD");
	Assert.assertEquals(jca.read().toString(), jca2.read().toString());
    }
    
    
    

    private void copy(MerkleTree to, AddressableStorage from, String label, MergeType mergeType) throws StoreException, IOException,
	    ClassNotFoundException, CorruptTreeException {
	to.sync(from, label, mergeType);
    }

    private void dump(AddressableStorage cas) throws StoreException {
	for (Key d : cas.listCas()) {
	    System.out.println(d + "->" + cas.getCas(d, JsonElement.class));
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
