package com.crypticbit.javelin.js;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.store.Identity;
import com.crypticbit.javelin.store.cas.CasFactory;
import com.crypticbit.javelin.store.cas.ContentAddressableStorage;
import com.google.gson.JsonElement;

public class JsonCasAdapterTest {

    private static final String JSON_EXAMPLE = "[\"foo\",100,{\"a\":1000.21,\"b\":6},true,null,[1,2,3]]";

    @Test
    public void test() throws  IOException, StoreException {
	Logger LOG = Logger.getLogger("com.crypticbit");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.FINEST);
	LOG.addHandler(handler);
	LOG.setLevel(Level.FINEST);
	
	JsonCasAdapter jca = new JsonCasAdapter(JSON_EXAMPLE);
	ContentAddressableStorage cas = new CasFactory().createMemoryCas();
	Identity head = jca.write(cas);
	JsonElement x = JsonCasAdapter.read(head, cas);
	Assert.assertEquals(jca.getElement(),x);
	
	
    }
    
    private void dump(ContentAddressableStorage cas) throws StoreException {
	for(Identity d : cas.list()) {
	    System.out.println(d+"->"+cas.get(d));
	}
    }
}
