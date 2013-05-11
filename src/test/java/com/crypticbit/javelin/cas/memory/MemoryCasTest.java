package com.crypticbit.javelin.cas.memory;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.crypticbit.javelin.cas.*;
import com.crypticbit.javelin.cas.DigestFactory.DigestMethod;

public class MemoryCasTest {

    private CasFactory cf = new CasFactory();

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

    @Test
    public void testCheck() {
	fail("Not yet implemented");
    }

    @Test
    public void testGet() {
	fail("Not yet implemented");
    }

    @Test
    public void testListMessageDigest() {
	fail("Not yet implemented");
    }

    @Test
    public void testList() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetDigestFactory() {
	fail("Not yet implemented");
    }

    private ByteArrayInputStream isFromString(String string) throws UnsupportedEncodingException {
	return new ByteArrayInputStream(string.getBytes("UTF-8"));
    }

}
