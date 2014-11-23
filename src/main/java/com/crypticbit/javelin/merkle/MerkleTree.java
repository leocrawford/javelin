package com.crypticbit.javelin.merkle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.HackedJsonPath;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

import difflib.PatchFailedException;

/**
 * Merkle tree is a tree in which every non-leaf node is labelled with the hash of the labels of its children nodes.
 * Hash trees are useful because they allow efficient and secure verification of the contents of large data
 * structures.(http://en.wikipedia.org/wiki/Merkle_tree).
 * 
 * @author leo
 */

// what is immutable? commit? element? anchors?
// multiple anchors?
// FIXME tidy up exceptions
public class MerkleTree {

    public static final String HEAD = "HEAD";
    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.merkle");
    private static final Gson gson = new Gson();
    /** The current head of the Json data structure */
    private final ExtendedAnchor<CommitDao> selectedAnchor;
    /** The set of all the labels, one of which is the current one */
    private final ExtendedAnchor<LabelsDao> labelsAnchor;
    /** The underlying data store */
    private final AddressableStorage store;
    private final CommitFactory commitFactory;

    /**
     * Create a new merkle tree with a random anchor, and new set of labels containing only HEAD (which initially points
     * at nothing)
     * 
     * @throws TreeMapperException
     * @throws StoreException
     */
    public MerkleTree(AddressableStorage store) throws StoreException {
	this(store, createLabels(touch(store)), HEAD);
    }

    /**
     * Create a merkle tree using a store with an existing set of labels, which are provided by labelsAddress and the
     * current label identified by label
     */
    public MerkleTree(AddressableStorage store, Key labelsAddress, String label) throws JsonSyntaxException,
	    StoreException {
	this(store, new ExtendedAnchor<>(touch(store), labelsAddress, LabelsDao.class), label);
    }

    private MerkleTree(AddressableStorage store, ExtendedAnchor<LabelsDao> labelsAnchor,
	    ExtendedAnchor<CommitDao> selectedAnchor) {
	this.store = touch(store);
	this.commitFactory = new CommitFactory(store);
	this.labelsAnchor = labelsAnchor;
	this.selectedAnchor = selectedAnchor;
    }

    /**
     * Create a new branch.
     */
    private MerkleTree(MerkleTree parent) throws StoreException {
	this(parent.store, parent.labelsAnchor, new ExtendedAnchor<>(parent.store, parent.selectedAnchor,
		CommitDao.class));
    }

    private static ExtendedAnchor<LabelsDao> createLabels(AddressableStorage store) throws StoreException {
	ExtendedAnchor<LabelsDao> labelsAnchor = new ExtendedAnchor<>(store, LabelsDao.class);
	LabelsDao labels = new LabelsDao();
	labels.addCommitAnchor(HEAD, store);
	labelsAnchor.setDestinationValue(labels);
	return labelsAnchor;
    }

    private MerkleTree(AddressableStorage store, ExtendedAnchor<LabelsDao> labelsAnchor, String label)
	    throws JsonSyntaxException, StoreException {
	this(store, labelsAnchor, labelsAnchor.getDestinationValue().getCommitAnchor(label, touch(store)));
    }

    private static AddressableStorage touch(AddressableStorage store) {
	store.registerAdapter(new JsonAdapter<LabelsDao>(LabelsDao.class), LabelsDao.class);
	store.registerAdapter(new JsonAdapter<CommitDao>(CommitDao.class), CommitDao.class);
	return store;
    }

    public MerkleTree branch() throws StoreException {
	return new MerkleTree(this);
    }

    public Commit getCommit() throws CorruptTreeException {
	return commitFactory.getCommitFromAnchor(selectedAnchor);
    }

    public Key getLabelsAddress() {
	return labelsAnchor.getSourceAddress();
    }

    public Object getAsObject() throws CorruptTreeException {
	return getCommit().getAsObject();
    }

    public synchronized MerkleTree merge(MerkleTree other) throws PatchFailedException, MergeException,
	    CorruptTreeException, StoreException {
	merge(selectedAnchor, commitFactory.getCommitFromAnchor(other.selectedAnchor), other.selectedAnchor
		.getDestinationAddress());
	return this;
    }

    public JsonElement read() throws CorruptTreeException {
	return getCommit().getAsElement();

    }

