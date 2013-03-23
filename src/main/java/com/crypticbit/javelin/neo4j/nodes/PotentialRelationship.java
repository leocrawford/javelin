package com.crypticbit.javelin.neo4j.nodes;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;

public interface PotentialRelationship {
    Relationship create(UpdateOperation updateOperation);

}