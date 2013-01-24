package com.crypticbit.javelin;

import java.io.Serializable;

import com.crypticbit.javelin.neo4j.strategies.VectorClockAdapter.VectorClock;

public interface MergeableBlock extends Serializable {

    public String getJson();

    public VectorClock getVectorClock();

}
