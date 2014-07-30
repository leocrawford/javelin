package com.crypticbit.javelin.store;

import java.io.Serializable;

import com.google.gson.JsonElement;

public interface Adapter<T>  {

    public byte[] toByteArray(T element);
    public T fromByteArray(byte[] bytes);
    public Key getContentDigest(T element);


}
