package com.crypticbit.javelin.js;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonCasAdapter {

    private enum NodeType {
	MAP {
	    @Override
	    boolean isType(JsonNode node) {
		return node.isObject();
	    }

	    @Override
	    NodeAdapter getAdapter(JsonNode node) {
		return new MapAdapter(node);
	    }

	},
	ARRAY {
	    @Override
	    boolean isType(JsonNode node) {
		return node.isArray();
	    }
	    
	    @Override
	    NodeAdapter getAdapter(JsonNode node) {
		return new ArrayAdapter(node);
	    }

	    
	},
	VALUE {
	    @Override
	    boolean isType(JsonNode node) {
		return node.isValueNode();
	    }
	    
	    @Override
	    NodeAdapter getAdapter(JsonNode node) {
		return new ValueAdapter(node);
	    }

	  
	};
	abstract boolean isType(JsonNode node);
	abstract NodeAdapter getAdapter(JsonNode node);

	static NodeType findType(JsonNode node) {
	    for (NodeType nt : NodeType.values())
		if (nt.isType(node))
		    return nt;
	    throw new Error("An unknown Json type was discovered: " + node.getClass());
	}

	
    }

    private JsonNode node;

    public JsonCasAdapter(String data) throws JsonParseException, JsonMappingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	node = mapper.readValue(data, JsonNode.class); // src can be a File, URL, InputStream etc

    }

    public JsonCasAdapter(JsonNode node) {
	this.node = node;
    }

    public Digest write(ContentAddressableStorage cas) throws CasException, IOException {
	return NodeType.findType(node).getAdapter(node).write(cas);
    }
}
