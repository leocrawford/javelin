package com.crypticbit.javelin.merkle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.convert.JsonStoreAdapterFactory;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.store.AddressableStorage;
import com.crypticbit.javelin.store.JsonAdapter;
import com.crypticbit.javelin.store.Key;
import com.crypticbit.javelin.store.StoreException;
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

// what is immutable? commit? element? anchors?
// multiple anchors?
// FIXME tidy up exceptions
public class MerkleTree {

    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.merkle");

    /** The current head of the Json data structure */
    private ExtendedAnchor<CommitDao> selectedAnchor;
    private ExtendedAnchor<LabelsDao> labelsAnchor;
    private JsonElement element;
    /**
     * The last known head of the actual data structure, set after a read or write operation
     */
    private Commit commit;
    /** The underlying data store */
    private AddressableStorage store;
    private CommitFactory commitFactory;

    private Gson gson = new Gson();

    /**
     * Create a new json data structure with a random anchor, which can be retrieved using <code>getCommitAnchor</code>
     * 
     * @throws TreeMapperException
     * @throws StoreException
     */
    public MerkleTree(AddressableStorage store) throws StoreException {
	setup(store);
	labelsAnchor = new ExtendedAnchor<>(store, LabelsDao.class);
	LabelsDao labels = new LabelsDao();
	selectedAnchor = labels.addCommitAnchor("HEAD", store);
	labelsAnchor.setDestinationValue(labels);

    }
    

    private void setup(AddressableStorage store) {
	this.store = store;
	store.registerAdapter(new JsonAdapter<LabelsDao>(LabelsDao.class), LabelsDao.class);
	store.registerAdapter(new JsonAdapter<CommitDao>(CommitDao.class), CommitDao.class);
	commitFactory = new CommitFactory(store);

    }

    /**
     * Create a new json data structure - by selecting a label from an existing ds by name
     * @throws CorruptTreeException 
     * 
     * @throws Error
     * @throws TreeMapperException
     * @throws StoreException
     * @throws JsonSyntaxException
     */
    MerkleTree(AddressableStorage store, Key labelsAddress, String label) throws JsonSyntaxException, CorruptTreeException, StoreException  {
	setup(store);
	this.labelsAnchor = new ExtendedAnchor<>(store, labelsAddress, LabelsDao.class);
	if (labelsAnchor.getDestinationValue().hasCommitAnchor(label)) {
	    this.selectedAnchor = labelsAnchor.getDestinationValue().getCommitAnchor(label, store);
	}
	else {
	    // FIXME exception handling, and thought about changing to unknown
	    // branch
	    throw new CorruptTreeException("Labels don't exist: " + labelsAnchor.getDestinationValue());
	    // this.selectedAnchor = labels.addCommitAnchor(label);
	}
    }

    /**
     * Create a new branch. Same DS
     */
    private MerkleTree(MerkleTree parent) throws StoreException {
	setup(parent.store);
	this.labelsAnchor = parent.labelsAnchor;
	this.selectedAnchor = new ExtendedAnchor<>(store, parent.selectedAnchor, CommitDao.class);
    }

    /*
     * public DataStructure(CasKasStore store, Identity identity) { this(store); this.selectedAnchor = new
     * Anchor(identity); }
     */

    public MerkleTree branch() throws StoreException {
	return new MerkleTree(this);
    }

    public synchronized MerkleTree checkout() throws CorruptTreeException {
	commit = commitFactory.getCommitFromAnchor(selectedAnchor);
	element = commit.getAsElement();
	if (LOG.isLoggable(Level.FINER)) {
	    LOG.log(Level.FINER, "Reading commit: " + commit);
	}
	return this;
    }

    // FIXME - what about persisting shared labels? Would they be betetr stored
    // as our own mergable data structure?
    /*
     * public DataStructure branch(String newLabel) throws StoreException { return new DataStructure(store, labels,
     * newLabel); }
     */

    public synchronized MerkleTree commit() throws StoreException, CorruptTreeException {
	Key write = commitFactory.getJsonElementStoreAdapter().write(element);
	commit = commitFactory.createCommit(selectedAnchor, write, selectedAnchor.getDestinationAddress());
	return checkout();
    }

