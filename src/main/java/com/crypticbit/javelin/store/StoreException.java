package com.crypticbit.javelin.store;

@SuppressWarnings("serial")
public class StoreException extends Exception {

    public StoreException(String message) {
	super(message);
    }

    public StoreException(String message, Throwable cause) {
	super(message, cause);
    }
}
