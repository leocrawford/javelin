package com.crypticbit.javelin.cas.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.cas.*;
import com.crypticbit.javelin.store.cas.DigestFactory.DigestMethod;

public class MemoryCasTest {

    private StorageFactory cf = new StorageFactory();

    @Test
    public void testChangeDigestBetweenOperations() throws UnsupportedEncodingException, StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();

	Identity md1 = cas.store(prFromString("message 1"));
	cas.getDigestFactory().setDefault(DigestMethod.SHA256);
	Identity md2 = cas.store(prFromString("message 2"));
	assertEquals("9238IACC441303APBHFSS02FVHPLCK2G", md1.getDigestAsString());
	assertEquals("GHR8RNN6B7NULVFBISLLA51H86U27DN36F3GT2R8QABN9AO9KL40====", md2.getDigestAsString());
    }

    @Test
    public void testCheck() throws UnsupportedEncodingException, StoreException, IOException, NoSuchAlgorithmException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Identity md1 = cas.store(prFromString("message 1"));
	assertTrue(cas.check(md1));
	assertFalse(cas.check(new Digest(MessageDigest.getInstance("SHA-1"))));
    }

    @Test
    public void testGet() throws UnsupportedEncodingException, StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Identity md1 = cas.store(prFromString("message 1"));
	Identity md2 = cas.store(prFromString("message 2"));

	assertEquals("message 1", prToString(cas.get(md1)));
	assertEquals("message 2", prToString(cas.get(md2)));
    }

    @Test
    public void testList() throws UnsupportedEncodingException, StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Identity md[] = new Digest[10];
	for (int loop = 0; loop < 10; loop++) {
	    md[loop] = cas.store(prFromString("message" + loop));
	}
	List<Identity> list = cas.list();
	assertEquals(10, list.size());
	// check they're in ascehnding order - and they exist
	for (int loop = 0; loop < 10; loop++) {
	    if (loop >= 1) {
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    }
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    @Test
    public void testListAfterStart() throws UnsupportedEncodingException, StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	List<Identity> createList = new LinkedList<>();
	for (int loop = 0; loop < 10; loop++) {
	    createList.add(cas.store(prFromString("message" + loop)));
	}
	Collections.sort(createList);
	List<Identity> list = cas.list(createList.get(5));
	assertEquals(5, list.size());
	// check they're in ascending order - and they exist
	for (int loop = 0; loop < 5; loop++) {
	    if (loop >= 1) {
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    }
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    @Test
    public void testStoreWithDefaultSha1DigestMethod() throws StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Identity md = cas.store(prFromString("message 1"));
	org.junit.Assert.assertEquals("9238IACC441303APBHFSS02FVHPLCK2G", md.getDigestAsString());
    }

    @Test
    public void testStoreWithSha256DigestMethod() throws StoreException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	cas.getDigestFactory().setDefault(DigestMethod.SHA256);
	Identity md1 = cas.store(prFromString("message 1"));
	Identity md2 = cas.store(prFromString("message 2"));
	org.junit.Assert.assertNotEquals(md1.getDigestAsString(), md2.getDigestAsString());
	org.junit.Assert.assertNotEquals(md1.getDigestAsByte(), md2.getDigestAsByte());
	org.junit.Assert.assertEquals("MKJATSD3877UDPE3EVMKO8H8H3NBG7SH78872458CVG0JGBLHSI0====", md1
		.getDigestAsString());
	org.junit.Assert.assertEquals("GHR8RNN6B7NULVFBISLLA51H86U27DN36F3GT2R8QABN9AO9KL40====", md2
		.getDigestAsString());
    }

    private PersistableResource prFromString(String string) throws UnsupportedEncodingException {
	return new GeneralPersistableResource(string);
    }

    private String prToString(PersistableResource pr) throws UnsupportedEncodingException, IOException {
	return pr.toString();
    }

}
