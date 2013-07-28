package com.crypticbit.javelin.js;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.crypticbit.javelin.store.Digest;
import com.crypticbit.javelin.store.Identity;

public class CommitDao {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yy-MM-dd~HH:mm:ss:SSS");
    /** Reference to head of value tree */
    private final Identity head;
    private final Date when;
    private final String user;
    private final Identity parents[];

    public CommitDao(Identity head, Date when, String user, Identity parent) {
	this(head, when, user, (parent == null ? new Identity[] {} : new Identity[] { parent }));
    }

    public CommitDao(Identity head, Date when, String user, Identity[] parents) {
	this.head = head;
	this.when = when;
	this.user = user;
	this.parents =  parents;
    }

    @Override
    // FIXME
    public boolean equals(Object obj) {
	return head.equals(((CommitDao) obj).head);
    }

    public Identity getHead() {
	return head;
    }

    public Identity[] getParents() {
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
