package com.crypticbit.javelin.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class JsonAdapterTest {

    @Test
    public void testGetContentDigest() {

	JsonAdapter<TestDao> testAdapter = new JsonAdapter<TestDao>(TestDao.class);
	TestDao a = new TestDao("test a");
	TestDao b = new TestDao("test b");

	Key keyA = testAdapter.getContentDigest(a);
	Key keyATake2 = testAdapter.getContentDigest(a);
	Key keyB = testAdapter.getContentDigest(b);

	// check repeatable
	assertEquals(keyA, keyATake2);
	// check unique
	assertNotEquals(keyA, keyB);
    }

    @Test
    public void testToAndFromByteArray() {

	JsonAdapter<TestDao> testAdapter = new JsonAdapter<TestDao>(TestDao.class);
	TestDao a = new TestDao("test a");
	TestDao b = new TestDao("test b");

	byte[] abyte = testAdapter.toByteArray(a);
	byte[] bbyte = testAdapter.toByteArray(b);

	TestDao aCopy = testAdapter.fromByteArray(abyte);
	TestDao bCopy = testAdapter.fromByteArray(bbyte);

	assertEquals(a.value, aCopy.value);
	assertEquals(b.value, bCopy.value);

    }

    @Test
    public void testToAndFromByteArrayForKeyValue() {

	JsonAdapter<KeyDao> testAdapter = new JsonAdapter<KeyDao>(KeyDao.class);
	KeyDao a = new KeyDao(new Key());
	KeyDao b = new KeyDao(new Key());

	byte[] abyte = testAdapter.toByteArray(a);
	byte[] bbyte = testAdapter.toByteArray(b);

	KeyDao aCopy = testAdapter.fromByteArray(abyte);
	KeyDao bCopy = testAdapter.fromByteArray(bbyte);

	assertEquals(a.value, aCopy.value);
	assertEquals(b.value, bCopy.value);
	assertNotEquals(a.value, b.value);

    }

    private static class KeyDao {
	private Key value;

	KeyDao(Key value) {
	    this.value = value;
	}
    }

    private static class TestDao {
	private String value;

	TestDao(String value) {
	    this.value = value;
	}
    }

}
