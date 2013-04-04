package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public abstract class CompoundFdoAdapter implements DatabaseStrategy {

    private DatabaseStrategy nextAdapter;
    private GraphDatabaseService graphDb;
    private DatabaseStrategy fdo;

    public CompoundFdoAdapter(GraphDatabaseService graphDb, DatabaseStrategy nextAdapter) {
	this.nextAdapter = nextAdapter;
	this.graphDb = graphDb;
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	return nextAdapter.createNewNode(parentNode, type, createOperation);
    }

    @Override
    public void delete(Relationship relationshipToNodeToDelete) {
	nextAdapter.delete(relationshipToNodeToDelete);

    }

    public abstract boolean doesExposeInterface(Class<?> exposesInterface);

    public DatabaseStrategy getFdo() {
	return fdo;
    }

    public DatabaseStrategy getNextAdapter() {
	return nextAdapter;
    }

    @Override
    public Relationship read(Relationship relationshipToNode, Class<?> desiredInterface) {
	if (doesExposeInterface(desiredInterface)) {
	    return relationshipToNode;
	}
	else {
	    return readNext(relationshipToNode, desiredInterface);
	}
    }

    public abstract Relationship readNext(Relationship relationshipToNode, Class<?> desiredInterface);

    @Override
    public void setTopFdo(DatabaseStrategy fdo) {
	this.fdo = fdo;
	this.nextAdapter.setTopFdo(fdo);
    }

    @Override
    public Relationship update(Relationship relationshipToParent, boolean removeEverything, UpdateOperation operation) {
	return nextAdapter.update(relationshipToParent, removeEverything, operation);

    }

    protected GraphDatabaseService getGraphDB() {
	return graphDb;
    }

}
