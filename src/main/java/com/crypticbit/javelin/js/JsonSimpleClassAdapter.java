package com.crypticbit.javelin.js;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.ContentAddressableStorage;
import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class JsonSimpleClassAdapter<T> extends DataAccessInterface<T> {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    private Class<T> clazz;

    JsonSimpleClassAdapter(ContentAddressableStorage cas, Class<T> clazz, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
	this.clazz = clazz;
    }

    @Override
    public T read(Key identity) throws StoreException, JsonSyntaxException {
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Read " + clazz + ": " + identity);
	}
	return getGson().fromJson(cas.get(identity).getAsString(), clazz);
    }

    @Override
    public Key write(T value) throws StoreException {
	if (value == null)
	    throw new Error("Writing null");
	if (LOG.isLoggable(Level.FINEST)) {
	    LOG.log(Level.FINEST, "Write " + clazz + ": " + value + " as " + getGson().toJson(value));
	}
	return cas.store(new GeneralPersistableResource(getGson().toJson(value)));
    }

}
