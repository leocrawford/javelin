package com.crypticbit.javelin.store;

import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.crypticbit.javelin.store.cas.PersistableResource;
import com.crypticbit.javelin.store.kas.KeyAddressableStorage;

public interface CasKasStore extends ContentAddressableStorage, KeyAddressableStorage{
    
}
