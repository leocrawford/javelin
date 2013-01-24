package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.GraphNode;
import com.crypticbit.javelin.History;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.operations.WriteVectorClock;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;

public class GraphNodeImpl implements Neo4JGraphNode {

    private Neo4JGraphNode graphNode;
    private Relationship incomingRelationship;
    private FundementalDatabaseOperations fdo;

    public GraphNodeImpl(Neo4JGraphNode graphNode, Relationship incomingRelationship, FundementalDatabaseOperations fdo) {
	this.graphNode = graphNode;
	this.incomingRelationship = incomingRelationship;
	this.fdo = fdo;
    }

    public Neo4JGraphNode navigate(String path) throws IllegalJsonException {
	PathTokenizer tokens = new PathTokenizer(path);
	Neo4JGraphNode currentNode = graphNode;
	for (PathToken token : tokens) {
	    if (!token.isRootToken())
		currentNode = currentNode.navigate(token);
	}
	return currentNode;
    }

    public void write(final String json) throws IllegalJsonException, JsonPersistenceException {
	try {
	    final JsonNode values = new ObjectMapper().readTree(json);
	    getStrategy().update(incomingRelationship, true, new JsonWriteUpdateOperation(values));
	} catch (JsonProcessingException jpe) {
	    throw new IllegalJsonException("The JSON string was badly formed: " + json, jpe);
	} catch (IOException e) {
	    throw new JsonPersistenceException("IOException whilst writing data to database", e);
	}

    }

    public Relationship getIncomingRelationship() {
	return incomingRelationship;
    }

    public String toJsonString() {
	return graphNode.toJsonNode().toString();
    }

    @Override
    public JsonNode toJsonNode() {
	throw new UnsupportedOperationException("toJsonNode is to be provided locally");
    }

    @Override
    public Node getDatabaseNode() {
	throw new UnsupportedOperationException("getDatabaseNode is to be provided locally");
    }

    private List<History> history = null;

    private static final String DATE_FORMAT = "H:mm:ss.SSS yy-MM-dd";
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public List<History> getHistory() {
	if (history == null) {
	    history = new LinkedList<History>();
	    history.add(new History() {

		@Override
		public long getTimestamp() {
		    return graphNode.getTimestamp();
		}

		public String toString() {
		    return sdf.format(new Date(getTimestamp()));
		}

		@Override
		public GraphNode getVersion() {
		    return graphNode;
		}
	    });
	    for (Relationship r : graphNode.getDatabaseNode().getRelationships(RelationshipTypes.PREVIOUS_VERSION,
		    Direction.OUTGOING)) {

		Relationship readRelationship = getStrategy().read(r);
		final Neo4JGraphNode endNode = NodeTypes.wrapAsGraphNode(readRelationship.getEndNode(),
			readRelationship, getStrategy());
		history.addAll(endNode.getHistory());
	    }
	}
	return history;

    }

    @Override
    public long getTimestamp() {
	return (long) graphNode.getDatabaseNode().getProperty("timestamp");
    }

    @Override
    public Neo4JGraphNode put(String key) throws IllegalJsonException, JsonPersistenceException {
	throw new UnsupportedOperationException("put is to be provided locally");

    }

    @Override
    public EmptyGraphNode add() throws IllegalJsonException, JsonPersistenceException {
	throw new UnsupportedOperationException("add is to be provided locally");

    }

    @Override
    public Neo4JGraphNode navigate(PathToken token) throws IllegalJsonException {
	throw new UnsupportedOperationException("navigate(token) is to be provided locally");
    }

    @Override
    public FundementalDatabaseOperations getStrategy() {
	return fdo;
    }

    @Override
    public VectorClock getVectorClock() {
	// FIXME - what if VC is not at top of stack?
	return ((VectorClockAdapter) getStrategy()).getVectorClock(graphNode.getDatabaseNode());
    }

    @Override
    public void merge(String json, VectorClock vectorClock) throws JsonProcessingException, IOException {
	// FIXME - what if VC is not at top of stack?
	// FIXME Factor out Object Mapper
	VectorClockAdapter vca2 = ((VectorClockAdapter) getStrategy());
	vca2.addIncoming(getIncomingRelationship(),
		new JsonWriteUpdateOperation(new ObjectMapper().readTree(json)).add(new WriteVectorClock(vectorClock)));

    }

}
