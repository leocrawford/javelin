package com.crypticbit.javelin;

import java.io.IOException;
import java.nio.file.Files;

import com.crypticbit.javelin.neo4j.Neo4JJsonPersistenceService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Neo4JTestSupport {

    protected static final String JSON_TEXT = "{\"first\": 123, \"second\": [{\"k1\":{\"id\":\"sd1 p\"}}, 4, 5, 6, {\"id\": 123}], \"third\": 789, \"xid\": null}";
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    public Neo4JTestSupport() {
	super();
    }

    protected Neo4JJsonPersistenceService createNewService() throws IOException {
	return createNewService("unidentified-identity");
    }

    protected Neo4JJsonPersistenceService createNewService(String identity) throws IOException {
	return new Neo4JJsonPersistenceService(Files.createTempDirectory("neo4j_test").toFile(), identity);
    }

}