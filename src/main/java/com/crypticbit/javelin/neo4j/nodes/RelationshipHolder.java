package com.crypticbit.javelin.neo4j.nodes;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;

public class RelationshipHolder {

    private Relationship relationship;
    private PotentialRelationship potentialRelationship;

    public RelationshipHolder(Relationship relationship) {
	this.relationship = relationship;
    }

    public RelationshipHolder(PotentialRelationship potentialRelationship) {
	this.potentialRelationship = potentialRelationship;
    }

    public Relationship getRelationship() {
	return relationship;
    }

    public Relationship createOrUpdateRelationship(UpdateOperation operation, FundementalDatabaseOperations strategy) {
	if (relationship == null)
	    relationship = potentialRelationship.create(operation);
	else
	    relationship = strategy.update(relationship, true, operation);
	return relationship;
    }
    
    public boolean isRealRelationship() {
	return (relationship != null);
    }

}
