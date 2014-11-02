package com.crypticbit.javelin.merkle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.Key;

/**
 * This data access object stores a hash of labels, each of which link to an Anchor itself pointing at a commit. In
 * other words we have:
 *
 * <pre>
 * Anchor->Label-(many)->Anchor->Commit
 * </pre>
 *
 * This allows us to retain a unchanging reference to a set of mutable labels, which in turn point at the latest version
 * of mutable commits.
 *
 * @author leo
 */

public class LabelsDao {

    private Map<String, Key> labels = new HashMap<>();

    public void addAnchor(String label, ExtendedAnchor<CommitDao> anchor) {
	labels.put(label, anchor.getSourceAddress());
    }

    public ExtendedAnchor<CommitDao> addCommitAnchor(String label, AddressableStorage store) {
	ExtendedAnchor<CommitDao> result = new ExtendedAnchor<>(store, CommitDao.class);
	labels.put(label, result.getSourceAddress());
	return result;
    }

    public ExtendedAnchor<CommitDao> getCommitAnchor(String name, AddressableStorage store) {
	return new ExtendedAnchor<>(store, labels.get(name), CommitDao.class);
    }

    public Set<String> getLabels() {
	return labels.keySet();
    }

    public boolean hasCommitAnchor(String label) {
	return labels.containsKey(label);
    }

    @Override
    public String toString() {
	return labels.toString();
    }

}
