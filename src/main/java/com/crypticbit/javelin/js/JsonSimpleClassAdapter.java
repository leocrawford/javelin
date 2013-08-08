package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.store.GeneralPersistableResource;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonSyntaxException;

public class JsonSimpleClassAdapter<T> extends DataAccessInterface<T> {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    private Class<T> clazz;

    JsonSimpleClassAdapter(ContentAddressableStorage cas, Class<T> clazz, JsonStoreAdapterFactory jsa) {
	super(cas, jsa);
	this.clazz = clazz;
    }

    @Override
    public T read(Identity identity) throws StoreException, JsonSyntaxException, UnsupportedEncodingException {
	if (LOG.isLoggable(Level.FINEST))
	    LOG.log(Level.FINEST, "Read " + clazz + ": " + identity);
	return getGson().fromJson(cas.get(identity).getAsString(), clazz);
    }

    @Override
    public Identity write(T value) throws StoreException, IOException {
	if (LOG.isLoggable(Level.FINEST))
	    LOG.log(Level.FINEST, "Write " + clazz + ": " + value+" as "+getGson().toJson(value));
	return cas.store(new GeneralPersistableResource(getGson().toJson(value)));
    }

}
