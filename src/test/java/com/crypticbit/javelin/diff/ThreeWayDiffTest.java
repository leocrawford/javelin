package com.crypticbit.javelin.diff;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

public class ThreeWayDiffTest {

    private static final Gson GSON = new Gson();
    
    @Test
    public void testListAddWithDate() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(new Date(10000L), Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(new Date(10001L), Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(Arrays.toString(lca.toArray()));
	Assert.assertArrayEquals(new String[] { "a", "b", "x", "y", "c" }, lca.toArray());
    }

    @Test
    public void testListAddWithoutDate() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.getPatch().apply(lca);
	Assert.assertArrayEquals(new String[] { "a", "b", "x", "y", "c" }, lca.toArray());
    }
    
    @Test
    public void testListMultipleStepAddAndDelete() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "x", "c","d" }), "Branch 1");
	twd.getPatch().apply(lca);
	Assert.assertArrayEquals(new String[] { "a", "x", "y", "c","d" }, lca.toArray());
    }
    
    @Test
    public void testListMultipleStepChange() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] {         "i", "a", "b", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] {  "j","k","l","a", "b", "c" }), "Branch 2");
	twd.addBranchSnapshot(Arrays.asList(new String[] {         "i", "a", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] {  "j","k","l","y", "b", "c" }), "Branch 2");
	System.out.println(twd.getPatch());
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertArrayEquals(new String[] { "i", "j","k","l","y", "y", "x", "c" }, lca.toArray());
    }

    @Test
    public void testListWithinListAddWithoutDate() {
	
	List<Object> lca = GSON.fromJson("[a, b,[c]]",List.class);
	ThreeWayDiff<List<Object>> twd = new ThreeWayDiff<List<Object>>(lca);
	twd.addBranchSnapshot(GSON.fromJson("[a, b,[c,d]]",List.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("[a, b,[c,e]]",List.class), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertArrayEquals(GSON.fromJson("[a, b,[c,d,e]]",List.class).toArray(), lca.toArray());
    }
    
    @Test
    public void testMapAddAndDelete() {
	Map<String,String> lca = GSON.fromJson("{A:a,B:b,C:c}",Map.class);
	ThreeWayDiff<Map<String,String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:c,D:d}",Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b}",Map.class), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertEquals(GSON.fromJson("{A:a, B:b, D:d}", Map.class),lca);
    }
    
    @Test
    public void testMapChange() {
	Map<String,String> lca = GSON.fromJson("{A:a,B:b,C:{X:x}}",Map.class);
	ThreeWayDiff<Map<String,String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:{X:x,Y:y}}",Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:{Z:z}}",Map.class), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertEquals(GSON.fromJson("{A:a, B:b,C:{Y:y,Z:z}}", Map.class),lca);
    }
    
    
    @Test
    public void testMapAndArrayChange() {
	List<?> lca = GSON.fromJson("[a,b,{X:x}]",List.class);
	ThreeWayDiff<List<?>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("[a,b,{X:x,Y:y}]",List.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("[a,b,{Z:z}]",List.class), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertEquals(GSON.fromJson("[a,b,{Y:y,Z:z}]", List.class),lca);
    }
    
    @Test
    public void testArrayAndMapChange() {
	Map<String,String> lca = GSON.fromJson("{A:a,B:b,C:[x]}",Map.class);
	ThreeWayDiff<Map<String,String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:[x,y]}",Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:[x,z]}",Map.class), "Branch 2");
	twd.getPatch().apply(lca);
	System.out.println(lca);
	Assert.assertEquals(GSON.fromJson("{A:a, B:b,C:[x,y,z]}", Map.class),lca);
    }
    
}
