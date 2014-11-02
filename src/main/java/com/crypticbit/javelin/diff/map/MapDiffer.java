package com.crypticbit.javelin.diff.map;

import java.util.Map;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.SequenceDiff;

public class MapDiffer<T> implements DifferFactoryElement {

    @Override
    public SequenceDiff<Map<String, T>, MapDelta> createApplicator() {
	return new MapSequenceDiff<T>();
    }

    @Override
    public boolean supports(Object object) {
	return object instanceof Map;
    }

}
