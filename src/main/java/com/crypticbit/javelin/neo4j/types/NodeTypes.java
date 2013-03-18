package com.crypticbit.javelin.neo4j.types;

import org.neo4j.graphdb.Node;

import com.crypticbit.javelin.neo4j.Neo4JJsonType;
import com.crypticbit.javelin.neo4j.nodes.ArrayGraphNode;
import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.nodes.MapGraphNode;
import com.crypticbit.javelin.neo4j.nodes.ValueGraphNode;

public enum NodeTypes {
    ARRAY() {
	@Override
	Neo4JJsonType _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new ArrayGraphNode(graphNode, complexNode);
	}

    },
    MAP() {
	@Override
	Neo4JJsonType _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new MapGraphNode(graphNode, complexNode);
	}
    },
    VALUE() {
	@Override
	Neo4JJsonType _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new ValueGraphNode(graphNode, complexNode);
	}
    };
    public static Neo4JJsonType wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	if (graphNode.hasProperty(Parameters.Node.TYPE.name()))
	    return valueOf((String) graphNode.getProperty(Parameters.Node.TYPE.name()))._wrapAsGraphNode(graphNode,complexNode);
	else
	  throw new Error("Found node with no type. Node id: "+graphNode.getId());
	// FIXME throw exceptiom
    }

    abstract Neo4JJsonType _wrapAsGraphNode(Node graphNode, ComplexNode complexNode);
}