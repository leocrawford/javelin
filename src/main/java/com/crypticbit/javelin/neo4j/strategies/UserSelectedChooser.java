package com.crypticbit.javelin.neo4j.strategies;

import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.neo4j.graphdb.Relationship;

public class UserSelectedChooser implements Chooser {

    @Override
    public Entry<VectorClock, Relationship> select(List<Entry<VectorClock, Relationship>> candidates) {

	System.out.println("Concurrent updates. please choose one");
	int loop = 1;
	for (Entry<VectorClock, Relationship> c : candidates) {
	    System.out.println((loop++) + ") " + c.getValue().getEndNode() + " - " + c.getKey());
	}
	Scanner scanIn = new Scanner(System.in);
	int selection = scanIn.nextInt();
	scanIn.close();
	return candidates.get(selection - 1);

    }
}