    public void createLabel(String label) throws StoreException {
	LabelsDao value = labelsAnchor.getDestinationValue();
	value.addAnchor(label, this.selectedAnchor);
	labelsAnchor.setDestinationValue(value);
    }

    public MerkleTree write(String string) throws CorruptTreeException, StoreException {
	Key write = commitFactory.getJsonElementStoreAdapter().write(gson.fromJson(string, JsonElement.class));
	commitFactory.createCommit(selectedAnchor, write, selectedAnchor.getDestinationAddress());
	return this;
    }

    public void write(String path, String json) throws StoreException, CorruptTreeException {
	HackedJsonPath compiledPath = new HackedJsonPath(path, new Filter[] {});
	// code copied from jsonpath
	// FIXME reuse JSON
	// FIXME should this be in JCA?
	Object jsonObject = new Gson().fromJson(json, Object.class);
	if (!(jsonObject instanceof Map) && !(jsonObject instanceof List)) {
	    throw new IllegalArgumentException("Invalid container object");
	}
	JsonProvider jsonProvider = JsonProviderFactory.createProvider();
	Object originalResult, result;
	originalResult = result = getAsObject();

	PathTokenizer tokenizer = compiledPath.getTokenizer();
	PathToken lastToken = tokenizer.removeLastPathToken();
	for (PathToken pathToken : tokenizer) {
	    PathTokenFilter filter = pathToken.getFilter();
	    result = filter.filter(result, jsonProvider);
	}

	if (lastToken.isArrayIndexToken()) {
	    ((List) result).set(lastToken.getArrayIndex(), jsonObject);
	}
	else {
	    ((Map) result).put(lastToken.getFragment(), jsonObject);
	}

	Key valueIdentity = commitFactory.getObjectStoreAdapter().write(originalResult);
	// FIXME patterm of commit = create then checkout repeated several times
	commitFactory.createCommit(selectedAnchor, valueIdentity, selectedAnchor.getDestinationAddress());
    }

    private Commit merge(ExtendedAnchor<CommitDao> commitAnchorToMergeA, Commit commitB, Key addressB)
	    throws PatchFailedException, MergeException, CorruptTreeException, StoreException {
	ThreeWayDiff patch = commitFactory.getCommitFromAnchor(commitAnchorToMergeA).createChangeSet(commitB);
	Key valueIdentity = commitFactory.getObjectStoreAdapter().write(patch.apply());
	return commitFactory.createCommit(selectedAnchor, valueIdentity, commitAnchorToMergeA.getDestinationAddress(),
		addressB);
    }

    // public enum MergeType {
    // OVERWRITE, IGNORE_CONFLICT, MERGE
    // }

    public void sync(AddressableStorage remote, String label) throws CorruptTreeException, StoreException,
	    PatchFailedException, MergeException {

	ExtendedAnchor<CommitDao> anchor = labelsAnchor.getDestinationValue().getCommitAnchor(label, store);
	anchor.getDestinationValue();

	// use a StoreAggregator which allows us to access both stores as if they were one. Everything we access which
	// is only available in the remote store will be copied to teh local store.
	StoreAggregator aggregateStore = new StoreAggregator(store, remote);
	CommitFactory tempFactory = new CommitFactory(aggregateStore);

	ExtendedAnchor<CommitDao> remoteAnchor = new ExtendedAnchor<CommitDao>(remote, anchor.getSourceAddress(),
		CommitDao.class);

	// FIXME force load
	aggregateStore.getCas(remoteAnchor.getDestinationAddress(), CommitDao.class);
	Commit c = tempFactory.getCommitFromAnchor(remoteAnchor);
	c.recusivelyLoad();

	System.out.println("\n***\n" + c + "," + this.getCommit());
	System.out.println("1)" + c.getAsGraphToRoot());
	if (c.getAsGraphToRoot().containsVertex(this.getCommit())) {
	    LOG.log(Level.INFO, "Updating " + label + " to newer version from remote");
	    anchor.setDestinationAddress(remoteAnchor.getDestinationAddress());
	}
	else if (this.getCommit().getAsGraphToRoot().containsVertex(c))
	    LOG.log(Level.INFO, "Ignoring incoming from remote " + label + " as already in stream");
	else {
	    LOG.log(Level.INFO, "Merging incoming from remote " + label);
	    merge(selectedAnchor, c, remoteAnchor.getDestinationAddress());
	}

    }

    AddressableStorage getStore() {
	return store;
    }

}
