package com.crypticbit.javelin.store;


public interface KeyAddressableStorage extends AddressableStorage {

    /**
     * Store a reference (Digest) to a CAS element. Utilities optimistic locking by demanding that you know the latest
     * "old" value. If you don't an exception is thrown
     */
    void store(Identity id, Identity oldDigest, Identity newDigest) throws StoreException;

}
