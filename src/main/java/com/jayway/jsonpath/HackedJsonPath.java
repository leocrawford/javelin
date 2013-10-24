package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.PathTokenizer;

public final class HackedJsonPath extends JsonPath {
    public HackedJsonPath(String jsonPath, Filter[] filters) {
	super(jsonPath, filters);
    }

    @Override
    public PathTokenizer getTokenizer() {
	return super.getTokenizer();
    }
}