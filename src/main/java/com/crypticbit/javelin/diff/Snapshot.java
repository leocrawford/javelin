package com.crypticbit.javelin.diff;

import java.util.Date;

public class Snapshot {

    private final Date date;
    private final Object object;
    private final Object branch;
    
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
