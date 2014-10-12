package com.crypticbit.javelin.convert;

/**
 * Tree abstraction that allows us to read or write elements of a tree - usually performed recurisvely.
 * 
 * @author leo
 * @param <S>
 */

public interface TreeNodeAdapter<S> {

    /**
     * Read the object at the given node. Typcially requires conversion of type to Object, and may also require some
     * sort of transformation (e..g reading from a database)
     * 
     * @param node
     * @return
     * @throws TreeMapperException
     */
    public Object read(S node) throws TreeMapperException;

    /**
     * Write the given object, and return a reference to it which could be recovered with read.
     * 
     * @param toWrite
     * @return
     * @throws TreeMapperException
     */

    public S write(Object toWrite) throws TreeMapperException;

}
