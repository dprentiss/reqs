package com.github.dprentiss.reqs;

import java.util.Map;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;


/**
 * Parent class for wrapping a {@link Node} retrieved from a Reqs database.
 */
public abstract class NodeWrapper {
    private static final String NODE_TYPE = "Node";
    private final Node node;
    private final Map<String, String> properties = 
        new HashMap<String, String>();

    NodeWrapper(Node node) {
        this.node = node;
        for (String s : node.getPropertyKeys()) {
            properties.put(s, (String) node.getProperty(s));
        }
    }

    /**
     * Returns the node associated with {@link NodeWrapper}.
     */
    protected Node getNode() {
        return node;
    }
    
    /**
     * Returns all {@link Node} property keys and values of the associated node.
     */
    protected Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets a property on the associated node.
     *
     * @param key Property key
     * @param value Property value
     */
    protected void setProperty(String key, String value) {
        Transaction tx = graphDb().beginTx();
        try {
            node.setProperty(key, value);
            tx.success();
        } finally {
            tx.finish();
        }
    }
    
    protected void addRelationshipTo(Node otherNode, RelationshipType relType) {
        Transaction tx = graphDb().beginTx();
        try {
            node.createRelationshipTo(otherNode, relType);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    protected void addRelationshipFrom(Node otherNode, 
            RelationshipType relType) {
        Transaction tx = graphDb().beginTx();
        try {
            otherNode.createRelationshipTo(node, relType);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    protected <T extends NodeWrapper> Iterable<T> getRelsByDepth(RelationshipType relType, int depth) {

        TraversalDescription traversal = Traversal.description()
            .breadthFirst()
            .relationships(relType)
            .uniqueness(Uniqueness.NODE_GLOBAL)
            .evaluator(Evaluators.toDepth(depth))
            .evaluator(Evaluators.excludeStartPosition());
        Iterable<T> rels = null;
        return rels;
    }

    /**
     * Returns the {@GraphDatabaseService} that contians the node.
     */
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
