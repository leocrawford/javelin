package com.crypticbit.javelin.neo4j.strategies;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.neo4j.graphdb.*;

import com.crypticbit.javelin.neo4j.strategies.operations.PopulateFromNodeUpdate;
import com.crypticbit.javelin.neo4j.strategies.operations.WriteVectorClock;
import com.crypticbit.javelin.neo4j.types.Parameters;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;
import com.fasterxml.jackson.core.JsonProcessingException;

public class VectorClockAdapter extends CompoundFdoAdapter {

    private static final String VERSION_CLOCK = Parameters.Node.VERSION_CLOCK.name();

    private String identity;

    public VectorClockAdapter(GraphDatabaseService graphDb, DatabaseStrategy nextAdapter, String identity) {
	super(graphDb, nextAdapter);
	this.identity = identity;

    }

    public void addIncoming(final Relationship relationshipToNode, UpdateOperation operation) {
	// FIXME - do we update the VectorClock twice?
	super.createNewNode(relationshipToNode.getEndNode(), RelationshipTypes.INCOMING_VERSION, operation);
    }

    @Override
    public Relationship createNewNode(Node parentNode, RelationshipType type, UpdateOperation createOperation) {
	return super.createNewNode(parentNode, type, createOperation.add(new IncrementVectorClock()));
    }

    @Override
    public boolean doesExposeInterface(Class<?> exposesInterface) {
	return false;
    }

    public VectorClock getVectorClock(Node graphNode) {
	if (graphNode.hasProperty(VERSION_CLOCK)) {
	    String vcString = (String) graphNode.getProperty(VERSION_CLOCK);
	    try {
		VectorClock vc = VectorClock.deSerialize(vcString);
		return vc;
	    }
	    catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return new VectorClock();
    }

    @Override
    public Relationship readNext(Relationship relationshipToNode, Class<?> desiredInterface) {
	Node node = relationshipToNode.getEndNode();

	// if no incoming nodes, do nothing
	if (!node.hasRelationship(Direction.OUTGOING, RelationshipTypes.INCOMING_VERSION)) {
	    return getNextAdapter().read(relationshipToNode, desiredInterface);
	}

	Map<VectorClock, Relationship> candidates = new HashMap<>();
	// copy the candidate relationships into the map
	candidates.put(getVectorClock(node), relationshipToNode);
	for (Relationship r : node.getRelationships(Direction.OUTGOING, RelationshipTypes.INCOMING_VERSION)) {
	    Node endNode = r.getEndNode();
	    VectorClock vc = this.getVectorClock(endNode);
	    candidates.put(vc, r);
	}

	Set<VectorClock> toRemove = new HashSet<>();
	// get rid of any that are definitely predecessors of any other elements
	for (Entry<VectorClock, Relationship> c1 : candidates.entrySet()) {
	    if (!toRemove.contains(c1.getKey())) {
		for (Entry<VectorClock, Relationship> c2 : candidates.entrySet()) {
		    if (!toRemove.contains(c2.getKey())) {
			switch (VectorClock.compare(c1.getKey(), c2.getKey())) {
			case EQUAL:
			    break;
			case GREATER:
			    toRemove.add(c2.getKey());
			    break;
			case SIMULTANEOUS:
			    break;
			case SMALLER:
			    toRemove.add(c1.getKey());
			    break;
			default:
			    break;
			}
		    }

		}
	    }
	}

	System.out.println("Removing " + toRemove);

	for (VectorClock del : toRemove) {
	    candidates.remove(del);
	}

	Entry<VectorClock, Relationship> selected;

	if (candidates.size() == 1) {
	    selected = candidates.entrySet().iterator().next();
	}
	else {
	    Chooser chooser = new UserSelectedChooser();
	    selected = chooser.select(new ArrayList<>(candidates.entrySet()));
	}

	VectorClock acc = null;
	for (VectorClock c : candidates.keySet()) {
	    if (acc == null) {
		acc = c;
	    }
	    else {
		acc = VectorClock.max(acc, c);
	    }

	}
	if (selected.getValue() != relationshipToNode.getEndNode()) {
	    return super.update(relationshipToNode, true, new PopulateFromNodeUpdate(selected.getValue().getEndNode())
		    .add(new WriteVectorClock(acc)));
	}
	else {
	    return super.update(relationshipToNode, true, new WriteVectorClock(acc));
	    // return selected.getValue();
	}
    }

    @Override
    public Relationship update(Relationship relationshipToParent, boolean removeEverything, UpdateOperation operation) {
	return super.update(relationshipToParent, removeEverything, operation.add(new IncrementVectorClock()));
    }

    public class IncrementVectorClock extends UpdateOperation {

	@Override
	public Relationship[] getNewRelationships() {
	    return null;
	}

	@Override
	public Relationship updateElement(Relationship relationshipToGraphNodeToUpdate,
		DatabaseStrategy dal) {
	    VectorClock vc = getVectorClock(relationshipToGraphNodeToUpdate.getEndNode());
	    vc.incrementClock(identity);
	    try {
		relationshipToGraphNodeToUpdate.getEndNode().setProperty(VERSION_CLOCK, vc.serializeToString());
	    }
	    catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return relationshipToGraphNodeToUpdate;
	}

    }

    public enum VectorComparison {
	GREATER, EQUAL, SMALLER, SIMULTANEOUS
    }

}
