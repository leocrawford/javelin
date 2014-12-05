package com.crypticbit.javelin.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.CasKasStore.KeyValueStore;

/**
 * Provides a memory based AddressableStorage.
 */
public class MemoryAddressableStorage implements AddressableStorage {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.cas");
    private final Map<Class<?>, Adapter<?>> adapters;
	private CasKasStore store;

    public MemoryAddressableStorage() {
	store = new MemoryAddressableStore();
	adapters = new HashMap<>();
    }
   
    @Override
    public boolean checkCas(Key key) {
	return store.getCas().containsKey(key);
    }

    @Override
    public boolean checkKas(Key key) {
	return store.getKas().containsKey(key);
    }

    @Override
    public <S> S getCas(Key key, Class<S> clazz) throws StoreException {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (!checkCas(key)) {
	    throw new StoreException("The key " + key + " does not exist");
	}

	S result = adapter.fromByteArray(store.getCas().get(key));

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

	S result = adapter.fromByteArray(store.getKas().get(key));

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
    // for (Map.Entry<Key, byte[]> entry : store.getCas().entrySet()) {
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
	return new LinkedList<Key>(store.getCas().keySet());
    }

//    @Override
//    public List<Key> listCas(Key start) {
//	return new LinkedList<Key>(store.getCas().tailMap(start).keySet());
//    }

    @Override
    public <T> void registerAdapter(Adapter<T> adapter, Class<T> clazz) {
	adapters.put(clazz, adapter);

    }

    @Override
    public <S> void store(Key key, S oldValue, S newValue, Class<S> clazz) throws StoreException {
	if (!checkKas(key) || getKas(key, clazz).equals(oldValue)) {
	    store.getKas().put(key, ((Adapter<S>) adapters.get(clazz)).toByteArray(newValue));
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
	store.getCas().put(key, adapter.toByteArray(value));
	return key;
    }

    @Override
    public String toString() {
	return store.getCas().toString();
    }

    private <S> Adapter<S> getAdapter(Class<S> clazz) {
	Adapter<S> adapter = (Adapter<S>) adapters.get(clazz);
	if (adapter == null) {
	    throw new IllegalStateException("There is no adapter for type " + clazz);
	}
	return adapter;
    }

}
