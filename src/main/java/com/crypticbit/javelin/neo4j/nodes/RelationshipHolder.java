package com.crypticbit.javelin.neo4j.nodes;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy;
import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;

public class RelationshipHolder {

    private Relationship relationship;
    private PotentialRelationship potentialRelationship;

    public RelationshipHolder(PotentialRelationship potentialRelationship) {
	this.potentialRelationship = potentialRelationship;
    }

    public RelationshipHolder(Relationship relationship) {
	this.relationship = relationship;
    }

    public Relationship createOrUpdateRelationship(UpdateOperation operation, DatabaseStrategy strategy) {
	if (relationship == null) {
	    relationship = potentialRelationship.create(operation);
	}
	else {
	    relationship = strategy.update(relationship, true, operation);
	}
	return relationship;
    }

    public Relationship getRelationship() {
	return relationship;
    }

    public boolean isRealRelationship() {
	return (relationship != null);
    }

}
