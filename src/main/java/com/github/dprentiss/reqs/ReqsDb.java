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
    private static Index<Node> concerns;
    private static Index<Node> documents;
    private static final String DOCUMENT_KEY = "URI";
    private static Index<Node> primaryEntities;
    private static final String PRIMARY_ENTITY_KEY = "name";
    private static Index<Node> stakeholders;
    private static Index<Node> viewpoints;
    static ExecutionEngine cypher;

    // set to true to clear the database at STORE_DIR at startup 
    // FOR TESTING ONLY, ALL PREVIOUS DATA WILL BE LOST
    final boolean CLEAR_TEST_DB = false;

    /**
     * Possible relationships for a Reqs project.
     */
    private static enum RelTypes implements RelationshipType {
        IDENTIFIES, MEMBER
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
        documents = graphDb.index().forNodes("documents");
        stakeholders = graphDb.index().forNodes("stakeholders");
        primaryEntities = graphDb.index().forNodes("primaryEntities");
        viewpoints = graphDb.index().forNodes("viewpoints");

        // provide for clean database shutdown for all close events
        registerShutdownHook(graphDb);

        // allow for direct database queries with cyper
        cypher = new ExecutionEngine(graphDb);
    }

    public void addPrimaryEntity(String name) {
        Transaction tx = graphDb.beginTx();
        try {
            IndexHits hits = primaryEntities.get(PRIMARY_ENTITY_KEY, name);
            if (hits.hasNext()) {
                System.out.println("Duplicate node ignored");
            } else {
                Node primaryEntity = graphDb.createNode();
                primaryEntity.setProperty(PRIMARY_ENTITY_KEY, name);
                primaryEntities.add(primaryEntity, PRIMARY_ENTITY_KEY, name);
                tx.success();
            }
        } finally {
            tx.finish();
        }
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

    public void addDocument(String URI, String summary) {
        Transaction tx = graphDb.beginTx();
        try {
            IndexHits hits = documents.get(DOCUMENT_KEY, URI);
            if (hits.hasNext()) {
                System.out.println("Duplicate document node ignored");
            } else {
            Node document = graphDb.createNode();
            document.setProperty(DOCUMENT_KEY, URI);
            document.setProperty("summary", summary);
            documents.add(document, DOCUMENT_KEY, URI);
            tx.success();
            }
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
