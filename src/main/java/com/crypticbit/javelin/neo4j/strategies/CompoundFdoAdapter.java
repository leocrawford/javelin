package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public abstract class CompoundFdoAdapter implements FundementalDatabaseOperations {

    private FundementalDatabaseOperations nextAdapter;
    private GraphDatabaseService graphDb;
    private FundementalDatabaseOperations fdo;

    public CompoundFdoAdapter(GraphDatabaseService graphDb, FundementalDatabaseOperations nextAdapter) {
	this.nextAdapter = nextAdapter;
	this.graphDb = graphDb;
    }

    protected GraphDatabaseService getGraphDB() {
	return graphDb;
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	return nextAdapter.createNewNode(parentNode, type, createOperation);
    }

    @Override
    public Relationship update(Relationship relationshipToParent, boolean removeEverything, UpdateOperation operation) {
	return nextAdapter.update(relationshipToParent, removeEverything, operation);

    }

    @Override
    public Relationship read(Relationship relationshipToNode) {
	return nextAdapter.read(relationshipToNode);
    }

    @Override
    public void delete(Relationship relationshipToNodeToDelete) {
	nextAdapter.delete(relationshipToNodeToDelete);

    }
    
    public void setTopFdo(FundementalDatabaseOperations fdo) {
	this.fdo = fdo;
	this.nextAdapter.setTopFdo(fdo);
    }

    public FundementalDatabaseOperations getFdo() {
	return fdo;
    }


}
