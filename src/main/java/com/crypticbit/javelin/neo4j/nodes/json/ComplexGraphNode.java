package com.crypticbit.javelin.neo4j.nodes.json;

import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;

public interface ComplexGraphNode extends JsonGraphNode {

    
    public ComplexGraphNode navigate(String jsonPath) throws IllegalJsonException;
    public String toJsonString();
    public void write(String json) throws IllegalJsonException, JsonPersistenceException;





}
