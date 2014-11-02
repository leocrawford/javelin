package com.crypticbit.javelin.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class MemoryAddressableStorageTest {

	private StorageFactory cf = new StorageFactory();

	@Test
	public void testGet() throws UnsupportedEncodingException, StoreException,
			IOException {
		AddressableStorage cas = cf.createMemoryCas();
		Key md1 = cas.store(prFromString("\"message 1\""), JsonElement.class);
		Key md2 = cas.store(prFromString("\"message 2\""), JsonElement.class);

		assertEquals("\"message 1\"",
				prToString(cas.getCas(md1, JsonElement.class)));
		assertEquals("\"message 2\"",
				prToString(cas.getCas(md2, JsonElement.class)));
	}

	@Test
	public void testList() throws UnsupportedEncodingException, StoreException,
			IOException {
		AddressableStorage cas = cf.createMemoryCas();
		Key md[] = new Key[10];
		for (int loop = 0; loop < 10; loop++) {
			md[loop] = cas.store(prFromString("message" + loop),
					JsonElement.class);
		}
		List<Key> list = cas.listCas();
		assertEquals(10, list.size());
		// check they're in ascehnding order - and they exist
		for (int loop = 0; loop < 10; loop++) {
			if (loop >= 1) {
				assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
			}
			assertTrue(cas.checkCas(list.get(loop)));
		}
	}

	@Test
	public void testListAfterStart() throws UnsupportedEncodingException,
			StoreException, IOException {
		AddressableStorage cas = cf.createMemoryCas();
		List<Key> createList = new LinkedList<>();
		for (int loop = 0; loop < 10; loop++) {
			createList.add(cas.store(prFromString("message" + loop),
					JsonElement.class));
		}
		Collections.sort(createList);
		List<Key> list = cas.listCas(createList.get(5));
		assertEquals(5, list.size());
		// check they're in ascending order - and they exist
		for (int loop = 0; loop < 5; loop++) {
			if (loop >= 1) {
				assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
			}
			assertTrue(cas.checkCas(list.get(loop)));
		}
	}

	private Gson gson = new Gson();

	private JsonElement prFromString(String string)
			throws UnsupportedEncodingException {
		return gson.fromJson(string, JsonElement.class);
	}

	private String prToString(JsonElement jsonElement)
			throws UnsupportedEncodingException, IOException {
		return jsonElement.toString();
	}

	@Test
	public void testTypes() throws StoreException, IOException,
			JsonSyntaxException {
		// enableLog("com.crypticbit.javelin.js", Level.FINEST);
		//
		// DataAccessInterface<CommitDao> a = new JsonStoreAdapterFactory(new
		// StorageFactory().createMemoryCas())
		// .getSimpleObjectAdapter(CommitDao.class);
		//
		// Key d1 = new Key();
		// Key d2 = new Key();
		// Key d3 = new Key();
		//
		// Date d = new Date();
		//
		// CommitDao cd1 = new CommitDao(d1, d, "test", new Key[] { d2, d3 });
		//
		// Key i = a.write(cd1);
		//
		// CommitDao cd2 = a.read(i);
		//
		// assertEquals(d.getTime() / 1000, cd2.getWhen().getTime() / 1000);
		// assertEquals(d1, cd2.getHead());
		// assertEquals("test", cd2.getUser());
		// assertEquals(d2, cd2.getParents()[0]);
		// assertEquals(d3, cd2.getParents()[1]);

	}

}
