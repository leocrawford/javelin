package com.crypticbit.javelin;

@SuppressWarnings("serial")
public class JsonPersistenceException extends Exception {

    public JsonPersistenceException(String message) {
	super(message);
    }

    public JsonPersistenceException(String message, Throwable cause) {
	super(message, cause);
    }

}
