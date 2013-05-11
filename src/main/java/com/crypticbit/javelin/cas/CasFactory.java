package com.crypticbit.javelin.cas;

import com.crypticbit.javelin.cas.memory.MemoryCas;

/* Create or access any of the standard CAS types (memory, disk, remote, etc) */
public class CasFactory {
       
    public CasFactory() {
    }

    public ContentAddressableStorage createMemoryCas() {
	return new MemoryCas(new DigestFactory());
    }

}
