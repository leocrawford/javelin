package com.crypticbit.javelin.diff;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.diff.list.MultiViewList;

public class MultiViewListTest {

    private static final String[] SIMPLE_ARRAY = new String[] { "a", "b", "c", "d" };

    @Test
    public void testAdd() {
	MultiViewList<String> t = new MultiViewList<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	MultiViewList<String> ma = t.getMode("a");

	ma.add(0, "u");
	ma.add(0, "v");
	ma.add(1, "w");
	ma.add(2, "y");
	Assert.assertArrayEquals(new String[] { "v", "w", "y", "u", "a", "b", "c", "d" }, t.toArray());
	MultiViewList<String> mb = t.getMode("b");
	mb.add(0, "U");
	mb.add(0, "V");
	mb.add(1, "W");
	mb.add(2, "Y");
	Assert.assertArrayEquals(new String[] { "V", "W", "Y", "U", "v", "w", "y", "u", "a", "b", "c", "d" }, t
		.toArray());
    }

    @Test
    public void testAddAllIntCollection() {
	MultiViewList<String> t = new MultiViewList<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	MultiViewList<String> ma = t.getMode("a");
	ma.addAll(0, Arrays.asList(new String[] { "x", "y", "z" }));
	ma.addAll(2, Arrays.asList(new String[] { "u" }));
	MultiViewList<String> mb = t.getMode("b");
	mb.addAll(1, Arrays.asList(new String[] {}));
	mb.addAll(1, Arrays.asList(new String[] { "s", "t" }));
	mb.addAll(2, Arrays.asList(new String[] { "v" }));
	Assert.assertArrayEquals(new String[] { "x", "y", "u", "z", "a", "s", "v", "t", "b", "c", "d" }, t.toArray());
    }

    @Test
    public void testComplexAddAndRemove() {
	MultiViewList<String> t = new MultiViewList<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	MultiViewList<String> ma = t.getMode("a");
	ma.remove(1);
	ma.remove(2);

	Assert.assertArrayEquals(new String[] { "a", "c" }, t.toArray());
	ma.add(1, "x");
	Assert.assertArrayEquals(new String[] { "a", "x", "c" }, t.toArray());
	MultiViewList<String> mb = t.getMode("b");
	mb.remove(2);
	mb.add("y");
	mb.add("z");
	Assert.assertArrayEquals(new String[] { "a", "x", "y", "z" }, t.toArray());
	ma.remove(1);
	Assert.assertArrayEquals(new String[] { "a", "y", "z" }, t.toArray());

	ma.remove(1);

	Assert.assertArrayEquals(new String[] { "a", "y", "z" }, t.toArray());
    }

    @Test
    public void testRemove() {
	MultiViewList<String> t = new MultiViewList<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	MultiViewList<String> ma = t.getMode("a");
	ma.remove(1);
	ma.remove(2);
	Assert.assertArrayEquals(new String[] { "a", "c" }, t.toArray());
	MultiViewList<String> mb = t.getMode("b");
	mb.remove(2);
	Assert.assertArrayEquals(new String[] { "a" }, t.toArray());

	System.out.println(t);
	System.out.println(ma);
	System.out.println(mb);
    }

    @Test
    public void testSimpleAdd() {
	MultiViewList<String> t = new MultiViewList<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	MultiViewList<String> ma = t.getMode("a");
	ma.add(0, "x");
	MultiViewList<String> mb = t.getMode("b");
	mb.add(0, "y");
	mb.add(1, "z");

	Assert.assertEquals(3, mb.transformIndexForAccess(2));
	Assert.assertEquals(4, ma.transformIndexForAccess(2));
	Assert.assertArrayEquals(new String[] { "y", "z", "x", "a", "b", "c", "d" }, t.toArray());
    }

}
