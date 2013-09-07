package com.crypticbit.javelin.js;

import static org.junit.Assert.fail;

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
		ds.write("[0,1,2,3]").commit();

		JsonStoreAdapterFactory adapter = new JsonStoreAdapterFactory(cas);

		System.out.println(adapter.getKeyAdapter().visit(
				ds.getCommit().getIdentity()));

	}

}
