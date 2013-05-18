package com.crypticbit.javelin.store.cas;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface PersistableResource {

    public InputStream getInputStream();

    public byte[] getBytes();

    public String getAsString() throws UnsupportedEncodingException;

}
