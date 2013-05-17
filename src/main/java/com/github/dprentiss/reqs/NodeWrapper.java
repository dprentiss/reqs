package com.github.dprentiss.reqs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;


/**
 * Parent class for wrapping a {@link Node} retrieved from a Reqs database.
 * 
 * This class provides the domain specificity for all graph database objects.
 * Each instance contians a reference to a single underlying node and provides
 * methods that allow that node to represent the domain specific entities
 * present in the model.
 *
 * This class also isolates domain entities from the details of database
 * transactions. That is, the database should be transparent to extentions of
 * this class.
 *
 * TODO Add other relationship Traversals
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
     *
     * @param otherNode The node to create a realationship with.
     * @param relType The type of relation to create.
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
     *
     * @param otherNode The node to create a realationship with.
     * @param relType The type of relation to create.
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
     * Get domain specific relationships of all entities connected to an 
     * entitiy in question.
     * 
     * This method is domain specific in that it returns nodes that are related
     * to each other in ways that are meaningful to the archetecture description.
     * For example, this method called for a Viewpoint returns the associated 
     * concerns and stakeholders but not other Viewpoints, though they are
     * connected on the graph.
     *
     * For more information on implementation of Traversals see
     * {@see <a href="http://docs.neo4j.org/chunked/milestone/tutorial-traversal.html">The Traversal Framework</a>}
     *
     * @return All entinties related to this one in a domain specifically
     * meaningfull way.
     */
    public Iterable<Relationship> getRelationships() {
        TraversalDescription traversal = Traversal.description()
            .breadthFirst()
            .evaluator(new Evaluator() {
                @Override
                public Evaluation evaluate(final Path path) {
                    if (path.length() == 0) {
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                    boolean isOutgoingIS_MEMBER = (
                        path.lastRelationship().getEndNode() == 
                        path.endNode() &&
                        (path.lastRelationship()
                        .isType(ReqsDb.RelTypes.IS_MEMBER)));
                    boolean isRelTypeUnique = true;
                    Iterator<Relationship> i = 
                path.reverseRelationships().iterator();
                    i.next();
                    while (i.hasNext()) {
                        if (i.next().isType(path.lastRelationship()
                                .getType())) {
                            isRelTypeUnique = false;
                            break;
                        }
                    }
                    boolean included = isOutgoingIS_MEMBER || isRelTypeUnique;
                    boolean continued = included;
                    return Evaluation.of(included, continued);
                }
            });
                
        return traversal.traverse(node).relationships();
    }

    /**
     * Returns the {@GraphDatabaseService} that contians the node.
     */
    private GraphDatabaseService graphDb() {
        return node.getGraphDatabase();
    }

    /**
     * @return The unique database ID of the associated node.
     */
    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NodeWrapper 
            && node.equals(((NodeWrapper)o).getNode());
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
