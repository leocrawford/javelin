package com.crypticbit.javelin.store.kas;

import java.io.IOException;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;

public interface KeyAddressableStorage extends AddressableStorage {

    /**
     * Store a reference (Digest) to a CAS element. Utilities optimistic locking by demanding that you know the latest
     * "old" value. If you don't an exception is thrown
     * 
     * @throws IOException
     */
    void store(Identity id, Identity oldDigest, Identity newDigest) throws StoreException, IOException;

}