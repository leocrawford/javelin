package com.crypticbit.javelin.neo4j.strategies.operations;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy;
import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;
import com.crypticbit.javelin.neo4j.types.Parameters;

public final class ReplaceNodeUpdateOperation extends UpdateOperation {
    private final Node oldGraphNode;
    private final boolean removeEverything;
    private final Relationship relationshipToParent;

    public ReplaceNodeUpdateOperation(Node oldGraphNode, boolean removeEverything, Relationship relationshipToParent) {
	this.oldGraphNode = oldGraphNode;
	this.removeEverything = removeEverything;
	this.relationshipToParent = relationshipToParent;
    }

    @Override
    public Relationship[] getNewRelationships() {
	return null;
    }

    @Override
    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate, DatabaseStrategy dal) {

	Node newGraphNode = relationshipToGraphNodeToUpdate.getEndNode();
	if (!removeEverything) {
	    CloneUtils.copyOutgoingRelationships(oldGraphNode, newGraphNode);
	    CloneUtils.copyProperties(oldGraphNode, newGraphNode, Parameters.Node.values());
	}
	else {
	    CloneUtils.copyProperties(oldGraphNode, newGraphNode, Parameters.PRESERVED);
	}
	CloneUtils.copyProperties(relationshipToParent, relationshipToGraphNodeToUpdate);
	return relationshipToGraphNodeToUpdate;
    }

}