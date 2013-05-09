package com.github.dprentiss;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ReqsDb {
    private final String STORE_DIR;
    public GraphDatabaseService graphDb;

    private static enum RelTypes implements RelationshipType {
        IDENTIFIES, SATIFIES
    }

    public ReqsDb(String dbPath) {
        STORE_DIR = dbPath;
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(STORE_DIR);
        registerShutdownHook(graphDb);
    }

    public Node createNode() {
        Transaction tx = graphDb.beginTx();
        Node newNode;
        try {
            newNode = graphDb.createNode();
            tx.success();
        } finally {
            tx.finish();
        }
        return newNode;
    }

    public void createTestNode() {
        Transaction tx = graphDb.beginTx();
        Node newNode;
        try {
            newNode = graphDb.createNode();
            newNode.setProperty("test", "test");
            tx.success();
        } finally {
            tx.finish();
        }
    }
    public void addStakeholder(Node stakeholder, Node concern) {
        Transaction tx = graphDb.beginTx();
        try {
            stakeholder.createRelationshipTo(concern, RelTypes.IDENTIFIES);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
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

    public static void main(String[] args) {
        ReqsDb testDb = new ReqsDb("target/testDb");
        testDb.createTestNode();
    }
}
