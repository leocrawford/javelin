package com.crypticbit.javelin.store.cas;

import java.io.InputStream;

public interface PersistableResource {

    public String getAsString();

    public byte[] getBytes();

    public InputStream getInputStream();

}
