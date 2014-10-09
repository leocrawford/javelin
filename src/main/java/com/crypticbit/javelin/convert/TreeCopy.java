package com.crypticbit.javelin.convert;

import com.crypticbit.javelin.store.Key;


public class TreeCopy<S, R, P, Q> implements TreeMapper<P,S>{

    private TreeCopySource<S,R> source;
    private TreeCopySource<P,Q> dest;

    public TreeCopy(TreeCopySource<S, R> source, TreeCopySource<P,Q> dest) {
	this.source = source;
	this.dest = dest;
    }

    public P read(S element) throws VisitorException {
	return copy(source,dest,element);
    }
    
    
    private static <S,R, P, Q> P copy(TreeCopySource<S, R> source, TreeCopySource<P,Q> dest, S element) throws VisitorException {
	R unpackedElement = source.unpack(element);
	Object sourceTransformed = source.getSourceTransform().apply(unpackedElement);
	Q destTransformed = dest.getDestTransform().apply(sourceTransformed);
	P destPacked = dest.pack(destTransformed);
	return destPacked;
    }

    @Override
    public S write(P value) throws VisitorException {
	return copy(dest,source,value);
    }


}
