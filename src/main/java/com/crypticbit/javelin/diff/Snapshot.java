package com.crypticbit.javelin.diff;

import java.util.Date;

/** Represents a particular snapshot of a change graph */
public class Snapshot<T> {

    /** The object at that snapshot */
    private final T object;
    /** An identifier of the branch it was on */
    private final Object branch;

    /** Create a snapshot */
    public Snapshot(T object, Object branch) {
	this.object = object;
	this.branch = branch;
    }

    public Object getBranch() {
	return branch;
    }

    public T getObject() {
	return object;
    }

    @Override
    public String toString() {
	return getBranch().hashCode() + ": " + object.toString();
    }

}
