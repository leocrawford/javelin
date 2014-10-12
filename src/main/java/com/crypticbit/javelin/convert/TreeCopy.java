package com.crypticbit.javelin.convert;



public class TreeCopy<S,P> {

    private TreeNodeAdapter<S> source;
    private TreeNodeAdapter<P> dest;

    public TreeCopy(TreeNodeAdapter<S> source, TreeNodeAdapter<P> dest) {
	this.source = source;
	this.dest = dest;
    }

    public P read(S element) throws VisitorException {
	return copy(source,dest,element);
    }
    
    
    private static <S,P> P copy(TreeNodeAdapter<S> source, TreeNodeAdapter<P> dest, S element) throws VisitorException {
	return dest.write(source.read(element));
    }

    public S write(P value) throws VisitorException {
	return copy(dest,source,value);
    }


}
