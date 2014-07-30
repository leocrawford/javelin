package com.crypticbit.javelin.js;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.junit.Test;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.*;
import com.google.gson.JsonSyntaxException;

public class JsonSimpleClassAdapterTest extends TestUtils {

    @Test
    public void test() throws StoreException, IOException, JsonSyntaxException, VisitorException {
	enableLog("com.crypticbit.javelin.js", Level.FINEST);

	DataAccessInterface<CommitDao> a = new JsonStoreAdapterFactory(new StorageFactory().createMemoryCas())
		.getSimpleObjectAdapter(CommitDao.class);

	Key d1 = new Key();
	Key d2 = new Key();
	Key d3 = new Key();

	Date d = new Date();

	CommitDao cd1 = new CommitDao(d1, d, "test", new Key[] { d2, d3 });

	Key i = a.write(cd1);

	CommitDao cd2 = a.read(i);

	assertEquals(d.getTime() / 1000, cd2.getWhen().getTime() / 1000);
	assertEquals(d1, cd2.getHead());
	assertEquals("test", cd2.getUser());
	assertEquals(d2, cd2.getParents()[0]);
	assertEquals(d3, cd2.getParents()[1]);

    }

}
