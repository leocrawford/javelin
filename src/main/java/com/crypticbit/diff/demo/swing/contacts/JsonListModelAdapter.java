package com.crypticbit.diff.demo.swing.contacts;

import javax.swing.AbstractListModel;

import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.js.lazy.LazyJsonArray;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

@SuppressWarnings("serial")
public class JsonListModelAdapter extends AbstractListModel<String> {

    private JsonCasAdapter jca;
    private String path;
    private LazyJsonArray backing;
    private JsonPath label;
    
    JsonListModelAdapter(JsonCasAdapter jca, String path, String label) throws JsonSyntaxException, StoreException {
	this.jca = jca;
	this.path = path;
	this.label = new JsonPath(label,new Filter[]{});
	backing = findBacking();
    }
    
    private LazyJsonArray findBacking() throws JsonSyntaxException, StoreException {
	return (LazyJsonArray) jca.getCommit().navigate(path);
    }

    @Override
    public int getSize() {
	return backing.size();
    }

    @Override
    public String getElementAt(int index) {
	return label.read(getJsonElementAt(index));
    }
    
    public Object getJsonElementAt(int index) {
	return backing.get(index);
    }

}
