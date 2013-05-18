package com.crypticbit.javelin.store.kas;

import java.io.IOException;
import java.util.List;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.cas.PersistableResource;

public interface KeyAddressableStorage extends AddressableStorage {



    /**
     * Store the data against against its digest, and return that digest
     * 
     * @throws IOException
     */
    void store(Identity id, PersistableResource po) throws StoreException, IOException;
    
}
