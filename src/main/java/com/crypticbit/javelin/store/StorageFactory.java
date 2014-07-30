package com.crypticbit.javelin.store;

import com.google.gson.JsonElement;

/* Create or access any of the standard CAS types (memory, disk, remote, etc) */
public class StorageFactory {

    public StorageFactory() {
    }

    public AddressableStorage createMemoryCas() {
	AddressableStorage as = new MemoryAddressableStorage();
	as.registerAdapter(new JsonAdapter(), JsonElement.class);
	as.registerAdapter(new KeyAdapter(), Key.class);
	return as;
    }
}
