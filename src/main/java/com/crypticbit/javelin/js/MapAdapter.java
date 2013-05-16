package com.crypticbit.javelin.js;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MapAdapter extends NodeAdapter {

    private Map<String, Digest> rewrittenMap;
    private transient JsonNode node;
    
    public MapAdapter(JsonNode node) {
	this.node = node;
    }
 

    Digest write(ContentAddressableStorage cas) throws CasException, IOException {
	rewrittenMap = new HashMap<>();
	ObjectNode n = (ObjectNode) node;
	for (Iterator<Entry<String, JsonNode>> fields = n.fields(); fields.hasNext();) {
	    Entry<String, JsonNode> field  = fields.next();    
	    rewrittenMap.put(field.getKey(), new JsonCasAdapter(field.getValue()).write(cas)); 
	}
	return store(cas,rewrittenMap);
    }


 

}
