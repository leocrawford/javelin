package com.crypticbit.javelin.cas;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.KeyFactory;

public class DigestTest {

    @Test
    public void test() {
	byte[] randomData = createRandomData();
	Key originalDigest = new KeyFactory().getDefaultDigest(randomData);
	byte[] originalDigestAsByte = originalDigest.getKeyAsBytes();
	String originalDigestAsString = originalDigest.getKeyAsString();
	Key recoveredDigestByByte = new Key(originalDigestAsByte);
	assertArrayEquals(originalDigestAsByte, recoveredDigestByByte.getKeyAsBytes());
	assertEquals(originalDigestAsString, recoveredDigestByByte.getKeyAsString());
	Key recoveredDigestByString = new Key(originalDigestAsString);
	assertArrayEquals(originalDigestAsByte, recoveredDigestByString.getKeyAsBytes());
	assertEquals(originalDigestAsString, recoveredDigestByString.getKeyAsString());
    }

    @Test
    public void testCompare() {
	byte[] random = createRandomData();
	Key d1 = new Key(random);
	Key d2 = new Key(random);

	assertEquals(0, d1.compareTo(d2));

	byte[] newRandom = new byte[random.length + 1];
	System.arraycopy(random, 0, newRandom, 0, random.length);
	newRandom[random.length] = 0x5;
	Key d3 = new Key(newRandom);

	assertTrue(d1.compareTo(d3) < 0);
	assertTrue(d3.compareTo(d1) > 0);

    }

    @Test
    public void testEquals() {
	byte[] random = createRandomData();
	Key d1 = new Key(random);
	Key d2 = new Key(random);

	assertEquals(d1, d2);

	Key d3 = new Key(createRandomData());

	assertNotEquals(d2, d3);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    private static byte[] createRandomData() {
	byte[] b = new byte[20];
	new Random().nextBytes(b);
	return b;
    }
}
