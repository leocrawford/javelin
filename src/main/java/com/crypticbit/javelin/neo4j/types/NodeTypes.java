package com.crypticbit.javelin.neo4j.types;

import org.neo4j.graphdb.Node;

import com.crypticbit.javelin.neo4j.nodes.ComplexNode;
import com.crypticbit.javelin.neo4j.nodes.json.ArrayGraphNode;
import com.crypticbit.javelin.neo4j.nodes.json.JsonGraphNode;
import com.crypticbit.javelin.neo4j.nodes.json.MapGraphNode;
import com.crypticbit.javelin.neo4j.nodes.json.ValueGraphNode;

public enum NodeTypes {
    ARRAY() {
	@Override
	JsonGraphNode _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new ArrayGraphNode(graphNode, complexNode);
	}

    },
    MAP() {
	@Override
	JsonGraphNode _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new MapGraphNode(graphNode, complexNode);
	}
    },
    VALUE() {
	@Override
	JsonGraphNode _wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
	    return new ValueGraphNode(graphNode, complexNode);
	}
    };
    abstract JsonGraphNode _wrapAsGraphNode(Node graphNode, ComplexNode complexNode);

    public static JsonGraphNode wrapAsGraphNode(Node graphNode, ComplexNode complexNode) {
System.out.println("Found type @ :"+graphNode.getId());
	if (graphNode.hasProperty(Parameters.Node.TYPE.name())) return valueOf(
		(String) graphNode.getProperty(Parameters.Node.TYPE.name()))._wrapAsGraphNode(graphNode, complexNode);
	else
	    throw new Error("Found node with no type. Node id: " + graphNode.getId());
	// FIXME throw exceptiom
    }
}