    public void exportAll(OutputStream outputStream) throws JsonSyntaxException, StoreException, IOException {

	/*
	 * ObjectOutputStream oos = new ObjectOutputStream(outputStream); Set<Key> tempCas = new HashSet<>(); Set<Key>
	 * tempKas = new HashSet<>(); Map<String, Key> labelToCommitMap = new HashMap<>(); LabelsDao labels =
	 * labelsAnchor.readEndPoint(store); for (String label : labels.getLabels()) {
	 * System.out.println("Processing label " + label); Key commitAddress = labels.getCommitAnchor(label,
	 * jsonFactory).getAddress(); tempKas.add(commitAddress); CommitDao commitDao = labels.getCommitAnchor(label,
	 * jsonFactory).readEndPoint(store); Key destination = labels.getCommitAnchor(label, jsonFactory).read(store);
	 * System.out.println("dest=" + destination + "," + commitDao); labelToCommitMap.put(label, destination); Commit
	 * c = new Commit(commitDao, destination, jsonFactory); DirectedGraph<Commit, DefaultEdge> x =
	 * c.getAsGraphToRoot(); for (Commit v : x.vertexSet()) { tempCas.addAll(v.getAllIdentities());
	 * System.out.println("Commit=" + v.getIdentity2() + "," + v.toString() + "," + store.getCas(v.getIdentity2()));
	 * } } oos.writeObject(labelToCommitMap); tempKas.add(labelsAnchor.getAddress());
	 * tempCas.add(labelsAnchor.getDestination()); System.out.println("cas: " + tempCas); System.out.println("kas: "
	 * + tempKas); System.out.println("all: " + store.list()); Set<Key> missing = new HashSet(store.list());
	 * missing.removeAll(tempCas); missing.removeAll(tempKas); for (Key i : missing) { System.out.println(i + "=" +
	 * store.getCas(i)); } Map<Key, PersistableResource> casRresult = new HashMap<>(); for (Key i : tempCas) {
	 * casRresult.put(i, store.getCas(i)); } oos.writeObject(casRresult); Map<Key, PersistableResource> kasResult =
	 * new HashMap<>(); for (Key i : tempKas) { kasResult.put(i, store.getCas(i)); } oos.writeObject(kasResult);
	 */

    }

    public Commit getCommit() {
	return commit;
    }

    public Key getLabelsAddress() {
	return labelsAnchor.getSourceAddress();
    }

    /*
     * public Identity getCommitAnchor() { return selectedAnchor.getDigest(); }
     */

