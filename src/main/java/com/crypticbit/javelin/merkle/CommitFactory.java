package com.crypticbit.javelin.merkle;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.convert.JsonElementStoreAdapter;
import com.crypticbit.javelin.convert.ObjectStoreAdapter;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;

public class CommitFactory {
    
	private static final Logger LOG = Logger
		.getLogger("com.crypticbit.javelin.merkle");

    private final JsonElementStoreAdapter jsonElementStoreAdapter;
    private final ObjectStoreAdapter objectStoreAdapter;
    private final AddressableStorage store;

    public CommitFactory(AddressableStorage store) {

	this.store = store;

	jsonElementStoreAdapter = new JsonElementStoreAdapter(store);
	objectStoreAdapter = new ObjectStoreAdapter(store);
    }

    public JsonElementStoreAdapter getJsonElementStoreAdapter() {
	return jsonElementStoreAdapter;
    }

    public ObjectStoreAdapter getObjectStoreAdapter() {
	return objectStoreAdapter;
    }

    public Commit getCommit(Key address) throws StoreException {
	return wrap(store.getCas(address, CommitDao.class), address);
    }

    // FIXME - should we try and find an existing instance?
    Commit wrap(CommitDao dao, Key digest) {
	return new Commit(dao, digest, this);
    }

    Commit getCommitFromAnchor(ExtendedAnchor<CommitDao> anchor) throws StoreException {
	return new Commit(anchor.getDestinationValue(), anchor.getDestinationAddress(), this);
    }

    Commit createCommit(ExtendedAnchor<CommitDao> anchor, Key valueIdentity, Key... parents) throws StoreException {
	// FIXME hardcoded user
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Updating id -> " + anchor.getDestinationAddress());
	}
	return new Commit(anchor
		.setDestinationValue(new CommitDao(valueIdentity, new Date(), "auser", parents)), anchor
		.getDestinationAddress(), this);

    }

}
