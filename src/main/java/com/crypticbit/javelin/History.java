package com.crypticbit.javelin;

import com.crypticbit.javelin.neo4j.Neo4JGraphNode;

public interface History {

    public long getTimestamp();
    public Neo4JGraphNode getVersion();
    
}
