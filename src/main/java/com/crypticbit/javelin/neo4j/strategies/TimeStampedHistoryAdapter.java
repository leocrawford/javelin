package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.neo4j.strategies.operations.ReplaceNodeUpdateOperation;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

public class TimeStampedHistoryAdapter extends CompoundFdoAdapter {

    private Relationship currentVersion;

    public TimeStampedHistoryAdapter(GraphDatabaseService graphDb, FundementalDatabaseOperations nextAdapter) {
	super(graphDb, nextAdapter);
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	Transaction tx = getGraphDB().beginTx();
	try {
	    Node node = getGraphDB().createNode();
	    Relationship newRelationship = parentNode.createRelationshipTo(node, type);
	    Relationship version0 = super.createNewNode(node, RelationshipTypes.VERSION,
		    createOperation.add(getTimestampOperation()));

	    tx.success();
	    currentVersion = version0;
	    return newRelationship;
	} finally {
	    tx.finish();
	}


    }

    private UpdateOperation getTimestampOperation() {
	return new UpdateOperation() {
	    @Override
	    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		    FundementalDatabaseOperations dal) {
		relationshipToGraphNodeToUpdate.setProperty("timestamp", System.currentTimeMillis());
		return relationshipToGraphNodeToUpdate;
	    }
	};
    }

    @Override
    public Relationship update(final Relationship relationshipToParent, final boolean removeEverything,
	    final UpdateOperation operation) {
	// final Node nodeToUpdate = relationshipToParent.getEndNode();
	Transaction tx = getGraphDB().beginTx();
	try {
	    Relationship result = super.createNewNode(relationshipToParent.getEndNode(), RelationshipTypes.VERSION,
		    new ReplaceNodeUpdateOperation(read(relationshipToParent,null).getEndNode(), removeEverything,
			    relationshipToParent).add(operation).add(getTimestampOperation()));
	    tx.success();
	    return result;
	} finally {
	    tx.finish();
	}

    }
    
    @Override
    public boolean doesExposeInterface(Class<?> exposesInterface) {
	return exposesInterface == History.class;
    }

    @Override
    public Relationship readNext(Relationship relationshipToNode,  Class<?> desiredInterface) {
	// FIXME - addin current version, but make it work
//		if (currentVersion != null)
//		    return currentVersion;
//		else {
		    Node node = relationshipToNode.getEndNode();

		    Relationship found = null;
		    Long timestamp = null;

		    for (Relationship r : node.getRelationships(Direction.OUTGOING, RelationshipTypes.VERSION)) {
			if (timestamp == null || (Long) r.getProperty("timestamp") > timestamp) {
			    found = r;
			    timestamp = (Long) r.getProperty("timestamp");
			}
		    }
		    return found;
    }

    @Override
    public void delete(Relationship relationshipToNodeToDelete) {
	// FIXME - not implemented
    }



}
