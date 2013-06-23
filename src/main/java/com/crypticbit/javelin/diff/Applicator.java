package com.crypticbit.javelin.diff;

import java.util.List;

public interface Applicator<T> {
    
    public boolean supports(Object a, Object b);
    public void apply(T value,  List<ListDelta> deltas);
    public ListDelta getDelta(Object parent, Object child, Object branch);
}
