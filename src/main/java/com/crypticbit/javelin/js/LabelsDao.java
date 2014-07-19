package com.crypticbit.javelin.js;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.store.Key;

public class LabelsDao implements Serializable {

    private Map<String, Key> labels = new HashMap<>();

    public void addAnchor(String label, ExtendedAnchor<CommitDao> anchor) {
	labels.put(label, anchor.getAddress());

    }

    public ExtendedAnchor<CommitDao> addCommitAnchor(String label, JsonStoreAdapterFactory jsonStore) {
	ExtendedAnchor<CommitDao> result = new ExtendedAnchor<>(jsonStore, CommitDao.class);
	labels.put(label, result.getAddress());
	return result;
    }

    public ExtendedAnchor<CommitDao> getCommitAnchor(String name, JsonStoreAdapterFactory jsonStore) {
	return new ExtendedAnchor<>(labels.get(name), jsonStore, CommitDao.class);
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
