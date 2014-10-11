package com.crypticbit.javelin.convert;



public class TreeCopy<S,P> implements TreeMapper<P,S>{

    private TreeCopySource<S> source;
    private TreeCopySource<P> dest;

    public TreeCopy(TreeCopySource<S> source, TreeCopySource<P> dest) {
	this.source = source;
	this.dest = dest;
    }

    public P read(S element) throws VisitorException {
	return copy(source,dest,element);
    }
    
    
    private static <S,P> P copy(TreeCopySource<S> source, TreeCopySource<P> dest, S element) throws VisitorException {
	return dest.pack(source.unpack(element));
    }

    @Override
    public S write(P value) throws VisitorException {
	return copy(dest,source,value);
    }


}
