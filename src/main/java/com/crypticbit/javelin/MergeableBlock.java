package com.crypticbit.javelin;

import java.io.Serializable;

import com.crypticbit.javelin.neo4j.strategies.VectorClock;

public interface MergeableBlock extends Serializable {

    public String getJson();

    public VectorClock getVectorClock();

}
