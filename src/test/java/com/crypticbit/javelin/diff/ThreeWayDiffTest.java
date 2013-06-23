package com.crypticbit.javelin.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ThreeWayDiffTest {

    @Test
    public void testListAddWithDate() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff twd = new ThreeWayDiff(lca);
	twd.addBranchSnapshot(new Date(10000L), Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(new Date(10001L), Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.getPatch().apply(lca);
	Assert.assertArrayEquals(new String[] { "a", "b", "x", "y", "c" }, lca.toArray());
    }

    @Test
    public void testListAddWithoutDate() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff twd = new ThreeWayDiff(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.getPatch().apply(lca);
	Assert.assertArrayEquals(new String[] { "a", "b", "x", "y", "c" }, lca.toArray());
    }

    @Test
    public void testListWithinListAddWithoutDate() {
	
	List<String> lca2 = new ArrayList(Arrays.asList(new String[] { "c" }));
	List<Object> lca = new ArrayList<>(Arrays
		.asList(new Object[] { "a", "b", lca2 }));
	ThreeWayDiff twd = new ThreeWayDiff(lca);
	twd.addBranchSnapshot(Arrays.asList(new Object[] { "a","b", Arrays.asList(new String[] { "c","d" }) }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new Object[] { "a", "b", Arrays.asList(new String[] { "c","e" }) }), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
//	Assert.assertArrayEquals(new String[] { "a", "b", "x", "y", "c" }, lca.toArray());
    }
}
