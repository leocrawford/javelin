package com.crypticbit.javelin.store;

import java.util.List;

/**
 * Synchronous interface for key value store
 */

public interface AddressableStorage {

    /** Check if digest exists, if not return <code>null</code> */
    public boolean checkCas(Key key);

    public boolean checkKas(Key key);

    /**
     * Get the value of the resource identified by argument key, and convert it to class of type clazz, assuming an
     * adapter is registered
     */
    public <S> S getCas(Key digest, Class<S> clazz) throws StoreException;

    public <S> S getKas(Key digest, Class<S> clazz) throws StoreException;

    /**
     * Return the name of the instantiation of this interface in a human readable format
     */
    public String getName();

    /** List all keys */
    public List<Key> listCas();;

    /** list every item with a key >= start */
//    public List<Key> listCas(Key key);

    /**
     * Register a new adapter to wrap the convert the stored byte array to a usable class
     */
    public <T> void registerAdapter(Adapter<T> adapter, Class<T> clazz);

    /**
     * Store newValue using argument key as the key. Utilities optimistic locking by demanding that you know the latest
     * "old" value. If you don't an exception is thrown
     */
    public <S> void store(Key key, S oldValue, S newValue, Class<S> clazz) throws StoreException;

    /**
     * Store the data against against its key, and return that key
     */
    public <S> Key store(S po, Class<S> clazz);
}
