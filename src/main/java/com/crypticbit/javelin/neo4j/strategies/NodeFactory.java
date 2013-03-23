package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.nodes.json.ComplexGraphNode;

public interface NodeFactory<T> {

    public Class<? extends ComplexGraphNode> getInterface();
    public T create(Relationship r);

}
