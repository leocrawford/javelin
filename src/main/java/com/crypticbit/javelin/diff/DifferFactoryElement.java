package com.crypticbit.javelin.diff;

public interface DifferFactoryElement {

    public SequenceDiff createApplicator();

    public boolean supports(Object object);

}
