package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.MergeableBlock;
import com.crypticbit.javelin.neo4j.nodes.json.ComplexGraphNode;
import com.crypticbit.javelin.neo4j.nodes.json.JsonNodeFactory;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.VectorClock;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.operations.WriteVectorClock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.PathToken;

public class ComplexNode implements ComplexGraphNode {

    private RelationshipHolder incomingRelationship;
    private FundementalDatabaseOperations fdo;
    private JsonNodeFactory jsonNodeFactory;

    
    public ComplexNode(RelationshipHolder incomingRelationship, FundementalDatabaseOperations fdo) {
	this.incomingRelationship = incomingRelationship;
	this.fdo = fdo;
	this.jsonNodeFactory = new JsonNodeFactory(this, incomingRelationship);
    }

    @Override
    public ComplexNode add() throws IllegalJsonException, JsonPersistenceException {
	return getJsonNode().add();
    }

    public MergeableBlock getExtract() {
	return null;
	// new MergeableBlock() {
	// private String json = getGraphNode().toJsonNode().toString();
	// private VectorClock vc = getGraphNode().getVectorClock();
	//
	// @Override
	// public VectorClock getVectorClock() {
	// return vc;
	// }
	//
	// @Override
	// public String getJson() {
	// return json;
	// }
	//
	// public String toString() {
	// return json + " (" + vc + ")";
	// }
	// };
    }



    public Relationship getIncomingRelationship() {
	return incomingRelationship.getRelationship();
    }

    public ComplexGraphNode getJsonNode() {
	return jsonNodeFactory;
    }

    public FundementalDatabaseOperations getStrategy() {
	return fdo;
    }



    public VectorClock getVectorClock() {
	// FIXME - what if VC is not at top of stack?
	return null;
	// return ((VectorClockAdapter)
	// getStrategy()).getVectorClock(getGraphNode().getDatabaseNode());
    }

    public void merge(MergeableBlock block) throws JsonProcessingException, IOException {
	// FIXME - what if VC is not at top of stack?
	// FIXME Factor out Object Mapper
	VectorClockAdapter vca2 = ((VectorClockAdapter) getStrategy());
	vca2.addIncoming(getIncomingRelationship(),
		new JsonWriteUpdateOperation(new ObjectMapper().readTree(block.getJson())).add(new WriteVectorClock(
			block.getVectorClock())));

    }

    @Override
    public ComplexGraphNode navigate(PathToken token) throws IllegalJsonException {
	return jsonNodeFactory.navigate(token);
    }

    @Override
    public ComplexGraphNode navigate(String jsonPath) throws IllegalJsonException {
	return getJsonNode().navigate(jsonPath);
    }

    @Override
    public ComplexNode put(String key) throws IllegalJsonException, JsonPersistenceException {
	return getJsonNode().put(key);
    }

    @Override
    public JsonNode toJsonNode() {
	return getJsonNode().toJsonNode();
    }

    @Override
    public String toJsonString() {
	return getJsonNode().toJsonString();
    }

    @Override
    public void write(String json) throws IllegalJsonException, JsonPersistenceException {
	getJsonNode().write(json);
    }

    public List<History> getHistory() {
	return new HistoryImpl(this,getStrategy().read(incomingRelationship.getRelationship(), History.class)).getHistory();
    }

}
