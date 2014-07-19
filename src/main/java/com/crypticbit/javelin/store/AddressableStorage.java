package com.crypticbit.javelin.store;

import java.util.List;

public interface AddressableStorage {

    /** Check if digest exists, if not return <code>null</code> */
    public boolean check(Key digest) throws StoreException;

    /** Return the content found described by digest. Will throw a runtime exception if not exists */
    public PersistableResource get(Key digest) throws StoreException;

    /** List all digests */
    public List<Key> list();

    /** list every item with a digest >= start */
    public List<Key> list(Key id) throws StoreException;

}
