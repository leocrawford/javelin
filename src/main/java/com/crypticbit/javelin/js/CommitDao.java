package com.crypticbit.javelin.js;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.crypticbit.javelin.store.Key;

public class CommitDao {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yy-MM-dd~HH:mm:ss:SSS");
    /** Reference to head of value tree */
    private final Key head;
    private final Date when;
    private final String user;
    private final Key parents[];

    public CommitDao(Key head, Date when, String user, Key parent) {
	this(head, when, user, (parent == null ? new Key[] {} : new Key[] { parent }));
    }

    public CommitDao(Key head, Date when, String user, Key[] parents) {
	this.head = head;
	this.when = when;
	this.user = user;
	this.parents = parents;
    }

    @Override
    // FIXME
    public boolean equals(Object obj) {
	throw new Error();
	// return head.equals(((CommitDao) obj).head);
    }

    public Key getHead() {
	return head;
    }

    public Key[] getParents() {
	return parents;
    }

    public String getUser() {
	return user;
    }

    public Date getWhen() {
	return when;
    }

    @Override
    public int hashCode() {
	return head.hashCode();
    }

    @Override
    public String toString() {
	// FIXME - It's the commit we want to id, not the head. Two commits could point at same id.
	return head
		+ ":"
		+ user
		+ "@"
		+ SIMPLE_DATE_FORMAT.format(when)
		+ (parents == null || parents.length == 0 ? " ROOT" : (parents.length == 1 ? "" : " " + parents.length
			+ " parents"));
    }

}
