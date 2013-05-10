package com.github.dprentiss.reqs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.impl.util.FileUtils;

public class ReqsDb {
    private final String STORE_DIR;
    public GraphDatabaseService graphDb;
    public ExecutionEngine cypher;

    // set to true to clear the database at STORE_DIR at startup 
    // FOR TESTING ONLY, ALL PREVIOUS DATA WILL BE LOST
    final boolean CLEAR_TEST_DB = false;

    private static enum RelTypes implements RelationshipType {
        IDENTIFIES, SATIFIES
    }

    public ReqsDb(String dbPath) {
        STORE_DIR = dbPath;

        if (CLEAR_TEST_DB) {
            try {
                FileUtils.deleteRecursively(new File(STORE_DIR));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder(STORE_DIR).
            setConfig(GraphDatabaseSettings.node_keys_indexable, "name, title").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
            newGraphDatabase();
        registerShutdownHook(graphDb);
        cypher = new ExecutionEngine(graphDb);
    }

    public void addPrimaryEntity(String name) {
        Transaction tx = graphDb.beginTx();
        Node entity;
        try {
            entity = graphDb.createNode();
            entity.setProperty("name", name);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public Node getPrimaryEntity(String name) {
        ExecutionResult result;
        Iterator<Node> nCol;
        Node entity = null;
        result = cypher.execute(
                "start n=node:node_auto_index(name = \"" + name + "\") return n");
        nCol = result.columnAs("n");
        if (nCol.hasNext()) {
            entity = nCol.next();
        }
        return entity;
    }

    public Node getConcern(String title) {
        ExecutionResult result;
        Iterator<Node> nCol;
        Node concern = null;
        result = cypher.execute(
                "start n=node:node_auto_index(title = \"" + title + "\") return n");
        nCol = result.columnAs("n");
        if (nCol.hasNext()) {
            concern = nCol.next();
        }
        return concern;
    }

    public void addIdentifies(Node stakeholder, Node concern) {
        Transaction tx = graphDb.beginTx();
        try {
            stakeholder.createRelationshipTo(concern, RelTypes.IDENTIFIES);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public void addConcern(String title, String body) {
        Transaction tx = graphDb.beginTx();
        Node newNode;
        try {
            newNode = graphDb.createNode();
            newNode.setProperty("title", title);
            newNode.setProperty("body", body);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    private static void registerShutdownHook(
            final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
            }

    void shutDown() {
        graphDb.shutdown();
    }
}
