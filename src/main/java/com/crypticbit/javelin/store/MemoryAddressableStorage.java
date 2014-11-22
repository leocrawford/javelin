package com.crypticbit.javelin.store;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a memory based AddressableStorage.
 */
public class MemoryAddressableStorage implements AddressableStorage {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");
    private final TreeMap<Key, byte[]> casMap;
    private final TreeMap<Key, byte[]> kasMap;
    private final Map<Class<?>, Adapter<?>> adapters;

    public MemoryAddressableStorage() {
	casMap = new TreeMap<>();
	kasMap = new TreeMap<>();
	adapters = new HashMap<>();
    }
    
    // FIXME do we really want this?
    private MemoryAddressableStorage(TreeMap<Key, byte[]> casMap, TreeMap<Key, byte[]> kasMap, Map<Class<?>, Adapter<?>> adapters) {
	this.casMap = (TreeMap<Key, byte[]>) casMap.clone();
	this.kasMap = (TreeMap<Key, byte[]>) kasMap.clone();
	this.adapters = new HashMap<Class<?>, Adapter<?>>(adapters);
    }
    
    public MemoryAddressableStorage clone() {
	return new MemoryAddressableStorage(casMap,kasMap,adapters);
    }
    
    @Override
    public boolean checkCas(Key key) {
	return casMap.containsKey(key);
    }

    @Override
    public boolean checkKas(Key key) {
	return kasMap.containsKey(key);
    }

    @Override
    public <S> S getCas(Key key, Class<S> clazz) throws StoreException {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (!checkCas(key)) {
	    throw new StoreException("The key " + key + " does not exist");
	}

	S result = adapter.fromByteArray(casMap.get(key));

	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + result + " bytes from cas " + key);
	}
	return result;
    }

    @Override
    public <S> S getKas(Key key, Class<S> clazz) throws StoreException {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (!checkKas(key)) {
	    throw new StoreException("The key " + key + " does not exist");
	}

	S result = adapter.fromByteArray(kasMap.get(key));

	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + result + " bytes from kas " + key);
	}
	return result;
    }

    // public <S> String toString(Class<S> adapterClass) {
    // Adapter<S> adapter = (Adapter<S>) adapters.get(adapterClass);
    //
    // StringBuffer result = new StringBuffer();
    //
    // for (Map.Entry<Key, byte[]> entry : casMap.entrySet()) {
    // result.append(entry.getKey() + "," + adapter.fromByteArray(entry.getValue()) + "\n");
    // }
    // return result.toString();
    // }

    @Override
    public String getName() {
	return "Transient Memory Store";
    }

    @Override
    public List<Key> listCas() {
	return new LinkedList<Key>(casMap.keySet());
    }

    @Override
    public List<Key> listCas(Key start) {
	return new LinkedList<Key>(casMap.tailMap(start).keySet());
    }

    @Override
    public <T> void registerAdapter(Adapter<T> adapter, Class<T> clazz) {
	adapters.put(clazz, adapter);

    }

    @Override
    public <S> void store(Key key, S oldValue, S newValue, Class<S> clazz) throws StoreException {
	if (!checkKas(key) || getKas(key, clazz).equals(oldValue)) {
	    kasMap.put(key, ((Adapter<S>) adapters.get(clazz)).toByteArray(newValue));
	}
	else {
	    throw new StoreException("Concurrent modification. Expected " + oldValue + " but got "
		    + getCas(key, oldValue.getClass()));
	}

    }

    @Override
    public <S> Key store(S value, Class<S> clazz) {
	Adapter<S> adapter = getAdapter(clazz);
	Key key = adapter.getContentDigest(value);
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Adding " + key + " = " + value);
	}
	casMap.put(key, adapter.toByteArray(value));
	return key;
    }

    @Override
    public String toString() {
	return casMap.toString();
    }

    private <S> Adapter<S> getAdapter(Class<S> clazz) {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (adapter == null) {
	    throw new IllegalStateException("There is no adapter for type " + clazz);
	}
	return adapter;
    }

}
