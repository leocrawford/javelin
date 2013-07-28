package com.crypticbit.javelin.diff;

import java.util.Date;

/** Represents a particular snapshot of a change graph */
public class Snapshot<T> {

    /** AN optional date that the change occurred at */
    private final Date date;
    /** The object at that snapshot */
    private final T object;
    /** An identifier of the branch it was on */
    private final Object branch;

    /** Create a snapshot */
    Snapshot(Date date, T object, Object branch) {
	this.date = date;
	this.object = object;
	this.branch = branch;
    }

    public Object getBranch() {
	return branch;
    }

    public Date getDate() {
	return date;
    }

    public T getObject() {
	return object;
    }

    @Override
    public String toString() {
	return getBranch() + ": " + object.toString();
    }

}
