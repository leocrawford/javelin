package com.crypticbit.javelin.cas;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.DigestFactory;

public class DigestTest {

    @Test
    public void test() {
	byte[] randomData = createRandomData();
	Digest originalDigest = new DigestFactory().getDefaultDigest(randomData);
	byte[] originalDigestAsByte = originalDigest.getDigestAsByte();
	String originalDigestAsString = originalDigest.getDigestAsString();
	Digest recoveredDigestByByte = new Digest(originalDigestAsByte);
	assertArrayEquals(originalDigestAsByte, recoveredDigestByByte.getDigestAsByte());
	assertEquals(originalDigestAsString, recoveredDigestByByte.getDigestAsString());
	Digest recoveredDigestByString = new Digest(originalDigestAsString);
	assertArrayEquals(originalDigestAsByte, recoveredDigestByString.getDigestAsByte());
	assertEquals(originalDigestAsString, recoveredDigestByString.getDigestAsString());
    }

    @Test
    public void testCompare() {
	byte[] random = createRandomData();
	Digest d1 = new Digest(random);
	Digest d2 = new Digest(random);

	assertEquals(0, d1.compareTo(d2));

	byte[] newRandom = new byte[random.length + 1];
	System.arraycopy(random, 0, newRandom, 0, random.length);
	newRandom[random.length] = 0x5;
	Digest d3 = new Digest(newRandom);

	assertTrue(d1.compareTo(d3) < 0);
	assertTrue(d3.compareTo(d1) > 0);

    }

    @Test
    public void testEquals() {
	byte[] random = createRandomData();
	Digest d1 = new Digest(random);
	Digest d2 = new Digest(random);

	assertEquals(d1, d2);

	Digest d3 = new Digest(createRandomData());

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
