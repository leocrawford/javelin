package com.crypticbit.javelin.js;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class NodeAdapter {

    private transient ObjectMapper mapper = new ObjectMapper();
    {
	mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }
    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    abstract Digest write(ContentAddressableStorage cas) throws CasException, IOException;

    protected InputStream getInputStream() throws JsonGenerationException, JsonMappingException, IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	mapper.writeValue(out, this);
	return new ByteArrayInputStream(out.toByteArray());
    }

    protected Digest store(ContentAddressableStorage cas) throws CasException, IOException {
	if (LOG.isLoggable(Level.FINEST))
	    LOG.log(Level.FINEST, "Writing " + this.getClass().getSimpleName() + " to CAS");
	return cas.store(getInputStream());
    }

}
