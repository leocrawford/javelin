package com.crypticbit.javelin.js;

import java.io.UnsupportedEncodingException;
import java.util.*;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class Commit implements Comparable<Commit> {

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

    public List<Commit> getShortestHistory() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {

	List<Commit> shortest = null;
	for (Commit c : getParents()) {
	    List<Commit> consider = c.getShortestHistory();
	    if (shortest == null || shortest.size() > consider.size())
		shortest = consider;
	}
	if (shortest == null)
	    shortest = new LinkedList<>();
	shortest.add(0, this);
	return shortest;
    }

    public String getUser() {
	return dao.getUser();
    }

    public Date getDate() {
	return dao.getWhen();
    }

    public Set<Commit> getParents() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	Set<Commit> parents = new TreeSet<>();
	for (Digest parent : dao.getParents()) {
	    parents.add(wrap(commitFactory.read(parent)));
	}
	return parents;
    }

    @Override
    public String toString() {
	return dao.toString();
    }

    private Commit wrap(CommitDao dao) {
	return new Commit(dao, jsonFactory, commitFactory);
    }

    @Override
    public int compareTo(Commit o) {
	return this.getDate().compareTo(o.getDate());
    }

}
