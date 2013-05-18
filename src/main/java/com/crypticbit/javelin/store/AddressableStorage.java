package com.crypticbit.javelin.store;

import java.util.List;

import com.crypticbit.javelin.store.cas.PersistableResource;

public interface AddressableStorage {

    
    /** Check if digest exists, if not return <code>null</code> */
    public boolean check(Identity digest) throws StoreException;

    /** Return the content found described by digest. Will throw a runtime exception if not exists */
    public PersistableResource get(Identity digest) throws StoreException;

    /** List all digests */
    public List<Identity> list();

    /** list every item with a digest >= start */
    public List<Identity> list(Identity id) throws StoreException;
    
}
