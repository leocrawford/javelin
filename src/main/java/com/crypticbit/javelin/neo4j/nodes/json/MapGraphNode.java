package com.crypticbit.javelin.neo4j.nodes.json;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.internal.PathToken;

/**
 * Wraps a database node as a node that holds Map's
 * 
 */
public class MapGraphNode extends AbstractMap<String, ComplexNode> implements Neo4JJsonType {

    public final class CreateNewMapElementUpdateOperation extends UpdateOperation {
	private final String key;
	private final UpdateOperation createOperation;
	Relationship newR;
	
	public CreateNewMapElementUpdateOperation(String key, UpdateOperation createOperation) {
	    this.key = key;
	    this.createOperation = createOperation;
	}

	@Override
	public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		FundementalDatabaseOperations dal) {
	    
	    newR = getStrategy().createNewNode(
		    relationshipToGraphNodeToUpdate.getEndNode(), RelationshipTypes.MAP,
		    createOperation);
	    newR.setProperty(Parameters.Relationship.KEY.name(), key);
	    return relationshipToGraphNodeToUpdate;
	}
    }

    private Node node;
    private Set<Map.Entry<String, ComplexNode>> children;
    private ComplexNode  holder;

    public MapGraphNode(Node node, ComplexNode complexNode) {
	this.node = node;
	this.holder = complexNode;
    }

    private FundementalDatabaseOperations getStrategy() {
  	return holder.getStrategy();
      }


    @Override
    public Set<java.util.Map.Entry<String, ComplexNode>> entrySet() {
	updateNodes();
	return children;
    }

    @Override
    public JsonNode toJsonNode() {
	return new ObjectNode(null, wrapChildrenAsJsonNode()) {
	};
    }

    /**
     * Loads all the relationships, and packages them up as a Map which backs
     * this class. Typically called lazily
     */
    public void updateNodes() {
	if (children == null) {
	    children = new HashSet<Map.Entry<String, ComplexNode>>();

	    for (Relationship r : node.getRelationships(RelationshipTypes.MAP, Direction.OUTGOING)) {
		children.add(new AbstractMap.SimpleImmutableEntry<String, ComplexNode>((String) r
			.getProperty(Parameters.Relationship.KEY.name()), new ComplexNode(new RelationshipHolder(r), getStrategy())));
	    }
	}
    }

    /** Converts the children to JsonNode's */
    public Map<String, JsonNode> wrapChildrenAsJsonNode() {
	return new AbstractMap<String, JsonNode>() {

	    @Override
	    public Set<Map.Entry<String, JsonNode>> entrySet() {
		return new AbstractSet<Map.Entry<String, JsonNode>>() {

		    @Override
		    public Iterator<Map.Entry<String, JsonNode>> iterator() {
			final Iterator<Map.Entry<String, ComplexNode>> i = MapGraphNode.this.entrySet().iterator();
			return new Iterator<Map.Entry<String, JsonNode>>() {

			    @Override
			    public boolean hasNext() {
				return i.hasNext();
			    }

			    @Override
			    public Map.Entry<String, JsonNode> next() {
				java.util.Map.Entry<String, ComplexNode> temp = i.next();
				return new AbstractMap.SimpleImmutableEntry<String, JsonNode>(temp.getKey(), temp
					.getValue().getJsonNode().toJsonNode());
			    }

			    @Override
			    public void remove() {
				i.remove();

			    }
			};
		    }

		    @Override
		    public int size() {
			return MapGraphNode.this.entrySet().size();
		    }
		};
	    }

	};
    }

    // delegate methods

   

    @Override
    public ComplexNode put(final String key) {
	if (this.containsKey(key))
	    return this.get(key);
	else {
	    return new ComplexNode(new RelationshipHolder(new PotentialRelationship() {

		@Override
		public Relationship create(final UpdateOperation createOperation) {
		    // this is a create, and an update (on the parent)
		    CreateNewMapElementUpdateOperation operation = new CreateNewMapElementUpdateOperation(key, createOperation);
		    getStrategy().update(holder.getIncomingRelationship(), false,
			    operation);
		    return operation.newR;
		}
	    }), getStrategy());
	}
    }

//    public static Relationship addElementToMap(FundementalDatabaseOperations dal, Node node, final String key,
//	    Node newNode) {
//	Relationship r = node.createRelationshipTo(newNode, RelationshipTypes.MAP);
//	r.setProperty(Parameters.Relationship.KEY.name(), key);
//	return r;
//    }

    public void removeElementFromMap(final Relationship relationshipToParent, final String key) {
	// this is a delete (on node) and update (on parent)
	getStrategy().update(relationshipToParent, false, new UpdateOperation() {
	    @Override
	    public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		    FundementalDatabaseOperations dal) {
		for (Relationship relationshipToNodeToDelete : relationshipToGraphNodeToUpdate.getEndNode()
			.getRelationships(Direction.OUTGOING, RelationshipTypes.MAP))
		    if (relationshipToNodeToDelete.getProperty(Parameters.Relationship.KEY.name()).equals(key))
			dal.delete(relationshipToNodeToDelete);
		return null;
	    }
	});
    }

    @Override
    public ComplexNode add() throws JsonPersistenceException {
	throw new JsonPersistenceException("It's not possible to add an array element to a map node. ");
    }


    @Override
    public ComplexNode navigate(PathToken token) throws IllegalJsonException {
	if (token.isArrayIndexToken())
	    throw new IllegalJsonException("Expecting a map element in json path expression: " + token.getFragment());
	return put(token.getFragment());
    }



}