package com.crypticbit.javelin.merkle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;

public class LabelsDao implements Serializable {

	private Map<String, Key> labels = new HashMap<>();

	public void addAnchor(String label, ExtendedAnchor<CommitDao> anchor) {
		labels.put(label, anchor.getSourceAddress());

	}

	public ExtendedAnchor<CommitDao> addCommitAnchor(String label,
			AddressableStorage store) {
		ExtendedAnchor<CommitDao> result = new ExtendedAnchor<>(store,
				CommitDao.class);
		labels.put(label, result.getSourceAddress());
		return result;
	}

	public ExtendedAnchor<CommitDao> getCommitAnchor(String name,
			AddressableStorage store) {
		return new ExtendedAnchor<>(store, labels.get(name), CommitDao.class);
	}

	public boolean hasCommitAnchor(String label) {
		return labels.containsKey(label);
	}

	public Set<String> getLabels() {
		return labels.keySet();
	}

	@Override
	public String toString() {
		return labels.toString();
	}

}
