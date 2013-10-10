package com.crypticbit.javelin.js;

import java.util.HashMap;
import java.util.Map;

public class DataStructureDao {

    private Map<String,Anchor> labels = new HashMap<>();
    
    public Anchor getAnchor(String name) {
	return labels.get(name);
    }
    

}
