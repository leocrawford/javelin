package com.crypticbit.javelin.convert.lazy;

import java.util.AbstractList;
import java.util.List;

public class LazyArray extends AbstractList<Object> {

	private List<Reference> backingList;

	public LazyArray(List<Reference> backingList) {
		this.backingList = backingList;
	}

	@Override
	public void add(int index, Object element) {
		backingList.add(index, new ValueReference(element));
	}

	@Override
	public Object get(int index) {
		return backingList.get(index).getValue();
	}

	public Object getBackedValue() {
		return backingList;
	}

	@Override
	public Object set(int index, Object element) {
		return backingList.set(index, new ValueReference(element)).getValue();
	}

	@Override
	public int size() {
		return backingList.size();
	}

}
