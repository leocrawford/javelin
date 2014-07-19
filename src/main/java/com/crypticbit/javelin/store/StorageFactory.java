package com.crypticbit.javelin.store;


/* Create or access any of the standard CAS types (memory, disk, remote, etc) */
public class StorageFactory {

    public StorageFactory() {
    }

    public CasKasStore createMemoryCas() {
	return new MemoryCasKas(new DigestFactory());
    }

}
