package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.MergeableBlock;
import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.RelationshipHolder;
import com.crypticbit.javelin.neo4j.strategies.VectorClock;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.operations.WriteVectorClock;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.PathToken;

public class ComplexNode {

    private RelationshipHolder incomingRelationship;
    private FundementalDatabaseOperations fdo;
    
    public ComplexNode(RelationshipHolder incomingRelationship, FundementalDatabaseOperations fdo) {
	this.incomingRelationship = incomingRelationship;
	this.fdo = fdo;
    }
    
    public Neo4JGraphNode getJsonNode() {
	 return new JsonGraphNode(this);
    }

    public FundementalDatabaseOperations getStrategy() {
	return fdo;
    }

    public Relationship getIncomingRelationship() {
	return incomingRelationship.getRelationship();
    }

    public ComplexNode navigate(PathToken token) {
	try {
	    return getJsonNode().navigate(token);
	} catch (IllegalJsonException e) {
	    // TODO Auto-generated catch block
	    throw new Error();
	}
    }
    

    public Node createOrUpdate(UpdateOperation operation) {
	return incomingRelationship.createOrUpdateRelationship(operation, getStrategy()).getEndNode();
	
    }

    public boolean isCreated() {
	return incomingRelationship.isRealRelationship();
    }

    public Node read() {
	return getStrategy().read(getIncomingRelationship()).getEndNode();
    }
    
    private static final String DATE_FORMAT = "H:mm:ss.SSS yy-MM-dd";
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public List<History> getHistory() {
	List<History> history = new LinkedList<History>();

//	System.out.println("History for: "+getGraphNode().getDatabaseNode().getId());
//	
//	for (Relationship r : getGraphNode().getDatabaseNode().getRelationships(RelationshipTypes.VERSION,
//		Direction.OUTGOING)) {

//	    Relationship readRelationship = getStrategy().read(r);
//	    final Neo4JGraphNode endNode = NodeTypes.wrapAsGraphNode(readRelationship.getEndNode(), r, getStrategy());
//	    history.add(new History() {
//
//		@Override
//		public long getTimestamp() {
//		    return endNode.getTimestamp();
//		}
//
//		public String toString() {
//		    return sdf.format(new Date(getTimestamp()));
//		}
//
//		@Override
//		public GraphNode getVersion() {
//		    return endNode;
//		}
//	    });

//	}
//	return history;
return null;
    }

    private Neo4JGraphNode getGraphNode() {
	// TODO Auto-generated method stub
	throw new Error();
    }

    public long getTimestamp() {
	return (long) getIncomingRelationship().getProperty("timestamp");
    }
    
    public VectorClock getVectorClock() {
	// FIXME - what if VC is not at top of stack?
	return null;
//	return ((VectorClockAdapter) getStrategy()).getVectorClock(getGraphNode().getDatabaseNode());
    }

    public void merge(MergeableBlock block) throws JsonProcessingException, IOException {
	// FIXME - what if VC is not at top of stack?
	// FIXME Factor out Object Mapper
	VectorClockAdapter vca2 = ((VectorClockAdapter) getStrategy());
	vca2.addIncoming(getIncomingRelationship(),
		new JsonWriteUpdateOperation(new ObjectMapper().readTree(block.getJson())).add(new WriteVectorClock(
			block.getVectorClock())));

    }

    public MergeableBlock getExtract() {
	return null; 
//		new MergeableBlock() {
//	    private String json = getGraphNode().toJsonNode().toString();
//	    private VectorClock vc = getGraphNode().getVectorClock();
//
//	    @Override
//	    public VectorClock getVectorClock() {
//		return vc;
//	    }
//
//	    @Override
//	    public String getJson() {
//		return json;
//	    }
//
//	    public String toString() {
//		return json + " (" + vc + ")";
//	    }
//	};
    }

    
    
    
}
