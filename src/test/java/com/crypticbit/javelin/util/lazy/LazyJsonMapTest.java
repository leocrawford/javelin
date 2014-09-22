package com.crypticbit.javelin.util.lazy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class LazyJsonMapTest {

    @Test
    public void test() {
	Map<String, Reference> backing = new HashMap<>();
	backing.put("t1", new ValueReference("v1"));
	backing.put("t2", new ValueReference("v2"));

	LazyJsonMap ljm = new LazyJsonMap(backing);

	ljm.put("t2", new ValueReference("r2"));
	ljm.put("t3", new ValueReference("r3"));

	System.out.println(ljm);
    }

}
