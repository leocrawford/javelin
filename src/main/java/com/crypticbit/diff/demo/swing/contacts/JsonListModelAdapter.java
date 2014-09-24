package com.crypticbit.diff.demo.swing.contacts;

import javax.swing.AbstractListModel;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.StoreException;
import com.crypticbit.javelin.util.lazy.LazyJsonArray;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

@SuppressWarnings("serial")
public class JsonListModelAdapter extends AbstractListModel<String> {

    private MerkleTree jca;
    private String path;
    private LazyJsonArray backing;
    private JsonPath label;

    JsonListModelAdapter(MerkleTree jca, String path, String label) throws JsonSyntaxException, StoreException,
	    VisitorException {
	this.jca = jca;
	this.path = path;
	this.label = new JsonPath(label, new Filter[] {});
	backing = findBacking();
    }

    @Override
    public String getElementAt(int index) {
	return label.read(getJsonElementAt(index)).toString();
    }

    public Object getJsonElementAt(int index) {
	return backing.get(index);
    }

    @Override
    public int getSize() {
	return backing.size();
    }

    private LazyJsonArray findBacking() throws JsonSyntaxException, StoreException, VisitorException {
	return (LazyJsonArray) jca.getCommit().navigate(path);
    }

}
