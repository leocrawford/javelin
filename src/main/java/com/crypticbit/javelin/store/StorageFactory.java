package com.crypticbit.javelin.store;

import com.crypticbit.javelin.store.cas.DigestFactory;
import com.crypticbit.javelin.store.memory.MemoryCasKas;

/* Create or access any of the standard CAS types (memory, disk, remote, etc) */
public class StorageFactory {

    public StorageFactory() {
    }

    public CasKasStore createMemoryCas() {
	return new MemoryCasKas(new DigestFactory());
    }

}
