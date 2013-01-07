package com.crypticbit.javelin;

import java.util.List;

import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.nodes.EmptyGraphNode;
import com.fasterxml.jackson.databind.JsonNode;

public interface GraphNode  {

    public GraphNode navigate(String jsonPath) throws IllegalJsonException;
    public void write(String json) throws IllegalJsonException, JsonPersistenceException;
    public Neo4JGraphNode put(String key) throws IllegalJsonException, JsonPersistenceException;
    public EmptyGraphNode add() throws IllegalJsonException, JsonPersistenceException;
    public JsonNode toJsonNode();
    public String toJsonString();
    public List<History> getHistory();
    public long getTimestamp();
    
    
}