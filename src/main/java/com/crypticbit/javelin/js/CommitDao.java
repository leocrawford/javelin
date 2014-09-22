package com.crypticbit.javelin.js;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.crypticbit.javelin.store.Key;

public class CommitDao {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yy-MM-dd~HH:mm:ss:SSS");
	/** Reference to head of value tree */
	private final Key head;
	private final Date when;
	private final String user;
	private final Key parents[];

	public CommitDao(Key head, Date when, String user, Key parent) {
		this(head, when, user, (parent == null ? new Key[] {}
				: new Key[] { parent }));
	}

	public CommitDao(Key head, Date when, String user, Key[] parents) {
		this.head = head;
		this.when = when;
		this.user = user;
		this.parents = parents;
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

}
