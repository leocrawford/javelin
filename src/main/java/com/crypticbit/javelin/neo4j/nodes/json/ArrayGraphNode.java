package com.crypticbit.javelin.neo4j.nodes.json;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.JsonPersistenceException;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.PotentialRelationship;
import com.crypticbit.javelin.neo4j.strategies.RelationshipHolder;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.internal.PathToken;

/**
 * This class hold GraphNodes that represent array's. It provides conversions to
 * JsonNode.
 * 
 * @author leo
 * 
 */
public class ArrayGraphNode extends AbstractList<ComplexNode> implements Neo4JJsonType {

    public final class CreateNewArrayElementUpdateOperation extends UpdateOperation {
	private final UpdateOperation createOperation;
	Relationship newR;

	public CreateNewArrayElementUpdateOperation(UpdateOperation createOperation) {
	    this.createOperation = createOperation;
	}

	@Override
	public Relationship updateElement(Relationship relationship, FundementalDatabaseOperations dal) {
	    int nextUnusedIndex = findNextUnusedIndex(relationship.getEndNode());
	    newR = getStrategy().createNewNode(relationship.getEndNode(), RelationshipTypes.ARRAY, createOperation);
	    newR.setProperty(Parameters.Relationship.INDEX.name(), nextUnusedIndex);
	    return relationship;
	}

    }

    private FundementalDatabaseOperations getStrategy() {
	return holder.getStrategy();
    }

    private Node node;
    private ComplexNode children[];
    private ComplexNode holder;

    /**
     * Create a node that represents this graph node
     * 
     * @param incomingRelationship
     */
    public ArrayGraphNode(Node node, ComplexNode complexNode) {
	this.node = node;
	this.holder = complexNode;
    }

    @Override
    public ComplexNode get(int index) {
	updateNodes();
	if (index == children.length)
	    return add();
	else
	    return children[index];

    }

    @Override
    public int size() {
	updateNodes();
	return children.length;
    }

    @Override
    public JsonNode toJsonNode() {
	return new ArrayNode(null, wrapChildrenAsJsonNode()) {
	};
    }

    /**
     * Read the node's relationships to build the children list. This is
     * typically done lazily
     */
    public void updateNodes() {
	if (children == null) {
	    Map<Integer, ComplexNode> map = new TreeMap<Integer, ComplexNode>();
	    for (Relationship r : node.getRelationships(RelationshipTypes.ARRAY, Direction.OUTGOING)) {
		map.put((Integer) r.getProperty(Parameters.Relationship.INDEX.name()), new ComplexNode(
			new RelationshipHolder(r), getStrategy()));
	    }
	    children = map.values().toArray(new ComplexNode[map.size()]);
	}
    }

    /**
     * The children are exposed as a collection of GraphNode's. In order to
     * build a JsonNode, they need to be converted to a collection of
     * JsonNode's.
     * 
     * @return the children collection, exposed as a collection of JsonNode's
     */
    private List<JsonNode> wrapChildrenAsJsonNode() {
	return new AbstractList<JsonNode>() {

	    @Override
	    public JsonNode get(int index) {
		return ArrayGraphNode.this.get(index).getJsonNode().toJsonNode();
	    }

	    @Override
	    public int size() {
		return ArrayGraphNode.this.size();
	    }
	};
    }

    public Node getDatabaseNode() {
	return node;
    }

    @Override
    public ComplexNode put(String key) throws JsonPersistenceException {
	throw new JsonPersistenceException("It's not possible to add a map element to an array node");
    }

    @Override
    public ComplexNode add() {

	return new ComplexNode(new RelationshipHolder(new PotentialRelationship() {

	    @Override
	    public Relationship create(final UpdateOperation createOperation) {
		CreateNewArrayElementUpdateOperation operation = new CreateNewArrayElementUpdateOperation(
			createOperation);
		getStrategy().update(holder.getIncomingRelationship(), false, operation);
		return operation.newR;
	    }
	}), getStrategy());

    }

    // FIXME - should be package protected
    // FIXME - is this in a transaction?
    public static Relationship addElementToArray(FundementalDatabaseOperations dal, Node node, int index, Node newNode) {
	Relationship r = node.createRelationshipTo(newNode, RelationshipTypes.ARRAY);
	r.setProperty(Parameters.Relationship.INDEX.name(), index);
	return r;
    }

    public void removeElementFromArray(final Relationship relationshipToParent, final int index) {
	// this is a delete (on node) and update (on parent)
	getStrategy().update(relationshipToParent, false, new UpdateOperation() {
	    @Override
	    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		    FundementalDatabaseOperations dal) {
		for (Relationship relationshipToNodeToDelete : relationshipToGraphNodeToUpdate.getEndNode()
			.getRelationships(Direction.OUTGOING, RelationshipTypes.ARRAY))
		    if (relationshipToNodeToDelete.getProperty(Parameters.Relationship.INDEX.name()).equals(index))
			dal.delete(relationshipToNodeToDelete);
		return null;
	    }
	});
    }

    private int findNextUnusedIndex(Node parent) {
	int max = 0;
	for (Relationship a : parent.getRelationships(Direction.OUTGOING, RelationshipTypes.ARRAY)) {
	    if ((int) a.getProperty(Parameters.Relationship.INDEX.name()) > max)
		max = (int) a.getProperty(Parameters.Relationship.INDEX.name());
	}
	return max + 1;
    }

    @Override
    public ComplexNode navigate(PathToken token) throws IllegalJsonException {
	if (!token.isArrayIndexToken())
	    throw new IllegalJsonException("Expecting an array element in json path expression: " + token.getFragment());
	return get(token.getArrayIndex());
    }

}