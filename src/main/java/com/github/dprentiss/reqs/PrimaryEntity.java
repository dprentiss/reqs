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

    public String getName() {
        return (String) getProperty("name");
    }

    public void addMember(PrimaryEntity member) {
        addRelationshipFrom(member, ReqsDb.RelTypes.IS_MEMBER);
    }

    @Override
    public String toString() {
        return getName();
    }
}
