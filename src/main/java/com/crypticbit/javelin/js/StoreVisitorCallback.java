package com.crypticbit.javelin.js;

import java.util.List;
import java.util.Map;

import com.crypticbit.javelin.store.Identity;
import com.google.common.base.Function;

public interface StoreVisitorCallback<T,F> {

    public T arriveList(List<F> list);
    public T arriveMap(Map<String,F> map);
    public T arriveValue(Object value);
 
       public Function<Identity, F> getTransform();


}
