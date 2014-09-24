package com.crypticbit.javelin.merkle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class CommitTest extends TestUtils {

	// FIXME - test production of graph that contains a merge

	private MerkleTree jca1, jca2, jca3, jca4;

	public CommitTest() throws StoreException, IOException, VisitorException {
		String c1 = "[\"a\"]";
		String c2 = "[\"a\",\"b\"]";
		String c3 = "[\"a\",\"b\",\"c1\"]";
		String c4 = "[\"a\",\"b\",\"c2\",\"d\"]";
		String c5 = "[\"a\",\"b\",\"c2\",\"d\",\"e\"]";
		String c6 = "[\"a\",\"b\",\"c2\",\"d\",[\"f\"],\"f\"]";
		String c7 = "[\"a\",\"b1\",\"c2\",\"d\",[\"f\"],\"g\"]";

		jca1 = new MerkleTree(new StorageFactory().createMemoryCas());
		jca1.write(c1).commit().write(c2).commit();
		jca2 = jca1.branch();
		jca1.write(c3).commit();
		jca2.write(c4).commit();
		jca3 = jca2.branch();
		jca3.write(c5).commit().write(c6);
		jca4 = jca2.branch();
		jca4.write(c7).commit();
	}

	@Test
	public void testCreateChangeSet() throws JsonSyntaxException,
			StoreException, PatchFailedException, IOException, VisitorException {
		// ThreeWayDiff patch =
		// jca1.getCommit().createChangeSet(jca4.getCommit());
		// System.out.println("X-"+patch.apply());
		System.out.println("1 = " + jca1.read());
//		show(jca1.getCommit(),jca4.getCommit());

		jca1.getCommit().debug();
		MerkleTree x = jca1.merge(jca4);
//		show(jca1.getCommit(),jca4.getCommit(),x.getCommit());
	}

	// @Test
	// public void testfindLca() throws StoreException, IOException {
	// assertEquals(jca2.getCommit(),
	// jca4.getCommit().findLca(jca3.getCommit()));
	// }

	@Test
	public void testGetAsGraph() throws StoreException, IOException,
			JsonSyntaxException, VisitorException {
		assertEquals(3, jca1.getCommit().getAsGraphToRoot().vertexSet().size());
		assertEquals(3, jca2.getCommit().getAsGraphToRoot().vertexSet().size());
		assertEquals(4, jca3.getCommit().getAsGraphToRoot().vertexSet().size());
		assertEquals(4, jca4.getCommit().getAsGraphToRoot().vertexSet().size());
	}

	@Test
	public void testNavigate() throws JsonSyntaxException, StoreException,
			VisitorException {
		String c8 = "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":[\"g\",1,2,3,{\"k\":true,\"l\":false}]}";
		jca4.write(c8).commit();

		assertEquals(false, jca4.getCommit().navigate("f[4].l"));
		assertEquals("e", jca4.getCommit().navigate("c.d"));
	}

}
