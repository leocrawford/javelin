package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.nodes.json.ComplexGraphNode;

public interface NodeFactory<T> {

    public T create(Relationship r);

    public Class<? extends ComplexGraphNode> getInterface();

}
