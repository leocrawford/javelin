package com.crypticbit.javelin;

import java.io.IOException;
import java.util.List;

import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.nodes.EmptyGraphNode;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface GraphNode {
    
    // FIXME why different to Neo4JGraphNode

    public GraphNode navigate(String jsonPath) throws IllegalJsonException;

    public void write(String json) throws IllegalJsonException, JsonPersistenceException;

    public Neo4JGraphNode put(String key) throws IllegalJsonException, JsonPersistenceException;

    public EmptyGraphNode add() throws IllegalJsonException, JsonPersistenceException;

    public JsonNode toJsonNode();

    public String toJsonString();

    public List<History> getHistory();

    public VectorClock getVectorClock();

    // FIXME Factor out to a serialisable class
    public void merge(String json, VectorClock vectorClock) throws JsonProcessingException, IOException;

    public long getTimestamp();

}