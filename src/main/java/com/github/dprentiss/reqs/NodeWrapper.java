package com.github.dprentiss.reqs;

import java.util.Map;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * Parent class for wrapping nodes retrieved from a Reqs database.
 */
public class NodeWrapper {
    private final String NODE_TYPE = "Generic Node";
    private final Node node;
    private final Map<String, String> properties = 
        new HashMap<String, String>();

    NodeWrapper(Node node) {
        this.node = node;
        for (String s : node.getPropertyKeys()) {
            properties.put(s, (String) node.getProperty(s));
        }
    }

    protected Node getNode() {
        return node;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }

    private GraphDatabaseService graphDb() {
        return node.getGraphDatabase();
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NodeWrapper &&
            node.equals(((NodeWrapper) o).getNode());
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + "\n";
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            string += entry.getKey() + " : " + entry.getValue() + "\n";
        }
        return string; 
    }
}
