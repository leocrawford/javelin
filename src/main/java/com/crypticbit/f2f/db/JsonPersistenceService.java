package com.crypticbit.f2f.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPath;

public interface JsonPersistenceService {

    public JsonNode get(JsonPath path);

}
