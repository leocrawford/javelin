package com.crypticbit.javelin;

import java.io.IOException;

import org.junit.Test;

public class VectorClockTest extends Neo4JTestSupport {

    @Test
    public void testBasicWriteFromRoot() throws IOException, JsonPersistenceException, IllegalJsonException {
	// Neo4JJsonPersistenceService ps1 = createNewService("id-1");
	// Neo4JJsonPersistenceService ps2 = createNewService("id-2");
	//
	// ps1.getRootNode().write(JSON_TEXT);
	// ps1.getRootNode().navigate("second").write("[2,3,4]");
	// ps1.getRootNode().navigate("second").write("[2,3,4,5]");
	// MergeableBlock extractFrom1to2 = ps1.getRootNode().navigate("second").getExtract();
	// System.out.println("Copying from 1 to 2: "+extractFrom1to2);
	//
	// ps2.getRootNode().write(JSON_TEXT);
	// ps2.getRootNode().navigate("second").write("[5,6]");
	// ps2.getRootNode().navigate("second").merge(extractFrom1to2);
	//
	// MergeableBlock extractFrom2to1 = ps2.getRootNode().navigate("second").getExtract();
	// System.out.println("Copying from 2 to 1" + extractFrom2to1);
	// ps1.getRootNode().navigate("second").merge(extractFrom2to1);
	//
	// System.out.println("Read from 1: " + ps1.getRootNode().navigate("second").getExtract());
	//

    }

}
