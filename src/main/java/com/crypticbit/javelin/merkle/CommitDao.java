package com.crypticbit.javelin.merkle;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.crypticbit.javelin.store.Key;

/** Simple Data Access object (DAO) for commit */
public class CommitDao {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yy-MM-dd~HH:mm:ss:SSS");
	/** Reference to head of value tree */
	private final Key head;
	/** Creation time */
	private final Date when;
	/** FIXME: User - needs to be thought about */
	private final String user;
	/**
	 * The parents of the commit. usually only one, unless this is the product
	 * of a merge
	 */
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

	@Override
	public String toString() {
		return "Commit: " + getHead() + "@" + SIMPLE_DATE_FORMAT.format(when);
	}

}
