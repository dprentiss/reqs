package com.github.dprentiss.reqs;

/**
 * Allows for compile-time checking of node type usage. Based on {@link org.neo4j.graphdb.RelationshipType}.
 */
public interface NodeType {
    public String name();
}
