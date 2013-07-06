package com.crypticbit.javelin.js;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.diff.list.UnorderedIndexedWritesListDecorator;

public class UnorderedIndexedWritesListDecoratorTest {

    private static final String[] SIMPLE_ARRAY = new String[] { "a", "b", "c", "d" };

    @Test
    public void testAddAllIntCollection() {
	List<String> t = new UnorderedIndexedWritesListDecorator<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	t.addAll(0, Arrays.asList(new String[] { "x", "y", "z" }));
	t.addAll(2, Arrays.asList(new String[] { "u" }));
	t.addAll(1, Arrays.asList(new String[] {}));
	t.addAll(1, Arrays.asList(new String[] { "s", "t" }));
	t.addAll(2, Arrays.asList(new String[] { "v" }));
	System.out.println(t);
	Assert.assertArrayEquals(new String[] { "x", "y", "z", "a", "s", "t", "b", "u", "v", "c", "d" }, t.toArray());
    }

    @Test
    public void testAdd() {
	UnorderedIndexedWritesListDecorator<String> t = new UnorderedIndexedWritesListDecorator<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	t.addMode("a");
	t.addMode("b");
	t.chooseMode("a");
	
	t.add(0, "u");
	t.add(0, "v");
	t.add(1, "w");
	t.add(2, "y");
	System.out.println(t);
	Assert.assertArrayEquals(new String[] { "v", "w","y","u", "a","b","c", "d" }, t.toArray());
	
	t.chooseMode("b");
	t.add(0, "U");
	t.add(0, "V");
	t.add(1, "W");
	t.add(2, "Y");
	
	System.out.println(t);
    }

    @Test
    public void testRemove() {
	List<String> t = new UnorderedIndexedWritesListDecorator<>(new ArrayList<String>(Arrays.asList(SIMPLE_ARRAY)));
	t.remove(1);
	t.remove(2);
	Assert.assertArrayEquals(new String[] { "a", "d" }, t.toArray());
    }

}
