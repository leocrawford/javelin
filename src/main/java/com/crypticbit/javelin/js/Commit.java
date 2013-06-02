package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class Commit {

    private CommitDao dao;
    private JsonFactory jsonFactory;
    private CommitFactory commitFactory;
    
    Commit(CommitDao dao, JsonFactory jsonFactory, CommitFactory commitFactory) {
	this.dao = dao;
	this.jsonFactory = jsonFactory;
	this.commitFactory = commitFactory;
	
    }

    public JsonElement getElement() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	  return jsonFactory.read(dao.getHead());
    }
    
    public Date getWhen() {
        return dao.getWhen();
    }
    
    public String getUser() {
        return dao.getUser();
    }
    
    public String toString() {
	return dao.toString();
    }
    
    public List<Commit> getHistory() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	List<Commit> history = new LinkedList<>();
	for(CommitDao parent = dao; parent != null; parent = parent.getParents().length > 0 ? commitFactory.read(parent.getParents()[0]):null)
	    history.add(new Commit(parent,jsonFactory, commitFactory));
	return history;
    }
    
    
    

}
