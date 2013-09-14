package com.crypticbit.javelin.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.crypticbit.javelin.store.cas.PersistableResource;

public class GeneralPersistableResource implements PersistableResource, Serializable {

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
    public String getAsString() {
	try {
	    return new String(getBytes(), "UTF-8");
	}
	catch (UnsupportedEncodingException e) {
	    throw new Error("UTF-8 is not supported on this platform");
	}
    }

    @Override
    public byte[] getBytes() {
	return data;
    }

    @Override
    public InputStream getInputStream() {
	return new ByteArrayInputStream(getBytes());
    }

    @Override
    public String toString() {
	String full = getAsString();
	if (full.length() > 60) {
	    return full.substring(0, 60) + "...";
	}
	else {
	    return full;
	}

    }

}
