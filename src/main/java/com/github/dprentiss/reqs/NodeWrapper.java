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
 * 
 * @author David Prentiss
 */
public abstract class NodeWrapper {
    private static final String NODE_TYPE = "Node";
    private final Node node;

    /**
     * Constructor.
     *
     * @param node The underlying node of this object
     */
    NodeWrapper(Node node) {
        this.node = node;
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
    protected Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        for (String s : node.getPropertyKeys()) {
            properties.put(s, node.getProperty(s));
        }
        return properties;
    }

    /**
     * Sets a property on the associated node.
     *
     * @param key Property key
     * @param value Property value
     */
    protected void setProperty(String key, Object value) {
        Transaction tx = graphDb().beginTx();
        try {
            node.setProperty(key, value);
            tx.success();
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Gets a property on the associated node.
     *
     * @param key Property key
     */
    protected Object getProperty(String key) {
        Object o = null;
        Transaction tx = graphDb().beginTx();
        try {
            o = node.getProperty(key);
            tx.success();
        } finally {
            tx.finish();
        }
        return o;
    }
    
    /**
     * Create a relationship from this underlying node to another.
     */
    protected void addRelationshipTo(NodeWrapper otherNode, 
            RelationshipType relType) {
        Transaction tx = graphDb().beginTx();
        try {
            // nodes must not point to themselves
            if (!this.equals(otherNode)) {
                node.createRelationshipTo(otherNode.getNode(), relType);
                tx.success();
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * Create a relationship to this underlying node from another.
     */
    protected void addRelationshipFrom(NodeWrapper otherNode, 
            RelationshipType relType) {
        Transaction tx = graphDb().beginTx();
        try {
            // nodes must not point to themselves
            if (!this.equals(otherNode)) {
                otherNode.getNode().createRelationshipTo(node, relType);
                tx.success();
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * TODO
     */
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
        return o instanceof NodeWrapper && node.equals(((NodeWrapper)o).getNode());
    }

    @Override
    public String toString() {
        String string = node.toString() + "\n";  
        for (Map.Entry<String, Object> entry : getProperties().entrySet()) {
            string += entry.getKey() + " : " + entry.getValue() + "\n";
        }
        return string; 
    }
}
