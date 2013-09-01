package com.crypticbit.javelin.store.memory;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.*;
import com.crypticbit.javelin.store.cas.DigestFactory;
import com.crypticbit.javelin.store.cas.PersistableResource;

/**
 * Provides a memory based CAS. The digest is calculated using a <code>digestFactory</code> which is created at
 * instantiation, and has the same life as this object. You can change the default algorithm used by accessing the
 * factory.
 */
public class MemoryCasKas implements CasKasStore {

    private DigestFactory digestFactory;

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");

    private final TreeMap<Identity, byte[]> casMap = new TreeMap<>();

    public MemoryCasKas(DigestFactory digestFactory) {
	this.digestFactory = digestFactory;
    }

    @Override
    public boolean check(Identity digest) {
	return casMap.containsKey(digest);
    }

    @Override
    public PersistableResource get(Identity digest) {
	GeneralPersistableResource pr = new GeneralPersistableResource(casMap.get(digest));
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + pr.getBytes().length + " bytes from " + digest + " using memory CAS");
	}
	return pr;
    }

    @Override
    public DigestFactory getDigestFactory() {
	return digestFactory;
    }

    @Override
    public List<Identity> list() {
	return new LinkedList<Identity>(casMap.keySet());
    }

    @Override
    public List<Identity> list(Identity start) {
	return new LinkedList<Identity>(casMap.tailMap(start).keySet());
    }

    @Override
    public synchronized void store(Identity id, Identity oldDigest, Identity newDigest) throws StoreException {
	if (!check(id) || new Digest(get(id).getBytes()).equals(oldDigest)) {
	    casMap.put(id, newDigest.getDigestAsByte());
	}
	else {
	    throw new StoreException("Concurrent modification. Expected " + oldDigest + " but got "
		    + new Digest(get(id).getBytes()));
	}
    }

    @Override
    public Identity store(PersistableResource pr) {
	Digest digest = digestFactory.getDefaultDigest(pr.getBytes());
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

	@Override
	public String getName() {
return "In Memory Store";
	}

}
