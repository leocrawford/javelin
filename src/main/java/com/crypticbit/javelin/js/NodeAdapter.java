package com.crypticbit.javelin.js;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypticbit.javelin.cas.ByteBasedPersistableResource;
import com.crypticbit.javelin.cas.CasException;
import com.crypticbit.javelin.cas.ContentAddressableStorage;
import com.crypticbit.javelin.cas.Digest;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public abstract class NodeAdapter {

    public static class DigestSerializer extends JsonSerializer<Digest> {

	@Override
	public void serialize(Digest value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
		JsonProcessingException {
	    jgen.writeString(value.getDigestAsString());

	}

    }

    private transient ObjectMapper mapper = new ObjectMapper();
    {
	mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

	SimpleModule testModule = new SimpleModule();
	testModule.addSerializer(Digest.class, new DigestSerializer());
	mapper.registerModule(testModule);
    }
    private static final Logger LOG = Logger.getLogger("com.crypticbit.javelin.js");

    abstract Digest write(ContentAddressableStorage cas) throws CasException, IOException;

    protected Digest store(ContentAddressableStorage cas, Object object) throws CasException, IOException {
	if (LOG.isLoggable(Level.FINEST))
	    LOG.log(Level.FINEST, "Writing " + this.getClass().getSimpleName() + " to CAS");
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	mapper.writeValue(out, object);
	return cas.store(new ByteBasedPersistableResource(out.toByteArray()));
    }

}
