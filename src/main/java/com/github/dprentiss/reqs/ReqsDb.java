package com.github.dprentiss.reqs;

import java.io.File;
import java.io.IOException;
//import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
//import org.neo4j.cypher.javacompat.ExecutionResult;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.impl.util.FileUtils;

public class ReqsDb {
    private final String STORE_DIR;
    private static GraphDatabaseService graphDb;
    private static String NODE_TYPE_KEY = "type";
    private static Index<Node> concerns;
    private static Index<Node> documents;
    private static final String DOCUMENT_KEY = "URI";
    private static Index<Node> people;
    private static Index<Node> primaryEntities;
    private static final String PRIMARY_ENTITY_KEY = "name";
    private static Index<Node> stakeholders;
    private static Index<Node> viewpoints;
    private static Index<Node> nodeIndexByType;
    static ExecutionEngine cypher;

    // set to true to clear the database at STORE_DIR on startup 
    // FOR TESTING ONLY, ALL PREVIOUS DATA WILL BE LOST
    final boolean CLEAR_TEST_DB = true;

    /**
     * Possible relationships for a Reqs project.
     */
    private static enum RelTypes implements RelationshipType {
        IDENTIFIES, MEMBER
    }

    /**
     * Possible node types for a Reqs project.
     */
    private static enum NodeTypes implements NodeType {
        PRIMARY_ENTITY, DOCUMENT
    }

    /**
     * Initialize the database.
     * Creates an instance of {@link EmbeddedGraphDatabase}.
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
            newEmbeddedDatabase(STORE_DIR);
        
        // node indices
        concerns = graphDb.index().forNodes("concerns");
        // documents = graphDb.index().forNodes("documents");
        stakeholders = graphDb.index().forNodes("stakeholders");
        people = graphDb.index().forNodes("people");
        // primaryEntities = graphDb.index().forNodes("primaryEntities");
        viewpoints = graphDb.index().forNodes("viewpoints");
        nodeIndexByType = graphDb.index().forNodes("nodeIndexByType");

        // provide for clean database shutdown for all close events
        registerShutdownHook(graphDb);

        // allow for direct database queries with cyper
        cypher = new ExecutionEngine(graphDb);
    }

    public Node createPrimaryEntity() {
        Node node;
        Transaction tx = graphDb.beginTx();
        try {
            node = graphDb.createNode();
            node.setProperty(NODE_TYPE_KEY, NodeTypes.PRIMARY_ENTITY.name());
            nodeIndexByType.add(node, NODE_TYPE_KEY, NodeTypes.PRIMARY_ENTITY.name());
            tx.success();
        } finally {
            tx.finish();
        }
        return node;
    }

    public Node createDocument() {
        Node node;
        Transaction tx = graphDb.beginTx();
        try {
            node = graphDb.createNode();
            node.setProperty(NODE_TYPE_KEY, NodeTypes.DOCUMENT);
            nodeIndexByType.add(node, NODE_TYPE_KEY, NodeTypes.DOCUMENT);
            tx.success();
        } finally {
            tx.finish();
        }
        return node;
    }

    public Node getPrimaryEntity(String name) {
        Transaction tx = graphDb.beginTx();
        try {
        Node primaryEntity = primaryEntities.get(
                "name", name).getSingle(); 
        tx.success();
        return primaryEntity;
        } finally {
        tx.finish();
        }
    }

    public Node getDocument(String URI) {
        Transaction tx = graphDb.beginTx();
        try {
        Node document = documents.get(DOCUMENT_KEY, URI).getSingle(); 
        tx.success();
        return document;
        } finally {
        tx.finish();
        }
    }

    public void addIdentifies(Node stakeholder, Node concern) {
        Transaction tx = graphDb.beginTx();
        try {
            stakeholder.createRelationshipTo(concern, RelTypes.IDENTIFIES);
            stakeholders.putIfAbsent(stakeholder, 
                    PRIMARY_ENTITY_KEY, stakeholder.getProperty(PRIMARY_ENTITY_KEY));
            concerns.putIfAbsent(concern, 
                    DOCUMENT_KEY, concern.getProperty(DOCUMENT_KEY));
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public void addMemeber(Node member, Node organization) {
        Transaction tx = graphDb.beginTx();
        try {
            member.createRelationshipTo(organization, RelTypes.MEMBER);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Provides for clean database shutdown for all close events.
     */
    private static void registerShutdownHook(
            final GraphDatabaseService graphDb) {
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
