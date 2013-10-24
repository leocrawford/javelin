package com.google.common.collect;

import java.util.Map;

import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Maps.TransformedEntriesMap;

public class HackedTransformedEntriesMap<K, V1, V2> extends TransformedEntriesMap<K, V1, V2> {

    protected HackedTransformedEntriesMap(Map<K, V1> backingMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
	super(backingMap, transformer);
    }

    public Map<K, V1> getBackedValue() {
	return this.fromMap;
    }

}
