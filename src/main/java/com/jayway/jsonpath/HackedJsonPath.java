package com.jayway.jsonpath;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.PathTokenizer;

public final class HackedJsonPath extends JsonPath {
    public HackedJsonPath(String jsonPath, Filter[] filters) {
        super(jsonPath, filters);
    }

    public PathTokenizer getTokenizer(){
        return super.getTokenizer();
    }
}