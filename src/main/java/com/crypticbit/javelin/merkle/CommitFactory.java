package com.crypticbit.javelin.merkle;

import java.util.Date;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.crypticbit.javelin.convert.JsonElementStoreAdapter;
import com.crypticbit.javelin.convert.ObjectStoreAdapter;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

class CommitFactory {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.merkle");

    private final JsonElementStoreAdapter jsonElementStoreAdapter;
    private final ObjectStoreAdapter objectStoreAdapter;
    private final AddressableStorage store;
    private WeakHashMap<Key, Commit> cache = new WeakHashMap<>();

    CommitFactory(AddressableStorage store) {

	this.store = store;

	jsonElementStoreAdapter = new JsonElementStoreAdapter(store);
	objectStoreAdapter = new ObjectStoreAdapter(store);
    }

    Commit createCommit(ExtendedAnchor<CommitDao> anchor, Key valueIdentity, Key... parents)
	    throws CorruptTreeException {
	// FIXME hardcoded user

	try {
	    return new Commit(this, anchor.getDestinationAddress(), anchor.setDestinationValue(new CommitDao(
		    valueIdentity, new Date(), "auser", parents)));
	}
	catch (StoreException e) {
	    throw new CorruptTreeException("The anchor " + anchor
		    + " was supposed to point at a Commit, but was broken", e);
	}

    }

    // FIXME Shouldn't load if in cache
    Commit getCommit(Key address) throws CorruptTreeException {
	try {
	    return getCommit(address, store.getCas(address, CommitDao.class));
	}
	catch (StoreException e) {
	    throw new CorruptTreeException("A commit was expected at address " + address, e);
	}
    }

    Commit getCommitFromAnchor(ExtendedAnchor<CommitDao> anchor) throws CorruptTreeException {
	try {
	    return getCommit(anchor.getDestinationAddress(), anchor.getDestinationValue());
	}
	catch (JsonSyntaxException | StoreException e) {
	    throw new CorruptTreeException("The anchor " + anchor
		    + " was supposed to point at a Commit, but was broken", e);
	}
    }

    JsonElementStoreAdapter getJsonElementStoreAdapter() {
	return jsonElementStoreAdapter;
    }

    ObjectStoreAdapter getObjectStoreAdapter() {
	return objectStoreAdapter;
    }

    /**
     * Wrap a Commit DAO in a Commit, using a WeakHashMap as a cache.
     */

    private Commit getCommit(Key key, CommitDao dao) {
	Commit result = cache.get(key);
	if (result == null) {
	    result = new Commit(this, key, dao);
	    cache.put(key, result);
	}
	return result;
    }

}
