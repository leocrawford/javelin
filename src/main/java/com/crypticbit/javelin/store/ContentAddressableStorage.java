package com.crypticbit.javelin.store;


/**
 * Synchronous interface for the Content Addressable Storage System Methods are based on Camilstore ones described here,
 * http://ngerakines.github.io/camlispp/, though we could have simply implemented Map!
 */
public interface ContentAddressableStorage extends AddressableStorage {

    /** Get the digest factory used to generate digests */
    KeyFactory getDigestFactory();

    String getName();

    /**
     * Store the data against against its digest, and return that digest
     */
    Key store(PersistableResource po) throws StoreException;
}
