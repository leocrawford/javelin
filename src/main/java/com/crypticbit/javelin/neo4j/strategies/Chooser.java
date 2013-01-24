package com.crypticbit.javelin.neo4j.strategies;

import java.util.List;
import java.util.Map.Entry;

import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;

public interface Chooser {

    Entry<VectorClock, Relationship> select(List<Entry<VectorClock, Relationship>> list);

}
