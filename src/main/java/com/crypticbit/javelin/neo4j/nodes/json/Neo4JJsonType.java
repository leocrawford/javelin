package com.crypticbit.javelin.neo4j.nodes.json;

import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.internal.PathToken;

public interface Neo4JJsonType {
    public ComplexNode navigate(PathToken token) throws IllegalJsonException;   
    public ComplexNode put(String key) throws IllegalJsonException, JsonPersistenceException;
    public ComplexNode add() throws IllegalJsonException, JsonPersistenceException;
    public JsonNode toJsonNode();
}
