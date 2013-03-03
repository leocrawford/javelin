package com.crypticbit.javelin.neo4j;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.crypticbit.javelin.HistoryGraphNode;
import com.crypticbit.javelin.IllegalJsonException;
import com.crypticbit.javelin.neo4j.strategies.FundementalDatabaseOperations;
import com.crypticbit.javelin.neo4j.strategies.SimpleFdoAdapter;
import com.jayway.jsonpath.internal.PathToken;

public interface Neo4JGraphNode extends HistoryGraphNode {

    public Node getDatabaseNode();
    public FundementalDatabaseOperations getStrategy();
    public Neo4JGraphNode navigate(PathToken token) throws IllegalJsonException;
    public Relationship getIncomingRelationship();
    public boolean exists();
    


}
