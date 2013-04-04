package com.crypticbit.javelin.neo4j.strategies.operations;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy;
import com.crypticbit.javelin.neo4j.strategies.DatabaseStrategy.UpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.VectorClock;
import com.fasterxml.jackson.core.JsonProcessingException;

public class WriteVectorClock extends UpdateOperation {
    private VectorClock vc;

    public WriteVectorClock(VectorClock vc) {
	this.vc = vc;
    }

    @Override
    public Relationship[] getNewRelationships() {
	return null;
    }

    @Override
    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate, DatabaseStrategy dal) {
	try {
	    relationshipToGraphNodeToUpdate.getEndNode().setProperty("VERSION_CLOCK", vc.serializeToString());
	}
	catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return relationshipToGraphNodeToUpdate;
    }

}