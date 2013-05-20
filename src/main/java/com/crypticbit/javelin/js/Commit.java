package com.crypticbit.javelin.js;

import java.util.Date;

import com.crypticbit.javelin.store.Digest;

public class Commit {

    /** Reference to head of value tree */
    private Digest head;
    private Date when;
    private String user;
    private Digest parents[];

    public Commit(Digest head, Date when, String user, Digest parent) {
	this(head, when, user, new Digest[] { parent });
    }

    public Commit(Digest head, Date when, String user, Digest[] parents) {
	this.head = head;
	this.when = when;
	this.user = user;
	this.parents = parents;
    }

    public Digest getHead() {
	return head;
    }

    public Date getWhen() {
	return when;
    }

    public String getUser() {
	return user;
    }

    public Digest[] getParents() {
	return parents;
    }


    public String toString() {
	return head
		+ "@"
		+ user
		+ " ("
		+ when
		+ ")"
		+ (parents == null || parents.length == 0 ? " ROOT" : (parents.length == 1 ? "" : " "+parents.length
			+ " parents"));
    }

}