    public void importAll(InputStream inputStream, MergeType mergeType) throws IOException, ClassNotFoundException,
	    StoreException, JsonSyntaxException {
	/*
	 * ObjectInputStream ois = new ObjectInputStream(inputStream); Map<String, Key> labelToCommitMap = (Map<String,
	 * Key>) ois.readObject(); LabelsDao localLabels = labelsAnchor.readEndPoint(store); // copy all cas elements
	 * Map<Key, PersistableResource> casResult = (Map<Key, PersistableResource>) ois.readObject(); for (Entry<Key,
	 * PersistableResource> x : casResult.entrySet()) { Key idOfValueWrittenToStore = store.store(x.getValue()); if
	 * (!idOfValueWrittenToStore.equals(x.getKey())) { throw new IllegalStateException("The entry " + x.getKey() +
	 * " produced a new key on store to local (" + idOfValueWrittenToStore + ")"); } }
	 */
	/*
	 * // copy all kas elements Map<Identity, PersistableResource> kasResult = (Map<Identity, PersistableResource>)
	 * ois.readObject(); for (Entry<Identity, PersistableResource> x : kasResult.entrySet()) {
	 * System.out.println("Wrote kas: " + x.getKey()); // FIXME - what should I check against? // FIXME
	 * store.get(x.getKey()).getBytes()) very cumbersome if (store.check(x.getKey())) { if (mergeType ==
	 * MergeType.OVERWRITE) { System.out.println("Over-writing:" + x.getKey()); store.store(x.getKey(), new
	 * Digest(store.get(x.getKey()).getBytes()), new Digest(x.getValue() .getBytes())); } else { // throw new
	 * StoreException("KAS Conflict in " + mergeType + " mode. Key = " + x.getKey()); // fFIXME probably wnat to
	 * ignore some of these.. System.out.println("KAS Conflict in " + mergeType + " mode. Key = " + x.getKey() +
	 * "goes to "+store.get(x.getKey())+" but incoming "+new Digest(x.getValue() .getBytes())); } } else
	 * store.store(x.getKey(), null, new Digest(x.getValue().getBytes())); System.out.println("Got :" + new
	 * Digest(store.get(x.getKey()).getBytes())); }
	 */

	// merge labels
	/*
	 * for (String importedLabel : labelToCommitMap.keySet()) { if (localLabels.hasCommitAnchor(importedLabel)) {
	 * ExtendedAnchor<CommitDao> localCommitAnchor = localLabels.getCommitAnchor(importedLabel, jsonFactory); switch
	 * (mergeType) { case OVERWRITE: System.out.println("overwrite label " + importedLabel + " from " +
	 * localLabels); // FIXME does deleting read work? // localCommitAnchor.readEndPoint(store);
	 * localCommitAnchor.write(store, labelToCommitMap.get(importedLabel)); break; case IGNORE_CONFLICT:
	 * System.out.println("Doing nothing with label " + importedLabel + " from " + localLabels); break; case MERGE:
	 * System.out.println("Merging label " + importedLabel + " from " + localLabels);
	 * localCommitAnchor.readEndPoint(store); try { Commit ic = new
	 * Commit(jsonFactory.getSimpleObjectAdapter(CommitDao.class).read( labelToCommitMap.get(importedLabel)),
	 * labelToCommitMap.get(importedLabel), jsonFactory); System.out.println("import destination=" +
	 * labelToCommitMap.get(importedLabel)); System.out.println("local address=" + localCommitAnchor.getAddress());
	 * System.out.println("local destination=" + localCommitAnchor.getDestination()); System.out.println(new
	 * Commit(localCommitAnchor.getEndPoint(), localCommitAnchor .getDestination(),
	 * jsonFactory).getShortestHistory()); System.out.println(ic.getShortestHistory());
	 * localCommitAnchor.writeEndPoint(store, merge(localCommitAnchor, ic,
	 * labelToCommitMap.get(importedLabel)).getDao()); } catch (PatchFailedException e) { // FIXME betetr exception
	 * handling throw new StoreException("Can't merge", e); } break; } } else
	 * System.out.println("Not writing label"); // localLabels.addAnchor(importedLabel, importedCommitAnchor); }
	 */

    }

    public Object getAsObject() {
	return commit.getAsObject();
    }

    public synchronized MerkleTree merge(MerkleTree other) throws PatchFailedException, MergeException,
	    CorruptTreeException, StoreException {
	// FIXME factor out code for creating change set to Commit and make work
	// for multiple labels
	commit = merge(selectedAnchor, commitFactory.getCommitFromAnchor(other.selectedAnchor), other.selectedAnchor
		.getDestinationAddress());
	return checkout();
    }

    public JsonElement read() {
	return element;
    }

    public void createLabel(String label) throws StoreException {
	LabelsDao value = labelsAnchor.getDestinationValue();
	value.addAnchor(label, this.selectedAnchor);
	labelsAnchor.setDestinationValue(value);
    }

    public MerkleTree write(String string) {
	element = gson.fromJson(string, JsonElement.class);
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
	commit = commitFactory.createCommit(selectedAnchor, valueIdentity, selectedAnchor.getDestinationAddress());
	checkout();
    }

    private Commit merge(ExtendedAnchor<CommitDao> commitAnchorToMergeA, Commit commitB, Key addressB)
	    throws PatchFailedException, MergeException, CorruptTreeException, StoreException {
	ThreeWayDiff patch = commitFactory.getCommitFromAnchor(commitAnchorToMergeA).createChangeSet(commitB);
	Key valueIdentity = commitFactory.getObjectStoreAdapter().write(patch.apply());
	return commitFactory.createCommit(selectedAnchor, valueIdentity, commitAnchorToMergeA.getDestinationAddress(),
		addressB);
    }


    public enum MergeType {
	OVERWRITE, IGNORE_CONFLICT, MERGE
    }

}
