package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;

import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.Neo4JJsonType;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;

public class JsonGraphNode implements Neo4JGraphNode {

    private Neo4JJsonType graphNode;
    private  ComplexNode complexNode;

    public JsonGraphNode(ComplexNode complexNode) {
	this.complexNode = complexNode;
    }

    public ComplexNode navigate(String path) throws IllegalJsonException {
	PathTokenizer tokens = new PathTokenizer(path);
	ComplexNode currentNode = complexNode;
	for (PathToken token : tokens) {
	    if (!token.isRootToken())
		currentNode = currentNode.navigate(token);
	}
	return currentNode;
    }

    public void write(final String json) throws IllegalJsonException, JsonPersistenceException {
	try {
	    final JsonNode values = new ObjectMapper().readTree(json);
	    complexNode.createOrUpdate(new JsonWriteUpdateOperation(values));
	} catch (JsonProcessingException jpe) {
	    throw new IllegalJsonException("The JSON string was badly formed: " + json, jpe);
	} catch (IOException e) {
	    throw new JsonPersistenceException("IOException whilst writing data to database", e);
	}

    }

    public String toJsonString() {
	if (getGraphNode() != null)
	    return getGraphNode().toJsonNode().toString();
	else
	    return "";
    }

    @Override
    public JsonNode toJsonNode() {
	return getGraphNode().toJsonNode();
    }


    @Override
    public ComplexNode put(String key) throws IllegalJsonException, JsonPersistenceException {
	return getGraphNode().put(key);

    }

    @Override
    public ComplexNode  add() throws IllegalJsonException, JsonPersistenceException {
	return getGraphNode().add();

    }

    @Override
    public ComplexNode navigate(PathToken token) throws IllegalJsonException {
	return getGraphNode().navigate(token);
    }


    private Neo4JJsonType getGraphNode() {
	if(graphNode == null && complexNode.isCreated()) {
	    graphNode = NodeTypes.wrapAsGraphNode(complexNode.read(), complexNode);
	}
	return graphNode;
    }


}
