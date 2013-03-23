package com.crypticbit.javelin;

import com.crypticbit.javelin.neo4j.nodes.json.ComplexGraphNode;

public interface History {

    public long getTimestamp();

    public ComplexGraphNode getVersion();

}
