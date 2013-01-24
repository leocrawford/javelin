package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.MergeableBlock;
import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.VectorClock;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.internal.PathToken;

public class ValueGraphNode extends ValueNode implements Neo4JGraphNode {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private JsonNode delegate;
    private Node node;
    private GraphNodeImpl virtualSuperclass;

    public ValueGraphNode(Node graphNode, Relationship incomingRelationship, FundementalDatabaseOperations fdo) {
	this.node = graphNode;
	virtualSuperclass = new GraphNodeImpl(this, incomingRelationship, fdo);
	try {
	    if (graphNode.hasProperty(Parameters.Node.VALUE.name())) {
		this.delegate = OBJECT_MAPPER.readTree((String) graphNode.getProperty(Parameters.Node.VALUE
			.name()));
	    } else {
		this.delegate = OBJECT_MAPPER.readTree("null");
	    }
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    @Override
    public String asText() {
	return delegate.asText();
    }

    @Override
    public JsonToken asToken() {
	return delegate.asToken();
    }

    @Override
    public boolean equals(Object o) {
	return delegate.equals(o);
    }

    @Override
    public Neo4JGraphNode navigate(String path) {
	throw new Error("Not possible to navigate from leaf object");
    }

    @Override
    public JsonNode toJsonNode() {
	return delegate;
    }

    @Override
    public String toJsonString() {
	return delegate.toString();
    }

    @Override
    public String toString() {
	return delegate.toString();
    }

    @Override
    public Node getDatabaseNode() {
	return node;
    }

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	((BaseJsonNode) delegate).serialize(jgen, provider);

    }

    // delegate methods

    @Override
    public void write(String values) throws IllegalJsonException, JsonPersistenceException {
	virtualSuperclass.write(values);
    }

    @Override
    public List<History> getHistory() {
	return virtualSuperclass.getHistory();
    }

    @Override
    public long getTimestamp() {
	return virtualSuperclass.getTimestamp();
    }

    @Override
    public Neo4JGraphNode put(String key) throws JsonPersistenceException {
	throw new JsonPersistenceException("It's not possible to add data to a child node. ");
    }

    @Override
    public EmptyGraphNode add() throws JsonPersistenceException {
	throw new JsonPersistenceException("It's not possible to add data to a child node. ");
    }

    @Override
    public FundementalDatabaseOperations getStrategy() {
	return virtualSuperclass.getStrategy();
    }
    
    public Neo4JGraphNode navigate(PathToken token) throws IllegalJsonException {
   	    throw new IllegalJsonException("It's not possible to navigate within a child node: " + token.getFragment());
       }
    
    @Override
    public Relationship getIncomingRelationship() {
	return virtualSuperclass.getIncomingRelationship();
    }

    @Override
    public VectorClock getVectorClock() {
	return virtualSuperclass.getVectorClock();
    }
    
    @Override
    public void merge(MergeableBlock block) throws JsonProcessingException, IOException {
	virtualSuperclass.merge(block);
    }
    
    @Override
    public MergeableBlock getExtract() {
	return virtualSuperclass.getExtract();
    }
}
