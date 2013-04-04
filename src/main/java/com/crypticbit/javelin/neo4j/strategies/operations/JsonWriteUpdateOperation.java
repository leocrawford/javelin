package com.crypticbit.javelin.neo4j.strategies.operations;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.nodes.json.ArrayGraphNode.CreateNewArrayElementUpdateOperation;
import com.crypticbit.javelin.neo4j.nodes.json.MapGraphNode.CreateNewMapElementUpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;
import com.fasterxml.jackson.databind.JsonNode;

public final class JsonWriteUpdateOperation extends UpdateOperation {
    private final JsonNode jsonNode;
    private List<Relationship> newRelationships = new LinkedList<>();

    public JsonWriteUpdateOperation(JsonNode jsonNode) {
	this.jsonNode = jsonNode;
    }

    @Override
    public Relationship[] getNewRelationships() {
	return newRelationships.toArray(new Relationship[newRelationships.size()]);
    }

    @Override
    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate, FundementalDatabaseOperations dal) {

	Node updateNode = relationshipToGraphNodeToUpdate.getEndNode();
	if (jsonNode.isContainerNode()) {
	    if (jsonNode.isArray()) {
		updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.ARRAY.toString());
		for (int loop = 0; loop < jsonNode.size(); loop++) {
		    new CreateNewArrayElementUpdateOperation(loop,new JsonWriteUpdateOperation(jsonNode.get(loop))).updateElement(relationshipToGraphNodeToUpdate,dal);
		}
	    }
	    if (jsonNode.isObject()) {
		updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.MAP.toString());
		Iterator<String> fieldNamesIterator = jsonNode.fieldNames();
		while (fieldNamesIterator.hasNext()) {
		    String f = fieldNamesIterator.next();
		    new CreateNewMapElementUpdateOperation(f,new JsonWriteUpdateOperation(jsonNode.get(f))).updateElement(relationshipToGraphNodeToUpdate,dal);
		}
	    }
	}
	else {
	    updateNode.setProperty(Parameters.Node.TYPE.name(), NodeTypes.VALUE.toString());
	    updateNode.setProperty(Parameters.Node.VALUE.name(), jsonNode.toString());
	}
	return relationshipToGraphNodeToUpdate;
    }
}