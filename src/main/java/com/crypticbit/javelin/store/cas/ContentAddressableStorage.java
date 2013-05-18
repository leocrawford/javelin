package com.crypticbit.javelin.store.cas;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Synchronous interface for the Content Addressable Storage System Methods are based on Camilstore ones described here,
 * http://ngerakines.github.io/camlispp/, though we could have simply implemented Map!
 */
public interface ContentAddressableStorage {

    /** Check if digest exists, if not return <code>null</code> */
    public boolean check(Digest digest) throws CasException;

    /** Return the content found described by digest. Will throw a runtime exception if not exists */
    public PersistableResource get(Digest digest) throws CasException;

    /** List all digests */
    public List<Digest> list();

    /** list every item with a digest >= start */
    public List<Digest> list(Digest start) throws CasException;

    /** Get the digest factory used to generate digests */
    DigestFactory getDigestFactory();

    /**
     * Store the data against against its digest, and return that digest
     * 
     * @throws IOException
     */
    Digest store(PersistableResource po) throws CasException, IOException;

}
