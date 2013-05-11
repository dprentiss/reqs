package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Stakeholder nodes retrieved from a Reqs database.
 */
public class Stakeholder extends NodeWrapper {
    private static final String NODE_TYPE = "stakeholder";

    Stakeholder(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}
