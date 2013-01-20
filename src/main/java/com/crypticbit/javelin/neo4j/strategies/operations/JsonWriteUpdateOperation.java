package com.crypticbit.javelin.neo4j.strategies.operations;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.nodes.ArrayGraphNode;
import com.crypticbit.javelin.neo4j.nodes.MapGraphNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;
import com.fasterxml.jackson.databind.JsonNode;

public final class JsonWriteUpdateOperation extends UpdateOperation {
    private final JsonNode jsonNode;

    public JsonWriteUpdateOperation(JsonNode jsonNode) {
	this.jsonNode = jsonNode;
    }

    @Override
    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate, FundementalDatabaseOperations dal) {

	Node updateNode = relationshipToGraphNodeToUpdate.getEndNode();
	if (jsonNode.isContainerNode()) {
	    if (jsonNode.isArray()) {
		updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.ARRAY.toString());
		for (int loop = 0; loop < jsonNode.size(); loop++) {
	    
			Relationship newR = dal.createNewNode(relationshipToGraphNodeToUpdate.getEndNode(),
				RelationshipTypes.ARRAY,  new JsonWriteUpdateOperation(jsonNode.get(loop)));
			newR.setProperty(Parameters.Relationship.INDEX.name(),loop);
		}
	    }
	    if (jsonNode.isObject()) {
		updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.MAP.toString());
		Iterator<String> fieldNamesIterator = jsonNode.fieldNames();
		// FIXME - very wrong - copy and paste from Map
		while (fieldNamesIterator.hasNext()) {
		    String f = fieldNamesIterator.next();
		    Relationship newR = dal.createNewNode(
			    relationshipToGraphNodeToUpdate.getEndNode(), RelationshipTypes.MAP,
			    new JsonWriteUpdateOperation(jsonNode.get(f)));
		    newR.setProperty(Parameters.Relationship.KEY.name(), f);
		}
	    }
	} else {
	    updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.VALUE.toString());
	    updateNode.setProperty(Parameters.Node.VALUE.name(), jsonNode.toString());
	}
	return relationshipToGraphNodeToUpdate;
    }
}