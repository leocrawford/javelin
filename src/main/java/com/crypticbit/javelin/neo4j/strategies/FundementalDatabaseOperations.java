package com.crypticbit.javelin.neo4j.strategies;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations.UpdateOperation;

/**
 * The CRUD Operations a database needs to implement - and which can be
 * intercepted, to change behaviour
 * 
 * @author leo
 * 
 */
public interface FundementalDatabaseOperations {

    /**
     * Create a new node - with no content
     * 
     * @param updateOperation
     * 
     * @return the new node
     */
    public Node createNewNode(UpdateOperation createOperation);

    /**
     * Update a the node at the end of the relationship, by applying the
     * operation (Sort of command pattern).
     * 
     * @param relationshipToParent
     *            the relationship to the node that wants to be changed
     * @param removeEverything
     *            whether to remove all properties and relationships before the
     *            operation. The only ones to be removed are those known by the
     *            strategies implemented, so they should preserve any unknown
     *            properties or relationships
     * @param operation
     * @return
     */
    public void update(Relationship relationshipToParent, boolean removeEverything, UpdateOperation operation);

    /**
     * Read this relationship, and return the node at the other end
     * 
     * @param relationshipToNode
     */

    public Node read(Relationship relationshipToNode);

    /**
     * Delete the relationship and the node at the end of it
     * 
     * @param relationshipToNodeToDelete
     *            the relationship to remove, together with the node at the end
     *            of it
     */
    public void delete(Relationship relationshipToNodeToDelete);

    /**
     * The operation (command pattern) that is to be executed to perform an s
     * update
     * 
     * @author leo
     * 
     */
    public abstract class UpdateOperation {
	public abstract void updateElement(Node graphNode, FundementalDatabaseOperations dal);

	public UpdateOperation add(final UpdateOperation newOperation) {
	    return new UpdateOperation() {

		@Override
		public void updateElement(Node graphNode, FundementalDatabaseOperations dal) {
		    UpdateOperation.this.updateElement(graphNode, dal);
		    newOperation.updateElement(graphNode, dal);

		}

	    };
	}
    }

    public class NullUpdateOperation extends UpdateOperation {
	public static NullUpdateOperation INSTANCE = new NullUpdateOperation();

	@Override
	public void updateElement(Node node, FundementalDatabaseOperations dal) {
	}

    }

    public void setTopFdo(FundementalDatabaseOperations fdo);
}