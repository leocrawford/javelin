package com.crypticbit.javelin.cas;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ByteBasedPersistableResource implements PersistableResource {

    private byte[] resource;

    public ByteBasedPersistableResource(byte[] resource) {
	this.resource = resource;
    }

    public ByteBasedPersistableResource(String string) {
	try {
	    this.resource = string.getBytes("UTF-8");
	}
	catch (UnsupportedEncodingException e) {
	    throw new Error("UTF-8 is not supported on this platform");
	}
    }

    @Override
    public InputStream getInputStream() {
	return new ByteArrayInputStream(resource);
    }

    @Override
    public byte[] getBytes() {
	return resource;
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
