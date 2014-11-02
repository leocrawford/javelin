package com.crypticbit.javelin.convert.lazy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LazyArrayTest {

    @Test
    public void test() {
	List<Reference> backing = new ArrayList<>();
	backing.add(new ValueReference("v1"));
	backing.add(new ValueReference("v2"));

	LazyArray lam = new LazyArray(backing);

	lam.add("r1");
	lam.add(1, "r2");
	lam.set(0, "r3");

	Assert.assertArrayEquals(new String[] { "r3", "r2", "v2", "r1" }, lam.toArray());
    }

}
