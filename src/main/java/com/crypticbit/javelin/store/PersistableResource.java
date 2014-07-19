package com.crypticbit.javelin.store;

import java.io.InputStream;

public interface PersistableResource {

    public String getAsString();

    public byte[] getBytes();

    public InputStream getInputStream();

}
