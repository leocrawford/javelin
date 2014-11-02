package com.crypticbit.javelin.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class KeyAdapterTest {

    @Test
    public void testToByteArray() {

	KeyAdapter testAdapter = new KeyAdapter();
	Key a = new Key();
	Key b = new Key();

	byte[] abyte = testAdapter.toByteArray(a);
	byte[] bbyte = testAdapter.toByteArray(b);

	Key aCopy = testAdapter.fromByteArray(abyte);
	Key bCopy = testAdapter.fromByteArray(bbyte);

	assertEquals(a, aCopy);
	assertEquals(b, bCopy);
	assertNotEquals(a, b);

    }

}
