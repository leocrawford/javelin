package com.crypticbit.javelin.store.cas;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface PersistableResource {

    public String getAsString() throws UnsupportedEncodingException;

    public byte[] getBytes();

    public InputStream getInputStream();

}
