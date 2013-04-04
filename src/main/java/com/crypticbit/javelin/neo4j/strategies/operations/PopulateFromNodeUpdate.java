package com.crypticbit.javelin.neo4j.strategies.operations;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy;
import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;
import com.crypticbit.javelin.neo4j.types.Parameters;

public class PopulateFromNodeUpdate extends UpdateOperation {

    private Node nodeToCopyFrom;

    public PopulateFromNodeUpdate(Node nodeToCopyFrom) {
	this.nodeToCopyFrom = nodeToCopyFrom;
    }

    @Override
    public Relationship[] getNewRelationships() {
	return null;
    }

    @Override
    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate, DatabaseStrategy dal) {
	CloneUtils.copyOutgoingRelationships(nodeToCopyFrom, relationshipToGraphNodeToUpdate.getEndNode());
	CloneUtils.copyProperties(nodeToCopyFrom, relationshipToGraphNodeToUpdate.getEndNode(), Parameters.Node
		.values());
	return relationshipToGraphNodeToUpdate;
    }

}
