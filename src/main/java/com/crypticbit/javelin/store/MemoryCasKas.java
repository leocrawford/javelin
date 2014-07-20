package com.crypticbit.javelin.store;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.*;

/**
 * Provides a memory based CAS. The digest is calculated using a <code>digestFactory</code> which is created at
 * instantiation, and has the same life as this object. You can change the default algorithm used by accessing the
 * factory.
 */
public class MemoryCasKas implements CasKasStore {

    private KeyFactory digestFactory;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");

    private final TreeMap<Key, byte[]> casMap = new TreeMap<>();

    public MemoryCasKas(KeyFactory digestFactory) {
	this.digestFactory = digestFactory;
    }

    @Override
    public boolean check(Key digest) {
	return casMap.containsKey(digest);
    }

    @Override
    public PersistableResource get(Key digest) {
	if (!check(digest))
	    throw new Error("FIXME: " + digest);
	GeneralPersistableResource pr = new GeneralPersistableResource(casMap.get(digest));
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + pr.getBytes().length + " bytes from " + digest + " using memory CAS");
	}
	return pr;
    }

    @Override
    public KeyFactory getDigestFactory() {
	return digestFactory;
    }

    @Override
    public String getName() {
	return "In Memory Store";
    }

    @Override
    public List<Key> list() {
	return new LinkedList<Key>(casMap.keySet());
    }

    @Override
    public List<Key> list(Key start) {
	return new LinkedList<Key>(casMap.tailMap(start).keySet());
    }

    @Override
    public synchronized void store(Key id, Key oldDigest, Key newDigest) throws StoreException {
	if (!check(id) || new Key(get(id).getBytes()).equals(oldDigest)) {
	    casMap.put(id, newDigest.getDigestAsByte());
	}
	else {
	    throw new StoreException("Concurrent modification. Expected " + oldDigest + " but got "
		    + new Key(get(id).getBytes()));
	}
    }

    @Override
    public Key store(PersistableResource pr) {
	Key digest = digestFactory.getDefaultDigest(pr.getBytes());
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Adding " + pr.getBytes().length + " bytes to " + digest + " in memory CAS");
	}
	casMap.put(digest, pr.getBytes());
	return digest;
    }

    @Override
    public String toString() {
	return casMap.toString();
    }

}
