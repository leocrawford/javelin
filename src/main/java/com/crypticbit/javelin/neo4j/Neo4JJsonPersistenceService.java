package com.crypticbit.javelin.neo4j;

import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;

import com.crypticbit.javelin.JsonPersistenceService;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.nodes.PotentialRelationship;
import com.crypticbit.javelin.neo4j.nodes.RelationshipHolder;
import com.crypticbit.javelin.neo4j.strategies.CompoundFdoAdapter;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.SimpleFdoAdapter;
import com.crypticbit.javelin.neo4j.strategies.TimeStampedHistoryAdapter;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

/**
 * Provides persistence for Json objects (depicted using Jackson JsonNode) with
 * lookup functions using Jackson-JsonPaths.
 * 
 * @author leo
 * 
 */
public class Neo4JJsonPersistenceService implements JsonPersistenceService {

    private static final String ROOT = "ROOT";

    /**
     * Registers a shutdown hook for the Neo4j instance so that it shuts down
     * nicely when the VM exits (even if you "Ctrl-C" the running example before
     * it's completed)
     */
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		graphDb.shutdown();
	    }
	});
    }

    /**
     * The location of the database (this is actually a directory)
     */
    private File file;
    private transient GraphDatabaseService graphDb;
    private transient Node referenceNode;
    private String identity;

    /**
     * Use (or create if not present) the neo4j database at this location
     * 
     * @param file
     *            the location of an existing db, or location to create one
     * @param identity
     *            the unique identity of the machine/user combo (which is used
     *            for VectorClock)
     * */
    public Neo4JJsonPersistenceService(File file, String identity) {
	this.file = file;
	this.identity = identity;
	setup();
    }

    /**
     * Delete the loaded database, and recreate an empty one at the same
     * location
     * 
     * @param iAgreeThisisVeryDangerous
     *            if you don't agree - it won't delete
     */
    public void empty(boolean iAgreeThisisVeryDangerous) {
	if (iAgreeThisisVeryDangerous) {
	    if (graphDb != null) {
		graphDb.shutdown();
	    }
	    file.delete();
	    setup();
	}
    }

    /** Get the root of the graph */
    protected Node getDatabaseNode() {
	return referenceNode;
    }

    /** Do everything that's needed to actually create the database */
    private void setup() {
	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file.getAbsolutePath());
	registerShutdownHook(graphDb);
	referenceNode = graphDb.getReferenceNode();
    }

    // only for server
    private WrappingNeoServerBootstrapper srv;

    public void startWebService() {
	srv = new WrappingNeoServerBootstrapper((AbstractGraphDatabase) graphDb);
	srv.start();
    }

    public void stopWebService() {
	srv.stop();
    }

    public synchronized void startWebServiceAndWait() {
	startWebService();
	try {
	    this.wait();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public ComplexNode getRootNode() {
	final FundementalDatabaseOperations fdo = createDatabase();
	if (getDatabaseNode().hasRelationship(RelationshipTypes.MAP, Direction.OUTGOING)) {
	    Relationship r = getDatabaseNode().getRelationships(RelationshipTypes.MAP, Direction.OUTGOING).iterator()
		    .next();
	    return new ComplexNode(new RelationshipHolder(r), fdo);
	} else {

	    return new ComplexNode(new RelationshipHolder(new PotentialRelationship() {
		@Override
		public Relationship create(UpdateOperation createOperation) {
		    Relationship newR =  fdo.createNewNode(getDatabaseNode(), RelationshipTypes.MAP, createOperation);
		    return newR;
		}
	    }), fdo);
	}
    }

    private FundementalDatabaseOperations createDatabase() {
	CompoundFdoAdapter fdo = new VectorClockAdapter(graphDb, 
		new TimeStampedHistoryAdapter(graphDb,
		new SimpleFdoAdapter(graphDb)), identity);
	fdo.setTopFdo(fdo);
	return fdo;

    }

    public void close() {
	graphDb.shutdown();
    }

}
