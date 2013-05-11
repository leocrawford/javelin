package com.crypticbit.javelin.cas.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.crypticbit.javelin.cas.DigestFactory;
import com.google.common.io.ByteStreams;

/**
 * Provides a memory based CAS. The digest is calculated using a <code>digestFactory</code> which is created at
 * instantiation, and has the same life as this object. You can change the default algorithm used by accessing the
 * factory.
 */
public class MemoryCas implements ContentAddressableStorage {

    private DigestFactory digestFactory;

    public MemoryCas(DigestFactory digestFactory) {
	this.digestFactory = digestFactory;
    }

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");
    private final TreeMap<Digest, byte[]> map = new TreeMap<>();

    @Override
    public Digest store(InputStream is) throws IOException {
	byte[] data;
	data = ByteStreams.toByteArray(is);
	Digest digest = digestFactory.getDefaultDigest(data);
	map.put(digest, data);
	return digest;
    }

    @Override
    public boolean check(Digest digest) {
	return map.containsKey(digest);
    }

    @Override
    public InputStream get(Digest digest) {
	return new ByteArrayInputStream(map.get(digest));
    }

    @Override
    public List<Digest> list(Digest start) {
	return new LinkedList<Digest>(map.tailMap(start).keySet());
    }

    @Override
    public List<Digest> list() {
	return new LinkedList<Digest>(map.keySet());
    }

    @Override
    public DigestFactory getDigestFactory() {
	return digestFactory;
    }

}
