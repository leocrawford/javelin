package com.crypticbit.javelin.js;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.CasKasStore;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;

public class JsonStoreAdapterFactoryTest {

	@Test
	public void testGetKeyAdapter() throws StoreException, VisitorException {

		final CasKasStore cas = new StorageFactory().createMemoryCas();
		DataStructure ds = new DataStructure(cas);
		ds.write("[0,1,{2:3,4:\"a\"},4,null]").commit();

		JsonStoreAdapterFactory adapter = new JsonStoreAdapterFactory(cas);

		assertEquals(8,adapter.getKeyAdapter().visit(
				ds.getCommit().getIdentity()).size());

	}

}