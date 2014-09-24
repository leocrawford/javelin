package com.crypticbit.javelin.merkle;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.convert.js.JsonStoreAdapterFactory;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;

public class JsonStoreAdapterFactoryTest {

    @Test
    public void testGetKeyAdapter() throws StoreException, VisitorException {

	final AddressableStorage cas = new StorageFactory().createMemoryCas();
	MerkleTree ds = new MerkleTree(cas);
	ds.write("[0,1,{2:3,4:\"a\"},4,null]").commit();

	JsonStoreAdapterFactory adapter = new JsonStoreAdapterFactory(cas);

	assertEquals(8, adapter.getKeyAdapter().visit(ds.getCommit().getHead()).size());

    }

}
