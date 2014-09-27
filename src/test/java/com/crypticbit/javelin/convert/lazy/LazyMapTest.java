package com.crypticbit.javelin.convert.lazy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.crypticbit.javelin.convert.lazy.LazyMap;
import com.crypticbit.javelin.convert.lazy.Reference;
import com.crypticbit.javelin.convert.lazy.ValueReference;

public class LazyMapTest {

    @Test
    public void test() {
	Map<String, Reference> backing = new HashMap<>();
	backing.put("t1", new ValueReference("v1"));
	backing.put("t2", new ValueReference("v2"));

	LazyMap ljm = new LazyMap(backing);

	ljm.put("t2", new ValueReference("r2"));
	ljm.put("t3", new ValueReference("r3"));

	System.out.println(ljm);
    }

}
