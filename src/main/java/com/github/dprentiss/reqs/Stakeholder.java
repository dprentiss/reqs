package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Stakeholder nodes retrieved from a Reqs database.
 */
public class Stakeholder extends PrimaryEntity {
    private static final String NODE_TYPE = "Stakeholder";

    Stakeholder(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        /*
        return NODE_TYPE + " " + super.toString();
        */
        return getName();
    }
}
