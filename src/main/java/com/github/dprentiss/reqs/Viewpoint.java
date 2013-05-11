package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Viewpoint nodes retrieved from a Reqs database.
 */
public class Viewpoint extends Document {
    private static final String NODE_TYPE = "viewpoit";

    Viewpoint(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}