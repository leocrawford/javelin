package com.crypticbit.javelin.diff;

import java.util.Date;

/** Represents a particular snapshot of a change graph */
public class Snapshot {

    /** AN optional date that the change occurred at */
    private final Date date;
    /** The object at that snapshot */
    private final Object object;
    /** An identifier of the branch it was on */
    private final Object branch;

    /** Create a snapshot */
    Snapshot(Date date, Object object, Object branch) {
	this.date = date;
	this.object = object;
	this.branch = branch;
    }

    public Date getDate() {
	return date;
    }

    public Object getBranch() {
	return branch;
    }

    public Object getObject() {
	return object;
    }

}
