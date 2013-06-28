package com.crypticbit.javelin.diff;

public interface DifferFactoryElement {

    public boolean supports(Object object);
    public SequenceDiff createApplicator(); 
    
}
