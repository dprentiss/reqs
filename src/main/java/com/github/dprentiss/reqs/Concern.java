package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for concern nodes retrieved from a Reqs database.
 */
public class Concern extends NodeWrapper {
    private static final String NODE_TYPE = "Concern";

    Concern(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}
