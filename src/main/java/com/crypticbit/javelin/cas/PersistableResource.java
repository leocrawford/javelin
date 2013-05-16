package com.crypticbit.javelin.cas;

import java.io.InputStream;

public interface PersistableResource {

    public InputStream getInputStream();

    public byte[] getBytes();

}
