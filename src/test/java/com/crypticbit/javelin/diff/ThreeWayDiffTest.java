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
	List<String> result = twd.apply();
	Assert.assertArrayEquals(new String[] { "a", "b", "y", "x", "c" }, result.toArray());
    }

    @Test
    public void testListAddWithoutDate() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	Assert.assertArrayEquals(new String[] { "a", "b", "y", "x", "c" }, twd.apply().toArray());
    }

    @Test
    public void testListMultipleStepAddAndDelete() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "b", "y", "c" }), "Branch 2");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "a", "x", "c", "d" }), "Branch 1");
	Assert.assertArrayEquals(new String[] { "a", "y", "x", "c", "d" }, twd.apply().toArray());
    }

    @Test
    public void testListMultipleStepChange() {
	List<String> lca = new ArrayList<>(Arrays.asList(new String[] { "a", "b", "c" }));
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<List<String>>(lca);
	twd.addBranchSnapshot(Arrays.asList(new String[] { "i", "a", "b", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "j", "k", "l", "a", "b", "c" }), "Branch 2");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "i", "a", "x", "c" }), "Branch 1");
	twd.addBranchSnapshot(Arrays.asList(new String[] { "j", "k", "l", "y", "b", "c" }), "Branch 2");
	// System.out.println(twd.getPatch());
	Assert.assertArrayEquals(new String[] {  "j", "k", "l", "i","y", "x", "c" }, twd.apply().toArray());
    }

    @Test
    public void testListWithinListAddWithoutDate() {

	List<Object> lca = GSON.fromJson("[a, b,[c]]", List.class);
	ThreeWayDiff<List<Object>> twd = new ThreeWayDiff<List<Object>>(lca);
	twd.addBranchSnapshot(GSON.fromJson("[a, b,[c,d]]", List.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("[a, b,[c,e]]", List.class), "Branch 2");
	Assert.assertArrayEquals(GSON.fromJson("[a, b,[c,e,d]]", List.class).toArray(), twd.apply().toArray());
    }

    @Test
    public void testMapAddAndDelete() {
	Map<String, String> lca = GSON.fromJson("{A:a,B:b,C:c}", Map.class);
	ThreeWayDiff<Map<String, String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:c,D:d}", Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b}", Map.class), "Branch 2");
	Assert.assertEquals(GSON.fromJson("{A:a, B:b, D:d}", Map.class), twd.apply());
    }

    @Test
    public void testMapChange() {
	Map<String, String> lca = GSON.fromJson("{A:a,B:b,C:{X:x}}", Map.class);
	ThreeWayDiff<Map<String, String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:{X:x,Y:y}}", Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:{Z:z}}", Map.class), "Branch 2");
	Assert.assertEquals(GSON.fromJson("{A:a, B:b,C:{Y:y,Z:z}}", Map.class), twd.apply());
    }

    @Test
    public void testMapAndArrayChange() {
	List<String> lca = GSON.fromJson("[a,b,{X:x}]", List.class);
	ThreeWayDiff<List<String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("[a,b,{X:x,Y:y}]", List.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("[a,b,{Z:z}]", List.class), "Branch 2");
	Assert.assertEquals(GSON.fromJson("[a,b,{Y:y,Z:z}]", List.class), twd.apply());
    }

    @Test
    public void testArrayAndMapChange() {
	Map<String, String> lca = GSON.fromJson("{A:a,B:b,C:[x]}", Map.class);
	ThreeWayDiff<Map<String, String>> twd = new ThreeWayDiff<>(lca);
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:[x,y]}", Map.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("{A:a,B:b,C:[x,z]}", Map.class), "Branch 2");
	Assert.assertEquals(GSON.fromJson("{A:a, B:b,C:[x,z,y]}", Map.class), twd.apply());
    }

    @Test
    public void testArrayMultipleChangesOfDifferentLengths() {
	List<Object> lca = GSON.fromJson("[a, b, c]", List.class);
	ThreeWayDiff<List<Object>> twd = new ThreeWayDiff<List<Object>>(lca);
	twd.addBranchSnapshot(GSON.fromJson("[d,e]", List.class), "Branch 1");
	twd.addBranchSnapshot(GSON.fromJson("[f,g,h,i]", List.class), "Branch 2");
	Assert.assertArrayEquals(GSON.fromJson("[f,g,i]", List.class).toArray(), twd.apply().toArray());

    }

    @Test
    public void testRealExample() {
	ThreeWayDiff<Map<String, ?>> twd = new ThreeWayDiff<Map<String, ?>>(GSON.fromJson(
		"{name:\"Bill\",aliases:[Billy],phone:[{type:Mobile,code:01222,number:123456}],addresses:"
			+ "{home:[\"11 The Street\",\"Swindon\",\"Wilts\"]}}", Map.class));
	// correct number
	twd.addBranchSnapshot(GSON.fromJson(
		"{name:\"Bill\",aliases:[Billy],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
			+ "{home:[\"11 The Street\",\"Swindon\",\"Wilts\"]}}", Map.class), "Branch 1");

	// turn Wilts into Wilshire
	twd.addBranchSnapshot(GSON.fromJson(
		"{name:\"Bill\",aliases:[Billy],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
			+ "{home:[\"11 The Street\",\"Swindon\",\"Wiltshire\"]}}", Map.class), "Branch 1");

	// Add Nan's address
	twd.addBranchSnapshot(
		GSON.fromJson(
			"{name:\"Bill\",aliases:[Billy],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
				+ "{home:[\"11 The Street\",\"Swindon\",\"Wiltshire\"]},nans:[\"12 The Street\",\"Swindon\",\"Wilts\"]}",
			Map.class), "Branch 1");

	// turn Wilts into Wilshire
	twd.addBranchSnapshot(
		GSON.fromJson(
			"{name:\"Bill\",aliases:[Billy],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
				+ "{home:[\"11 The Street\",\"Swindon\",\"Wilts\"]},nans:[\"12 The Street\",\"Swindon\",\"Wiltshire\"]}",
			Map.class), "Branch 1");

	// Add alias "The Kid"
	twd.addBranchSnapshot(
		GSON.fromJson(
			"{name:\"Bill\",aliases:[\"The Kid\",Billy],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
				+ "{home:[\"11 The Street\",\"Swindon\",\"Wilts\"]},nans:[\"12 The Street\",\"Swindon\",\"Wiltshire\"]}",
			Map.class), "Branch 2");

	// turn Billy to BillNoMates
	twd.addBranchSnapshot(
		GSON.fromJson(
			"{name:\"Bill\",aliases:[BillyNoMates],phone:[{type:Mobile,code:01222,number:123457}],addresses:"
				+ "{home:[\"11 The Street\",\"Swindon\",\"Wiltshire\"]},nans:[\"12 The Street\",\"Swindon\",\"Wiltshire\"]}",
			Map.class), "Branch 1");

	// Add new number
	twd.addBranchSnapshot(
		GSON.fromJson(
			"{name:\"Bill\",aliases:[\"The Kid\",Billy],phone:[{type:Mobile,code:01222,number:123457},{type:Landline,code:01333,number:987656}],addresses:"
				+ "{home:[\"11 The Street\",\"Swindon\",\"Wilts\"]},nans:[\"12 The Street\",\"Swindon\",\"Wiltshire\"]}",
			Map.class), "Branch 2");

	System.out.println(twd.apply());
	// Assert.assertEquals(GSON.fromJson("{A:a, B:b,C:[x,y,z]}", Map.class),lca);
    }

    // @Test
    // public void testProcessExtraTypes() {
    // JsonParser parser = new JsonParser();
    // JsonElement lca = parser.parse("[a,b,{X:x}]");
    // ThreeWayDiff<JsonElement> twd = new ThreeWayDiff<>(lca, new DifferFactory() {
    // {
    // this.addApplicator(new JsonElementArray());
    // this.addApplicator(new JsonElementMap());
    // }
    // });
    // twd.addBranchSnapshot(parser.parse("[a,{X:x,Y:y}]"), "Branch 1");
    // twd.addBranchSnapshot(parser.parse("[i,j,a,b,{Z:z}]"), "Branch 2");
    // twd.apply();
    // System.out.println(lca);
    // Assert.assertEquals(4, lca.getAsJsonArray().size());
    // }

}
