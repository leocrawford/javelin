package com.google.gson;

import com.crypticbit.javelin.store.Key;

public interface AddressToJsonElementWrapper {

    public JsonElement unwrap(JsonElement element);

    public JsonElement wrap(Key key);

}
