package com.crypticbit.javelin.store.cas.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.JsonPersistableResource;
import com.crypticbit.javelin.store.cas.*;
import com.google.common.io.ByteStreams;

/**
 * Provides a memory based CAS. The digest is calculated using a <code>digestFactory</code> which is created at
 * instantiation, and has the same life as this object. You can change the default algorithm used by accessing the
 * factory.
 */
public class MemoryCas implements ContentAddressableStorage {

    private DigestFactory digestFactory;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");

    private final TreeMap<Digest, byte[]> map = new TreeMap<>();

    public MemoryCas(DigestFactory digestFactory) {
	this.digestFactory = digestFactory;
    }

    @Override
    public boolean check(Digest digest) {
	return map.containsKey(digest);
    }

    @Override
    public PersistableResource get(Digest digest) {
	JsonPersistableResource pr = new JsonPersistableResource(map.get(digest));
	if (LOG.isLoggable(Level.FINER))
	    LOG.log(Level.FINER, "Read "+pr.getBytes().length+" bytes from " + digest +" using memory CAS");
	return pr;
    }

    @Override
    public DigestFactory getDigestFactory() {
	return digestFactory;
    }

    @Override
    public List<Digest> list() {
	return new LinkedList<Digest>(map.keySet());
    }

    @Override
    public List<Digest> list(Digest start) {
	return new LinkedList<Digest>(map.tailMap(start).keySet());
    }

    @Override
    public Digest store(PersistableResource pr) throws IOException {
	Digest digest = digestFactory.getDefaultDigest(pr.getBytes());
	if (LOG.isLoggable(Level.FINER))
	    LOG.log(Level.FINER, "Adding "+pr.getBytes().length+" bytes to " + digest +" in memory CAS");
	map.put(digest, pr.getBytes());
	return digest;
    }

}
