package com.crypticbit.javelin.store;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a memory based AddressableStorage. 
 */
public class MemoryAddressableStorage implements AddressableStorage {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");
    private final TreeMap<Key, byte[]> backingMap = new TreeMap<>();
    private final Map<Class<?>, Adapter<?>> adapters = new HashMap<>();

    @Override
    public String getName() {
	return "Transient Memory Store";
    }

    @Override
    public List<Key> list() {
	return new LinkedList<Key>(backingMap.keySet());
    }

    @Override
    public List<Key> list(Key start) {
	return new LinkedList<Key>(backingMap.tailMap(start).keySet());
    }

    @Override
    public String toString() {
	return backingMap.toString();
    }

    @Override
    public <T> void registerAdapter(Adapter<T> adapter, Class<T> clazz) {
	adapters.put(clazz, adapter);

    }

    @Override
    public <S> void store(Key key, S oldValue, S newValue, Class<S> clazz) throws StoreException {
	if (!check(key) || get(key, clazz).equals(oldValue)) {
	    backingMap.put(key, ((Adapter<S>) adapters.get(clazz)).toByteArray(newValue));
	}
	else {
	    throw new StoreException("Concurrent modification. Expected " + oldValue + " but got "
		    + get(key, oldValue.getClass()));
	}

    }

    @Override
    public <S> Key store(S value, Class<S> clazz) throws StoreException {
	Adapter<S> adapter = getAdapter(clazz);
	Key key = adapter.getContentDigest(value);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Adding " + key + " = " + value);
	}
	backingMap.put(key, adapter.toByteArray(value));
	return key;
    }

    private <S> Adapter<S> getAdapter(Class<S> clazz) throws StoreException {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if( adapter == null)
	    throw new StoreException("There is no adapter for type "+clazz);
	return adapter;
    }

    @Override
    public <S> S get(Key key, Class<S> clazz) throws StoreException {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (!check(key))
	    throw new StoreException("The key " + key + " does not exist");

	S result = adapter.fromByteArray(backingMap.get(key));

	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + result + " bytes from " + key);
	}
	return result;
    }

    @Override
    public boolean check(Key key) throws StoreException {
	return backingMap.containsKey(key);
    }

}
