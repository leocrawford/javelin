package com.crypticbit.javelin.neo4j.strategies.operations;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

class CloneUtils {
    
    private CloneUtils() {}
    
    static Relationship cloneRelationshipToNewEndNode(Node newEndNode,
	    Relationship oldRelationship) {
	return cloneRelationship(oldRelationship.getStartNode(), newEndNode,
		oldRelationship);
    }

    static Relationship cloneRelationshipFromNewStartNode(Node newStartNode,
	    Relationship oldRelationship) {
	return cloneRelationship(newStartNode, oldRelationship.getEndNode(),
		oldRelationship);
    }

    static Relationship cloneRelationship(Node newStartNode, Node newEndNode,
	    Relationship oldRelationship) {
	Relationship newRelationship = newStartNode.createRelationshipTo(
		newEndNode, oldRelationship.getType());
	for (String key : oldRelationship.getPropertyKeys())
	    newRelationship.setProperty(key, oldRelationship.getProperty(key));
	return newRelationship;
    }

    static void copyProperties(Node fromNode, Node toNode,
	    com.crypticbit.javelin.neo4j.types.Parameters.Node[] params) {
	for (com.crypticbit.javelin.neo4j.types.Parameters.Node key : params) {
	    if (fromNode.hasProperty(key.name())) {
		toNode.setProperty(key.name(), fromNode.getProperty(key.name()));
	    }
	}

    }
    

    static void copyProperties(Relationship fromRelationship, Relationship toRelationship) {
	for (String p : fromRelationship.getPropertyKeys()) {
		toRelationship.setProperty(p, fromRelationship.getProperty(p));
	}

    }

    static void copyOutgoingRelationships(Node fromNode, Node toNode) {
	for (Relationship rel : fromNode.getRelationships(Direction.OUTGOING)) {
	    if (!rel.isType(RelationshipTypes.VERSION))
		cloneRelationshipFromNewStartNode(toNode, rel);
	}

    }
}
