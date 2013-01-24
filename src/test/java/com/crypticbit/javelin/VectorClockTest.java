package com.crypticbit.javelin;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.neo4j.Neo4JJsonPersistenceService;

public class VectorClockTest extends Neo4JTestSupport {

    @Test
    public void testBasicWriteFromRoot() throws IOException, JsonPersistenceException, IllegalJsonException {
	Neo4JJsonPersistenceService ps1 = createNewService("id-1");
	Neo4JJsonPersistenceService ps2 = createNewService("id-2");

	ps1.getRootNode().write(JSON_TEXT);
	ps1.getRootNode().navigate("second").write("[2,3,4]");
	ps1.getRootNode().navigate("second").write("[2,3,4,5]");
	GraphNode ps1Second = ps1.getRootNode().navigate("second");
	String s1 = ps1Second.toJsonString();
	System.out.println("Writing " + s1 + " with " + ps1Second.getVectorClock() + " to ps2");

	ps2.getRootNode().write(JSON_TEXT);
	ps2.getRootNode().navigate("second").write("[5,6]");
	ps2.getRootNode().navigate("second").merge(s1, ps1Second.getVectorClock());

	GraphNode ps2Second = ps2.getRootNode().navigate("second");
	System.out.println("We read: " + ps2Second + " with vc " + ps2Second.getVectorClock());

    }

}
