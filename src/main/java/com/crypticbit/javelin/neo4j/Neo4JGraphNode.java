package com.crypticbit.javelin.neo4j;

import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.HistoryGraphNode;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.MergeableBlock;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.SimpleFdoAdapter;
import com.crypticbit.javelin.neo4j.strategies.VectorClock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.internal.PathToken;

public interface Neo4JGraphNode extends Neo4JJsonType {

    
    public ComplexNode navigate(String jsonPath) throws IllegalJsonException;
    public String toJsonString();
    public void write(String json) throws IllegalJsonException, JsonPersistenceException;





}
