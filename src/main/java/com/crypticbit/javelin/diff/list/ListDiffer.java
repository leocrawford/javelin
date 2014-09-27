package com.crypticbit.javelin.diff.list;

import java.util.List;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.SequenceDiff;

public final class ListDiffer<T> implements DifferFactoryElement {

	@Override
	public SequenceDiff<List<T>, ListDelta> createApplicator() {
		return new ListSequenceDiff<T>();
	}

	@Override
	public boolean supports(Object object) {
		return object instanceof List;
	}

}