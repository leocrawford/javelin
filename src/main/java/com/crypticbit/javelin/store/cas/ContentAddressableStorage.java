package com.crypticbit.javelin.store.cas;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;

/**
 * Synchronous interface for the Content Addressable Storage System Methods are based on Camilstore ones described here,
 * http://ngerakines.github.io/camlispp/, though we could have simply implemented Map!
 */
public interface ContentAddressableStorage extends AddressableStorage {

    /** Get the digest factory used to generate digests */
    DigestFactory getDigestFactory();

    String getName();

    /**
     * Store the data against against its digest, and return that digest
     */
    Identity store(PersistableResource po) throws StoreException;
}
