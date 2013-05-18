package com.crypticbit.javelin.store.memory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.crypticbit.javelin.store.cas.Digest;
import com.crypticbit.javelin.store.cas.DigestFactory;
import com.crypticbit.javelin.store.cas.PersistableResource;
import com.crypticbit.javelin.store.kas.KeyAddressableStorage;

/**
 * Provides a memory based CAS. The digest is calculated using a <code>digestFactory</code> which is created at
 * instantiation, and has the same life as this object. You can change the default algorithm used by accessing the
 * factory.
 */
public class MemoryCasKas implements ContentAddressableStorage, KeyAddressableStorage {

    private DigestFactory digestFactory;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");

    private final TreeMap<Identity<?>, byte[]> map = new TreeMap<>();

    public MemoryCasKas(DigestFactory digestFactory) {
	this.digestFactory = digestFactory;
    }

    @Override
    public boolean check(Identity digest) {
	return map.containsKey(digest);
    }

    @Override
    public PersistableResource get(Identity digest) {
	GeneralPersistableResource pr = new GeneralPersistableResource(map.get(digest));
	if (LOG.isLoggable(Level.FINER))
	    LOG.log(Level.FINER, "Read "+pr.getBytes().length+" bytes from " + digest +" using memory CAS");
	return pr;
    }

    @Override
    public DigestFactory getDigestFactory() {
	return digestFactory;
    }

    @Override
    public List<Identity> list() {
	return new LinkedList<Identity>(map.keySet());
    }

    @Override
    public List<Identity> list(Identity start) {
	return new LinkedList<Identity>(map.tailMap(start).keySet());
    }

    @Override
    public Identity store(PersistableResource pr) throws IOException {
	Digest digest = digestFactory.getDefaultDigest(pr.getBytes());
	if (LOG.isLoggable(Level.FINER))
	    LOG.log(Level.FINER, "Adding "+pr.getBytes().length+" bytes to " + digest +" in memory CAS");
	map.put(digest, pr.getBytes());
	return digest;
    }

    @Override
    public void store(Identity id, PersistableResource pr) throws StoreException, IOException {
	map.put(id, pr.getBytes());
    }

}
