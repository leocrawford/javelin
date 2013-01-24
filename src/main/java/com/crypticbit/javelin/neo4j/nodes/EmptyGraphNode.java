package com.crypticbit.javelin.neo4j.nodes;

import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import scala.actors.threadpool.Arrays;

import com.crypticbit.javelin.GraphNode;
import com.crypticbit.javelin.History;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.PotentialRelationship;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.PathToken;

/**
 * This class hold GraphNodes that do not yet exist in the database.
 * 
 * @author leo
 * 
 */
public class EmptyGraphNode implements Neo4JGraphNode {

    private PotentialRelationship potentialRelationship;
    private Neo4JGraphNode node;
    private FundementalDatabaseOperations fdo;

    /**
     * Create a node that represents this graph node
     * 
     * @param incomingRelationship
     */
    public EmptyGraphNode(PotentialRelationship potentialRelationship,
	    FundementalDatabaseOperations fdo) {
	this.potentialRelationship = potentialRelationship;
	this.fdo = fdo;
    }

    @Override
    public List<History> getHistory() {
	return Arrays.asList(new History[] {});
    }

    @Override
    public long getTimestamp() {
	checkHaveDelegateNode();
	return node.getTimestamp();
    }

    private void checkHaveDelegateNode() {
	if (node == null)
	    throw new UnsupportedOperationException(
		    "Not possible to invoke this method on an EmptyGraphNode");
    }

    @Override
    public GraphNode navigate(String jsonPath) throws IllegalJsonException {
	checkHaveDelegateNode();
	return node.navigate(jsonPath);
    }

    @Override
    public void write(String json) throws IllegalJsonException,
	    JsonPersistenceException {
	if (node == null) {
	    try {
		final JsonNode values = new ObjectMapper().readTree(json);
		Relationship r = potentialRelationship
			.create(new JsonWriteUpdateOperation(values));
		node = NodeTypes.wrapAsGraphNode(r.getEndNode(), r,
			getStrategy());
	    } catch (JsonProcessingException jpe) {
		throw new IllegalJsonException(
			"The JSON string was badly formed: " + json, jpe);
	    } catch (IOException e) {
		throw new JsonPersistenceException(
			"IOException whilst writing data to database", e);
	    }
	} else
	    node.write(json);

    }

    private void makeRelationsipTangibleIfNotAlready(final NodeTypes nodeType) {
	if (node == null) {
	    Relationship r = potentialRelationship
		    .create(new UpdateOperation() {
			@Override
			public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
				FundementalDatabaseOperations dal) {
			    relationshipToGraphNodeToUpdate.getEndNode().setProperty(
				    Parameters.Node.TYPE.name(),
				    nodeType.name());
			    return relationshipToGraphNodeToUpdate;
			}
		    });
	    node = NodeTypes.wrapAsGraphNode(r.getEndNode(), r, getStrategy());

	}
	checkHaveDelegateNode();
    }

    @Override
    public Neo4JGraphNode put(String key) throws IllegalJsonException,
	    JsonPersistenceException {
	makeRelationsipTangibleIfNotAlready(NodeTypes.MAP);
	return node.put(key);

    }

    @Override
    public EmptyGraphNode add() throws IllegalJsonException,
	    JsonPersistenceException {
	makeRelationsipTangibleIfNotAlready(NodeTypes.ARRAY);
	return node.add();

    }

    @Override
    public JsonNode toJsonNode() {
	checkHaveDelegateNode();
	return node.toJsonNode();
    }

    @Override
    public String toJsonString() {
	checkHaveDelegateNode();
	return node.toJsonString();
    }

    @Override
    public Node getDatabaseNode() {
	checkHaveDelegateNode();
	return node.getDatabaseNode();
    }

    @Override
    public FundementalDatabaseOperations getStrategy() {
	return fdo;
    }

    @Override
    public Neo4JGraphNode navigate(PathToken token) throws IllegalJsonException {
	// checkHaveDelegateNode();
	makeRelationsipTangibleIfNotAlready(token.isArrayIndexToken() ? NodeTypes.ARRAY
		: NodeTypes.MAP);
	return node.navigate(token);
    }

    @Override
    public Relationship getIncomingRelationship() {
	checkHaveDelegateNode();
	return node.getIncomingRelationship();
    }
    
    @Override
    public VectorClock getVectorClock() {
	checkHaveDelegateNode();
	return node.getVectorClock();
    }

    @Override
    public void merge(String json, VectorClock vectorClock) throws JsonProcessingException, IOException {
	checkHaveDelegateNode();
	node.merge(json, vectorClock);
    }
}