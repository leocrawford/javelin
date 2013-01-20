package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;


public interface PotentialRelationship {

    Relationship create(UpdateOperation updateOperation);

}