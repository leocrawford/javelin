package com.crypticbit.javelin.store.cas;

import com.crypticbit.javelin.store.memory.MemoryCasKas;

/* Create or access any of the standard CAS types (memory, disk, remote, etc) */
public class CasFactory {

    public CasFactory() {
    }

    public ContentAddressableStorage createMemoryCas() {
	return new MemoryCasKas(new DigestFactory());
    }

}
