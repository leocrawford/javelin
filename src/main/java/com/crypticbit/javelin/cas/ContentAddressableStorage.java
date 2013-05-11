package com.crypticbit.javelin.cas;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * Synchronous interface for the Content Addressable Storage System Methods are based on Camilstore ones described here,
 * http://ngerakines.github.io/camlispp/, though we could have simply implemented Map!
 */
public interface ContentAddressableStorage {

    /** Store the data against against its digest, and return that digest 
     * @throws IOException */
    Digest store(InputStream is) throws CasException, IOException;

    /** Check if digest exists, if not return <code>null</code> */
    public boolean check(Digest digest) throws CasException;

    /** Return the content found described by digest. Will throw a runtime exception if not exists */
    public InputStream get(Digest digest) throws CasException;

    /** list every item with a digest >= start */
    public List<Digest> list(Digest start) throws CasException;

    /** List all digests */
    public List<Digest> list();

    /** Get the digest factory used to generate digests */
    DigestFactory getDigestFactory();

  

}