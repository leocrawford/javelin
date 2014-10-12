package com.crypticbit.javelin.convert;

/* Chains together two TreeNodeAdapters to allow conversion between two trees */
public class TreeMapper<S, P> {

    private TreeNodeAdapter<S> source;
    private TreeNodeAdapter<P> dest;

    public TreeMapper(TreeNodeAdapter<S> source, TreeNodeAdapter<P> dest) {
	this.source = source;
	this.dest = dest;
    }

    public P read(S element) throws TreeMapperException {
	return copy(source, dest, element);
    }

    public S write(P value) throws TreeMapperException {
	return copy(dest, source, value);
    }
    
    private static <S, P> P copy(TreeNodeAdapter<S> source, TreeNodeAdapter<P> dest, S element) throws TreeMapperException {
	return dest.write(source.read(element));
    }


}
