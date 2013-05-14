package com.github.dprentiss.reqs;

import org.neo4j.graphdb.Node;

/**
 * {@link NodeWrapper} for Document nodes retrieved from a Reqs database.
 */
public class Document extends NodeWrapper {
    private static final String NODE_TYPE = "document";

    Document(Node node) {
        super(node);
    }
    
    public void setURI(String URI) {
        setProperty("URI", URI);
    }

    public String getURI() {
        return (String)super.getProperty("URI");
    }

    public void setSummary(String summary) {
        setProperty("summary", summary);
    }

    public String getSummary() {
        return (String)super.getProperty("summary");
    }

    @Override
    public String toString() {
        String string = NODE_TYPE + " " + super.toString();
        return string; 
    }
}
