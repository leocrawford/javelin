package com.crypticbit.javelin.js;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.crypticbit.javelin.store.Identity;

public class LabelsDao implements Serializable {

    private Map<String, Identity> labels = new HashMap<>();

    public ExtendedAnchor<CommitDao> getAnchor(String name, JsonStoreAdapterFactory jsonStore) {
	return new ExtendedAnchor<>(labels.get(name), jsonStore, CommitDao.class);
    }

    public ExtendedAnchor<CommitDao> addAnchor(String label, JsonStoreAdapterFactory jsonStore) {
	ExtendedAnchor<CommitDao> result = new ExtendedAnchor<>(jsonStore, CommitDao.class);
	labels.put(label, result.getAddress());
	return result;
    }

    public boolean hasAnchor(String label) {
	return labels.containsKey(label);
    }

    public void addAnchor(String label, ExtendedAnchor<CommitDao> anchor) {
	labels.put(label, anchor.getAddress());

    }

    public String toString() {
	return labels.toString();
    }

}
