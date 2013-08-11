package org.jgrapht.graph;

import org.jgrapht.DirectedGraph;

import com.crypticbit.javelin.js.Commit;

@SuppressWarnings("serial")
public
class HackedDirectedGraphUnion extends DirectedGraphUnion<Commit, DefaultEdge> {

    public HackedDirectedGraphUnion(DirectedGraph<Commit, DefaultEdge> a,
    	DirectedGraph<Commit, DefaultEdge> b) {
        super(a,b);
    }
    
}