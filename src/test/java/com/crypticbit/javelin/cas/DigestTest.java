package com.crypticbit.javelin.cas;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

public class DigestTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void test() {
	byte[] randomData = createRandomData();
	Digest originalDigest = new DigestFactory().getDefaultDigest(randomData);
	byte[] originalDigestAsByte = originalDigest.getDigestAsByte();
	String originalDigestAsString = originalDigest.getDigestAsString();
	Digest recoveredDigestByByte = new Digest(originalDigestAsByte);
	org.junit.Assert.assertArrayEquals(originalDigestAsByte,recoveredDigestByByte.getDigestAsByte());
	org.junit.Assert.assertEquals(originalDigestAsString,recoveredDigestByByte.getDigestAsString());
	Digest recoveredDigestByString = new Digest(originalDigestAsString);
	org.junit.Assert.assertArrayEquals(originalDigestAsByte,recoveredDigestByString.getDigestAsByte());
	org.junit.Assert.assertEquals(originalDigestAsString,recoveredDigestByString.getDigestAsString());
    }

    private static byte[] createRandomData() {
	byte[] b = new byte[20];
	new Random().nextBytes(b);
	return b;
    }
}
