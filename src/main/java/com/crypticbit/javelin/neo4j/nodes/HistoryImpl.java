package com.crypticbit.javelin.neo4j.nodes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.History;
import com.crypticbit.javelin.neo4j.nodes.json.JsonGraphNode;
import com.crypticbit.javelin.neo4j.types.NodeTypes;
import com.crypticbit.javelin.neo4j.types.RelationshipTypes;

public class HistoryImpl {

    private Relationship incomingRelationship;
    private ComplexNode holder;

    public HistoryImpl(ComplexNode holder, Relationship incomingRelationship) {
	this.holder = holder;
	this.incomingRelationship = incomingRelationship;

	System.out.println("Creating history for :" + incomingRelationship.getEndNode());
    }

    private static final String DATE_FORMAT = "H:mm:ss.SSS yy-MM-dd";

    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public List<History> getHistory() {
	List<History> history = new LinkedList<History>();

	for (Relationship r : incomingRelationship.getEndNode().getRelationships(RelationshipTypes.VERSION,
		Direction.OUTGOING)) {

	    // FIXME - they might not be next in line
	    final Relationship readRelationship = r; // holder.getStrategy().read(r,null);
	    // FIXME - factor out code
	    final JsonGraphNode endNode = NodeTypes.wrapAsGraphNode(readRelationship.getEndNode(), holder);
	    history.add(new History() {

		@Override
		public long getTimestamp() {
		    return HistoryImpl.this.getTimestamp(readRelationship);
		}

		public String toString() {
		    return sdf.format(new Date(getTimestamp()));
		}

		@Override
		public JsonGraphNode getVersion() {
		    return endNode;
		}
	    });

	}
	return history;
    }

    private long getTimestamp(Relationship r) {
	return (long) r.getProperty("timestamp");
    }

}
