package com.crypticbit.javelin.js.lazy;

import java.util.AbstractList;
import java.util.List;

import com.crypticbit.javelin.js.BackedElement;

public class LazyJsonArray extends AbstractList<Object> implements BackedElement {

    private List<Reference> backingList;

    public LazyJsonArray(List<Reference> backingList) {
	this.backingList = backingList;
    }

    @Override
    public Object get(int index) {
	return backingList.get(index).getValue();
    }

    public Object set(int index, Object element) {
	return backingList.set(index, new ValueReference(element)).getValue();
    }

    public void add(int index, Object element) {
	backingList.add(index, new ValueReference(element));
    }

    @Override
    public int size() {
	return backingList.size();
    }

    @Override
    public Object getBackedValue() {
	return backingList;
    }

}
