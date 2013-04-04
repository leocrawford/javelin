package com.crypticbit.javelin.neo4j.nodes;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;

public interface PotentialRelationship {
    Relationship create(UpdateOperation updateOperation);

}