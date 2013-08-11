package org.jgrapht.graph;

import org.jgrapht.DirectedGraph;

import com.crypticbit.javelin.js.Commit;

/** A horrible hack to allow us to instantiate a DirectedGraphUnion by extending it, as constructor wasn't public */

@SuppressWarnings("serial")
public class HackedDirectedGraphUnion extends DirectedGraphUnion<Commit, DefaultEdge> {

    public HackedDirectedGraphUnion(DirectedGraph<Commit, DefaultEdge> a, DirectedGraph<Commit, DefaultEdge> b) {
	super(a, b);
    }

}