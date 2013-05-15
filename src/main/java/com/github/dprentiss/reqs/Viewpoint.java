package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Viewpoint nodes retrieved from a Reqs database.
 */
public class Viewpoint extends Document {

    Viewpoint(Node node) {
        super(node);
    }

    public void addConcern(Document concern) {
        addRelationshipTo(concern, ReqsDb.RelTypes.COVERS);
    }

    @Override
    public String toString() {
        return getURI(); 
    }
}
