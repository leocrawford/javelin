package com.crypticbit.javelin.merkle;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.convert.TreeMapperException;
import com.crypticbit.javelin.merkle.Commit;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.merkle.MerkleTree.MergeType;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class DataStructureTest extends TestUtils{
// FIXME reinstate null
    /* 
    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,5]]";
    private static final String JSON_EXAMPLE_4 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7]]";
    private static final String JSON_EXAMPLE_5 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3,7,8]]";
*/
    
    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3]]";
    private static final String JSON_EXAMPLE_2 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,4]]";
    private static final String JSON_EXAMPLE_3 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,5]]";
    private static final String JSON_EXAMPLE_4 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,7]]";
    private static final String JSON_EXAMPLE_5 = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,[1,2,3,7,8]]";

    @Test
    public void testBasicBranch() throws IOException, StoreException, JsonSyntaxException, PatchFailedException,
	    TreeMapperException {
	String JSON_EXAMPLEa = "[\"foo\",{\"a\":1,\"b\":TRUE}]";
	String JSON_EXAMPLE_2a = "[\"foo\",{\"a\":2,\"b\":FALSE}]";
	String JSON_EXAMPLE_3a = "[\"a\",{\"a\":1,\"b\":TRUE,\"c\":2.1}]";

	enableLog();
	AddressableStorage  memoryStore = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(memoryStore);
	jca.write(JSON_EXAMPLEa).commit();
	MerkleTree jca2 = jca.branch();

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
    public void testBasicReadWrite() throws IOException, StoreException, JsonSyntaxException, TreeMapperException {
	enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.checkout();
	Assert.assertEquals(jca.read(), jca.read());
    }

    @Test
    public void testCommitForComplexMultipleReadWrite() throws IOException, StoreException, JsonSyntaxException,
	    TreeMapperException {
	// enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
	jca.write(JSON_EXAMPLE).commit().getCommit();
	Commit c2 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE_3).commit().getCommit();

	Assert.assertEquals(2, c2.getShortestHistory().size());

	// use original value
	Commit c3 = jca.write(JSON_EXAMPLE_2).write(JSON_EXAMPLE).commit().getCommit();

	Assert.assertEquals(3, c3.getShortestHistory().size());

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE), c3.getElement());
    }

    @Test
    public void testCommitForMultipleReadWrite() throws IOException, StoreException, TreeMapperException {
	// enableLog();
	MerkleTree jca = new MerkleTree(new StorageFactory().createMemoryCas());
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
    public void testConcurrentWriteUsingTwoObjects() throws IOException, StoreException, TreeMapperException {
	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	jca.write(JSON_EXAMPLE_2);

	MerkleTree jca2 = new MerkleTree(store, jca.getLabelsAddress(), "HEAD");
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
    public void testMultiplBranches() throws StoreException, TreeMapperException {
	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree d1 = new MerkleTree(store).write(JSON_EXAMPLE).commit();
	MerkleTree d2 = d1.branch();
	d1.write(JSON_EXAMPLE_2).commit().saveLabel("Branch1");
	d2.write(JSON_EXAMPLE_3).commit().saveLabel("Branch2");
	MerkleTree d3 = d2.branch();
	d3.write(JSON_EXAMPLE_4).commit().saveLabel("Branch3");
	d2.write(JSON_EXAMPLE_5).commit();

	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_2), new MerkleTree(store, d1.getLabelsAddress(),
		"Branch1").checkout().read());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_5), new MerkleTree(store, d1.getLabelsAddress(),
		"Branch2").checkout().read());
	Assert.assertEquals(new JsonParser().parse(JSON_EXAMPLE_4), new MerkleTree(store, d1.getLabelsAddress(),
		"Branch3").checkout().read());

    }

    @Test
    public void testReadWriteUsingTwoObjects() throws IOException, StoreException, JsonSyntaxException,
	    TreeMapperException {
	AddressableStorage store = new StorageFactory().createMemoryCas();
	MerkleTree jca = new MerkleTree(store);
	jca.write(JSON_EXAMPLE);
	jca.commit();
	byte[] labelsAddress = jca.getLabelsAddress().getKeyAsBytes();

	MerkleTree jca2 = new MerkleTree(store, new Key(labelsAddress), "HEAD");
	jca2.checkout();
	Assert.assertEquals(jca.read(), jca2.read());
    }

    @Test
    public void testImportExport() throws IOException, StoreException, JsonSyntaxException, TreeMapperException,
	    ClassNotFoundException, PatchFailedException, InterruptedException {
	MerkleTree ds1 = new MerkleTree(new StorageFactory().createMemoryCas());
	MerkleTree ds2 = new MerkleTree(new StorageFactory().createMemoryCas());

	ds1.write(JSON_EXAMPLE);
	ds1.commit();

	copy(ds1, ds2, MergeType.OVERWRITE);
	Assert.assertEquals(ds1.read(), ds2.read());
	
	ds1.write(JSON_EXAMPLE_2);
	ds1.commit();

	copy(ds1, ds2, MergeType.MERGE);
	Assert.assertEquals(ds1.read(), ds2.read());
	
	ds2.write(JSON_EXAMPLE_3);
	ds2.commit();
	
	ds1.write(JSON_EXAMPLE_5);
	ds1.commit();
	
	copy(ds1, ds2, MergeType.MERGE);
	
	System.out.println(ds1.getCommit().getAsGraphToRoot());
	System.out.println(ds2.getCommit().getAsGraphToRoot());
	
//	show(ds1.getCommit(),ds2.getCommit());
//	Thread.sleep(1000*1000);
    }

    private void copy(MerkleTree ds1, MerkleTree ds2, MergeType mt) throws StoreException, TreeMapperException,
	    IOException, ClassNotFoundException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ds1.exportAll(out);
	ds2.importAll(new ByteArrayInputStream(out.toByteArray()), mt);
	ds2.checkout();
    }

    private void dump(AddressableStorage cas) throws StoreException {
	for (Key d : cas.listCas()) {
	    System.out.println(d + "->" + cas.getCas(d,JsonElement.class));
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
