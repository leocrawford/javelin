package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.crypticbit.javelin.neo4j.types.Parameters;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

public class SimpleFdoAdapter implements FundementalDatabaseOperations {

    private GraphDatabaseService graphDb;
    private FundementalDatabaseOperations fdo;

    public SimpleFdoAdapter(GraphDatabaseService graphDb) {
	this.graphDb = graphDb;
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	Relationship result;
	Transaction tx = graphDb.beginTx();
	try {
	    Node node = graphDb.createNode();
	    Relationship newRelationship = parentNode.createRelationshipTo(node, type);
	    result = createOperation.updateElement(newRelationship, fdo);
	    tx.success();
	} finally {
	    tx.finish();
	}
	return result;
    }

    @Override
    public Relationship update(Relationship relationshipToParent, boolean removeEverything, UpdateOperation o) {
	Transaction tx = graphDb.beginTx();
	Relationship result;
	try {
	    if (removeEverything) {
		removeRelationships(relationshipToParent.getEndNode(), RelationshipTypes.ARRAY, RelationshipTypes.MAP);
		removeProperties(relationshipToParent.getEndNode(), Parameters.DISCARDED);
	    }
	    result = o.updateElement(relationshipToParent, fdo);
	    tx.success();
	} finally {
	    tx.finish();
	}
	return result;
    }

    private void removeProperties(Node node, Parameters.Node[] values) {
	for (Parameters.Node key : values) {
	    node.removeProperty(key.name());
	}

    }

    private void removeRelationships(Node node, RelationshipTypes... types) {
	for (Relationship relationship : node.getRelationships(Direction.OUTGOING, types)) {
	    relationship.delete();
	}
	// FIXME - what do we do at other end? Actually delete (and possibly
	// screw up history, or garbage collect?
    }

    @Override
    public Relationship read(Relationship r) {
	return r;
    }

    @Override
    public void delete(Relationship relationshipToNodeToDelete) {
	Transaction tx = graphDb.beginTx();
	try {
	    Node nodeAtOtherEnd = relationshipToNodeToDelete.getEndNode();
	    relationshipToNodeToDelete.delete();
	    nodeAtOtherEnd.delete();
	    tx.success();
	} finally {
	    tx.finish();
	}
    }

    @Override
    public void setTopFdo(FundementalDatabaseOperations fdo) {
	this.fdo = fdo;

    }
}
