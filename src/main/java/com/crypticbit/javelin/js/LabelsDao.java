package com.crypticbit.javelin.js;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.crypticbit.javelin.store.Identity;

public class LabelsDao implements Serializable {

    private Map<String,Identity> labels = new HashMap<>();
    
    public Anchor getAnchor(String name) {
	return new Anchor(labels.get(name));
    }
    
    public Anchor addAnchor(String label) {
    	Anchor result = new Anchor();
    	labels.put(label, result.getAddress());
    	return result;
    }

	public boolean hasAnchor(String label) {
		return labels.containsKey(label);
	}

	public void addAnchor(String label, Anchor anchor) {
		labels.put(label, anchor.getAddress());
    	
	}
    

}
