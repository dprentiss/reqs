package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for PrimaryEntity nodes retrieved from a Reqs database.
 */
public class PrimaryEntity extends NodeWrapper {
    private static final String NODE_TYPE = "primaryEntity";

    PrimaryEntity(Node node) {
        super(node);
    }

    public void setName(String name) {
        setProperty("name", name);
    }

    public void addMember(PrimaryEntity member) {
        addRelationshipTo(member.getNode(), ReqsDb.RelTypes.MEMBER);
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}
