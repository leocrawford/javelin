package com.crypticbit.javelin.js;

import java.io.IOException;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.databind.JsonNode;

public class ValueAdapter extends NodeAdapter{

    private JsonNode node;
    
    public ValueAdapter(JsonNode node) {
	this.node = node;
    }

    @Override
    Digest write(ContentAddressableStorage cas) throws CasException, IOException {
	return store(cas);
    }
    
}
