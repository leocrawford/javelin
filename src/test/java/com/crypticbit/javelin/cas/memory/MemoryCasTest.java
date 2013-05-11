package com.crypticbit.javelin.cas.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.crypticbit.javelin.cas.*;
import com.crypticbit.javelin.cas.DigestFactory.DigestMethod;
import com.google.common.io.CharStreams;

public class MemoryCasTest {

    private CasFactory cf = new CasFactory();

    @Test
    public void testCheck() throws UnsupportedEncodingException, CasException, IOException, NoSuchAlgorithmException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Digest md1 = cas.store(isFromString("message 1"));
	assertTrue(cas.check(md1));
	assertFalse(cas.check(new Digest(MessageDigest.getInstance("SHA-1"))));
    }

    @Test
    public void testGet() throws UnsupportedEncodingException, CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Digest md1 = cas.store(isFromString("message 1"));
	Digest md2 = cas.store(isFromString("message 2"));

	assertEquals("message 1", isToString(cas.get(md1)));
	assertEquals("message 2", isToString(cas.get(md2)));
    }

    @Test
    public void testChangeDigestBetweenOperations() throws UnsupportedEncodingException, CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();

	Digest md1 = cas.store(isFromString("message 1"));
	cas.getDigestFactory().setDefault(DigestMethod.SHA256);
	Digest md2 = cas.store(isFromString("message 2"));
	assertEquals("488689298c2102300d595c5fce004ffc73565050", md1.getDigestAsString());
	assertEquals("84768ddee659efeafdeb972b55143141bc23b6e333c70e8b68d29774ab09a548", md2.getDigestAsString());
    }

    @Test
    public void testList() throws UnsupportedEncodingException, CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Digest md[] = new Digest[10];
	for (int loop = 0; loop < 10; loop++)
	    md[loop] = cas.store(isFromString("message" + loop));
	List<Digest> list = cas.list();
	assertEquals(10, list.size());
	// check they're in ascehnding order - and they exist
	for (int loop = 0; loop < 10; loop++) {
	    if (loop >= 1)
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    @Test
    public void testListAfterStart() throws UnsupportedEncodingException, CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	List<Digest> createList = new LinkedList<Digest>();
	for (int loop = 0; loop < 10; loop++)
	    createList.add(cas.store(isFromString("message" + loop)));
	Collections.sort(createList);
	List<Digest> list = cas.list(createList.get(5));
	assertEquals(5, list.size());
	// check they're in ascending order - and they exist
	for (int loop = 0; loop < 5; loop++) {
	    if (loop >= 1)
		assertTrue(list.get(loop - 1).compareTo(list.get(loop)) < 0);
	    assertTrue(cas.check(list.get(loop)));
	}
    }

    @Test
    public void testStoreWithDefaultSha1DigestMethod() throws CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	Digest md = cas.store(isFromString("message 1"));
	org.junit.Assert.assertEquals("488689298c2102300d595c5fce004ffc73565050", md.getDigestAsString());
    }

    @Test
    public void testStoreWithSha256DigestMethod() throws CasException, IOException {
	ContentAddressableStorage cas = cf.createMemoryCas();
	cas.getDigestFactory().setDefault(DigestMethod.SHA256);
	Digest md1 = cas.store(isFromString("message 1"));
	Digest md2 = cas.store(isFromString("message 2"));
	org.junit.Assert.assertNotEquals(md1.getDigestAsString(), md2.getDigestAsString());
	org.junit.Assert.assertNotEquals(md1.getDigestAsByte(), md2.getDigestAsByte());
	org.junit.Assert.assertEquals("b526aef1a341cfe6e5c377ed4c222888eeb81f913a107110a867e009c1758f24", md1
		.getDigestAsString());
	org.junit.Assert.assertEquals("84768ddee659efeafdeb972b55143141bc23b6e333c70e8b68d29774ab09a548", md2
		.getDigestAsString());
    }

    private ByteArrayInputStream isFromString(String string) throws UnsupportedEncodingException {
	return new ByteArrayInputStream(string.getBytes("UTF-8"));
    }

    private String isToString(InputStream is) throws UnsupportedEncodingException, IOException {
	return CharStreams.toString(new InputStreamReader(is, "UTF-8"));
    }

}
