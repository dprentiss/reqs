package com.github.dprentiss.reqs;

import java.io.File;
import java.io.IOException;
//import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
//import org.neo4j.cypher.javacompat.ExecutionResult;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Wraps a Neo4j database with methods appropriate for the requirements engineering domain.
 */
public class ReqsDb {
    private static final String NODE_TYPE_KEY = "type";
    private static final String PRIMARY_ENTITY_KEY = "name";
    private static final String DOCUMENT_KEY = "URI";
    private static final String AUTO_INDEX_KEYS
        = NODE_TYPE_KEY + ","
        + DOCUMENT_KEY + ","
        + PRIMARY_ENTITY_KEY;

    // declare database home
    private static String STORE_DIR;

    // declare database
    private static GraphDatabaseService graphDb;

    // declare object for cypher queries
    static ExecutionEngine cypher;

    // set to true to clear the database at STORE_DIR on startup 
    // FOR TESTING ONLY, ALL PREVIOUS DATA WILL BE LOST
    final boolean CLEAR_TEST_DB = false;

    ReadableIndex<Node> autoNodeIndex;

    /**
     * Possible relationships for a Reqs project.
     */
    public static enum RelTypes implements RelationshipType {
        IDENTIFIES, IS_MEMBER, COVERS
    }

    /**
     * Possible node types for a Reqs project.
     */
    public static enum NodeTypes implements NodeType {
        PRIMARY_ENTITY, DOCUMENT
    }

    /**
     * Initialize the database.
     * Creates an instance of {@link org.neo4j.kernel.EmbeddedGraphDatabase}.
     *
     * @param dbPath path to database directory
     */
    public ReqsDb(String dbPath) {
        STORE_DIR = dbPath;

        if (CLEAR_TEST_DB) {
            try {
                FileUtils.deleteRecursively(new File(STORE_DIR));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // embedded database
        graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder(STORE_DIR).
            setConfig(GraphDatabaseSettings.node_keys_indexable, 
                    AUTO_INDEX_KEYS).
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
            newGraphDatabase();
        
        // node indices
        autoNodeIndex = graphDb.index().getNodeAutoIndexer().getAutoIndex();

        // provide for clean database shutdown for all close events
        registerShutdownHook(graphDb);

        // allow for direct database queries with cypher
        cypher = new ExecutionEngine(graphDb);
    }

    public Iterable<Node> getAllNodes() {
        return GlobalGraphOperations.at(graphDb).getAllNodes();
    }

    public Iterable<Relationship> getAllRelationships() {
        return GlobalGraphOperations.at(graphDb).getAllRelationships();
    }

    /**
     * Add a new primary entity node
     */
    public Node createPrimaryEntity() {
        Node node;
        Transaction tx = graphDb.beginTx();
        try {
            node = graphDb.createNode();
            node.setProperty(NODE_TYPE_KEY, NodeTypes.PRIMARY_ENTITY.name());
            tx.success();
        } finally {
            tx.finish();
        }
        return node;
    }

    /**
     * Add a new document node
     */
    public Node createDocument() {
        Node node;
        Transaction tx = graphDb.beginTx();
        try {
            node = graphDb.createNode();
            node.setProperty(NODE_TYPE_KEY, NodeTypes.DOCUMENT.name());
            tx.success();
        } finally {
            tx.finish();
        }
        return node;
    }

    /**
     * Return a primary entity node
     */
    public Node getPrimaryEntity(String name) {
        Transaction tx = graphDb.beginTx();
        try {
        Node primaryEntity = autoNodeIndex.get(
                "name", name).getSingle(); 
        tx.success();
        return primaryEntity;
        } finally {
        tx.finish();
        }
    }

    /**
     * Return a documenth node
     */
    public Node getDocument(String URI) {
        Transaction tx = graphDb.beginTx();
        try {
        Node document = autoNodeIndex.get(DOCUMENT_KEY, URI).getSingle(); 
        tx.success();
        return document;
        } finally {
        tx.finish();
        }
    }

    /*
    public Iterable<PrimaryEntity> getAllPrimaryEntities() {
        return null;
    }

    public void addRelationship(Node from, Node to, RelationshipType relType) {
        Transaction tx = graphDb.beginTx();
        try {
            from.createRelationshipTo(to, relType);
            tx.success();
        } finally {
            tx.finish();
        }
    }
    */

    /**
     * Provides for clean database shutdown for all close events.
     */
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    /**
     * Shuts down the database.
     */
    void shutDown() {
        graphDb.shutdown();
    }
}
