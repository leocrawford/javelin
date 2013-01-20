package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import com.crypticbit.javelin.neo4j.strategies.operations.ReplaceNodeUpdateOperation;


public class TimeStampedHistoryAdapter extends CompoundFdoAdapter {

    public TimeStampedHistoryAdapter(GraphDatabaseService graphDb,
	    FundementalDatabaseOperations nextAdapter) {
	super(graphDb, nextAdapter);
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	return super.createNewNode(parentNode, type, createOperation
			.add(getTimestampOperation()));
    }

    private UpdateOperation getTimestampOperation() {
	return new UpdateOperation() {
	    @Override
	    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		    FundementalDatabaseOperations dal) {
		relationshipToGraphNodeToUpdate.getEndNode().setProperty("timestamp", System.currentTimeMillis());
		return relationshipToGraphNodeToUpdate;
	    }
	};
    }

    @Override
    public Relationship update(final Relationship relationshipToParent,
	    final boolean removeEverything, final UpdateOperation operation) {
	final Node nodeToUpdate = relationshipToParent.getEndNode();
	return createNewNode(relationshipToParent.getStartNode(), relationshipToParent.getType(), new ReplaceNodeUpdateOperation(nodeToUpdate,
			removeEverything, relationshipToParent).add(operation));

    }

    @Override
    public void delete(Relationship relationshipToNodeToDelete) {
	// FIXME - not implemented
    }

}
