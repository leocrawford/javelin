package com.crypticbit.javelin;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.neo4j.Neo4JGraphNode;
import com.crypticbit.javelin.neo4j.Neo4JJsonPersistenceService;
import com.crypticbit.javelin.neo4j.nodes.GraphNodeImpl;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter;
import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;
import com.crypticbit.javelin.neo4j.strategies.operations.JsonWriteUpdateOperation;
import com.crypticbit.javelin.neo4j.strategies.operations.WriteVectorClock;

public class VectorClockTest extends Neo4JTestSupport {

    @Test
    public void testBasicWriteFromRoot() throws IOException,
	    JsonPersistenceException, IllegalJsonException {
	Neo4JJsonPersistenceService ps1 = createNewService("id-1");
	Neo4JJsonPersistenceService ps2 = createNewService("id-2");

	ps1.getRootNode().write(JSON_TEXT);
	ps1.getRootNode().navigate("second").write("[2,3,4]");
	
	
	
	ps1.getRootNode().navigate("second").write("[2,3,4,5]");
	String s1 = ps1.getRootNode().navigate("second").toJsonString();
	Neo4JGraphNode gN1 = (Neo4JGraphNode)ps1.getRootNode()
		.navigate("second");
	VectorClock v1 = ((VectorClockAdapter) gN1.getStrategy())
		.getVectorClock(gN1.getDatabaseNode());
	System.out.println("Writing " + s1 + " with " + v1 + " to ps2");

	ps2.getRootNode().write(JSON_TEXT);
	ps2.getRootNode().navigate("second").write("[5,6]");
	Neo4JGraphNode gN2 = (Neo4JGraphNode) ps2.getRootNode()
		.navigate("second");
	VectorClockAdapter vca2 = ((VectorClockAdapter) gN2.getStrategy());
	vca2.addIncoming(gN2.getIncomingRelationship(),
		new JsonWriteUpdateOperation(MAPPER.readTree(s1))
			.add(new WriteVectorClock(v1)));

	// ps2.startWebServiceAndWait();
	
	GraphNode x = ps2.getRootNode().navigate("second");
	System.out.println("We read: "+x+" with vc "+vca2.getVectorClock(gN2.getDatabaseNode()));
	
	// ps2.startWebServiceAndWait();
	
	// assertEquals(MAPPER.readTree(JSON_TEXT),
	// MAPPER.readTree(ps.getRootNode().toJsonString()));
    }

}
