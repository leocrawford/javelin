package com.crypticbit.javelin.js;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.CasFactory;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonCasAdapterTest {

    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException, CasException {
	Logger LOG = Logger.getLogger("com.crypticbit");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.FINEST);
	LOG.addHandler(handler);
	LOG.setLevel(Level.FINEST);
	
	JsonCasAdapter jca = new JsonCasAdapter("[\"foo\",100,\"a\",1000.21,true,null]");
	ContentAddressableStorage cas = new CasFactory().createMemoryCas();
	jca.write(cas);
	
	for(Digest d : cas.list())
	System.out.println(d+"->"+cas.get(d));

    }
}
