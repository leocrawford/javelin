package com.crypticbit.javelin.neo4j;

import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;

import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.nodes.PotentialRelationship;
import com.crypticbit.javelin.neo4j.nodes.RelationshipHolder;
import com.crypticbit.javelin.neo4j.strategies.*;
import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

/**
 * Provides persistence for Json objects (depicted using Jackson JsonNode) with lookup functions using
 * Jackson-JsonPaths.
 * 
 * @author leo
 */
public class Neo4JJsonPersistenceService {

    private static final RelationshipTypes ROOT_RELATIONSHIP_TYPE = RelationshipTypes.ROOT;

    /**
     * The location of the database (this is actually a directory)
     */
    private File file;

    private GraphDatabaseService graphDb;
    private Node referenceNode;
    private String identity;
    // only for server
    private WrappingNeoServerBootstrapper srv;

    /**
     * Use (or create if not present) the neo4j database at this location
     * 
     * @param file
     *            the location of an existing db, or location to create one
     * @param identity
     *            the unique identity of the machine/user combo (which is used for VectorClock)
     */
    public Neo4JJsonPersistenceService(File file, String identity) {
	this.file = file;
	this.identity = identity;
	setup();
    }

    public void close() {
	graphDb.shutdown();
    }

    /**
     * Gets (and if necessary) created the root node for the database. In Json speak this is $.
     * 
     * @return the one and only root node for this database
     */
    public ComplexNode getRootNode() {
	// Neo4J already has a root node, but we want to create another and link it to the existing root node - because
	// this gives us a relationship between the two, which lots of other code expects to exist, and we want to avoid
	// lots of special cases. We therefore offer up our root that has an association back to Neo4J's root.
	final DatabaseStrategy fdo = createDatabaseStrategyChain();
	// have we already created a root
	if (getUnderlyingRootNode().hasRelationship(ROOT_RELATIONSHIP_TYPE, Direction.OUTGOING)) {
	    Relationship r = getUnderlyingRootNode().getRelationships(ROOT_RELATIONSHIP_TYPE, Direction.OUTGOING)
		    .iterator().next();
	    return new ComplexNode(new RelationshipHolder(r), fdo);
	}
	else {
	    return new ComplexNode(new RelationshipHolder(new PotentialRelationship() {
		@Override
		public Relationship create(UpdateOperation createOperation) {
		    Relationship newR = fdo.createNewNode(getUnderlyingRootNode(), ROOT_RELATIONSHIP_TYPE,
			    createOperation);
		    return newR;
		}
	    }), fdo);
	}
    }

    /** Get the root of the graph (which is not the same as the root of the root of the database we expose */
    protected Node getUnderlyingRootNode() {
	return referenceNode;
    }

    /**
     * Create the set of persistence strategies that are invoked in turn (as a chain) in order to access the underlying
     * database.
     * 
     * @return the chain of database strategies required for the root node
     */
    private DatabaseStrategy createDatabaseStrategyChain() {
	CompoundFdoAdapter fdo = new VectorClockAdapter(graphDb, new TimeStampedHistoryAdapter(graphDb,
		new SimpleFdoAdapter(graphDb)), identity);
	fdo.setTopFdo(fdo);
	return fdo;

    }

    /** Do everything that's needed to actually create the database */
    @SuppressWarnings("deprecation")
    // until they actually remove this, it works well for us
    private void setup() {
	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file.getAbsolutePath());
	registerShutdownHook(graphDb);
	referenceNode = graphDb.getReferenceNode();
    }

    /**
     * Registers a shutdown hook for the Neo4j instance so that it shuts down nicely when the VM exits (even if you
     * "Ctrl-C" the running example before it's completed)
     */
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		graphDb.shutdown();
	    }
	});
    }

    public void startWebService() {
	srv = new WrappingNeoServerBootstrapper((AbstractGraphDatabase) graphDb);
	srv.start();
    }

    public synchronized void startWebServiceAndWait() {
	startWebService();
	try {
	    this.wait();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void stopWebService() {
	srv.stop();
    }
}
