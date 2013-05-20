package com.crypticbit.javelin.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.cas.PersistableResource;

public class GeneralPersistableResource implements PersistableResource {

    private byte[] data;

    public GeneralPersistableResource(byte[] data) {
	this.data = data;
    }

    public GeneralPersistableResource(String string) {
	try {
	    this.data = string.getBytes("UTF-8");
	}
	catch (UnsupportedEncodingException e) {
	    throw new Error("UTF-8 is not supported on this platform");
	}
    }
    
    @Override
    public InputStream getInputStream() {
	return new ByteArrayInputStream(getBytes());
    }

    @Override
    public byte[] getBytes() {
	return data;
    }

    public String toString() {
	try {
	    String full = getAsString();
	    if (full.length() > 60)
		return full.substring(0, 60) + "...";
	    else
		return full;
	}
	catch (UnsupportedEncodingException e) {
	    throw new Error("UTF-8 is not supported on this platform");
	}
    }

    @Override
    public String getAsString() throws UnsupportedEncodingException {
	return new String(getBytes(), "UTF-8");
    }

}