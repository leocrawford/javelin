package com.crypticbit.javelin.js;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ArrayAdapter extends NodeAdapter {

    private transient JsonNode node;
    private List<String> rewrittenArray;

    public ArrayAdapter(JsonNode node) {
	this.node = node;
    }

    @Override
    Digest write(ContentAddressableStorage cas) throws CasException, IOException {
	rewrittenArray = new ArrayList<String>();
	ArrayNode n = (ArrayNode) node;
	for (JsonNode tn : n) {
	    rewrittenArray.add(new JsonCasAdapter(tn).write(cas).getDigestAsString());
	}
	return store(cas);
    }

}
