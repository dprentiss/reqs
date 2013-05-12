package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Person nodes retrieved from a Reqs database.
 */
public class Person extends PrimaryEntity {
    private static final String NODE_TYPE = "person";

    Person(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}
