package com.crypticbit.javelin.js;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.junit.Test;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.DigestFactory;
import com.crypticbit.javelin.store.memory.MemoryCasKas;

public class JsonSimpleClassAdapterTest extends TestUtils {

    @Test
    public void test() throws StoreException, IOException {
	enableLog("com.crypticbit.javelin.js", Level.FINEST);

	DataAccessInterface<CommitDao> a = new JsonStoreAdapterFactory(new MemoryCasKas(new DigestFactory()))
		.getSimpleObjectAdapter(CommitDao.class);

	Digest d1 = new Digest();
	Digest d2 = new Digest();
	Digest d3 = new Digest();

	Date d = new Date();

	CommitDao cd1 = new CommitDao(d1, d, "test", new Digest[] { d2, d3 });

	Identity i = a.write(cd1);

	CommitDao cd2 = a.read(i);

	assertEquals(d.getTime() / 1000, cd2.getWhen().getTime() / 1000);
	assertEquals(d1, cd2.getHead());
	assertEquals("test", cd2.getUser());
	assertEquals(d2, cd2.getParents()[0]);
	assertEquals(d3, cd2.getParents()[1]);

    }

}
