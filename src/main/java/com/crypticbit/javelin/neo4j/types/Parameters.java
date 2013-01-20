package com.crypticbit.javelin.neo4j.types;

public interface Parameters {
    
    public enum Relationship {  
    KEY, INDEX
    }
    
    public enum Node {
	TYPE, VALUE, VERSION_CLOCK
    }
    
    public static Node[] PRESERVED=new Node[]{Node.VERSION_CLOCK};
    public static Node[] DISCARDED=new Node[]{Node.TYPE,Node.VALUE};
}