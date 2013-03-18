package com.crypticbit.javelin.neo4j;

import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.nodes.json.Neo4JJsonType;

public interface Neo4JGraphNode extends Neo4JJsonType {

    
    public ComplexNode navigate(String jsonPath) throws IllegalJsonException;
    public String toJsonString();
    public void write(String json) throws IllegalJsonException, JsonPersistenceException;





}
