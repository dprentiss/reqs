package com.github.dprentiss;

import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.index.Index;
//import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

public class ReqsDb {
    private final String STORE_DIR;
    public GraphDatabaseService graphDb;
    public ExecutionEngine cypher;
    // public IndexManager index;

    private static enum RelTypes implements RelationshipType {
        IDENTIFIES, SATIFIES
    }

    public ReqsDb(String dbPath) {
        STORE_DIR = dbPath;
        graphDb = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder(STORE_DIR).
            setConfig(GraphDatabaseSettings.node_keys_indexable, "name").
            setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
            newGraphDatabase();
        registerShutdownHook(graphDb);
        cypher = new ExecutionEngine(graphDb);
        //index = graphDb.index();
        //Index<Node> stakeholders = index.forNodes("name");
    }

    public Node getStakeholder(String name) {
        ExecutionResult result;
        Iterator<Node> nCol;
        Node stakeholder = null;
        result = cypher.execute("start n=node:node_auto_index(name = \"" + name + "\") return n");
        nCol = result.columnAs("n");
        if (nCol.hasNext()) {
            stakeholder = nCol.next();
        }
        return stakeholder;
    }

    public void addStakeholder(String name) {
        Transaction tx = graphDb.beginTx();
        Node newStakeholder;
        try {
            newStakeholder = graphDb.createNode();
            newStakeholder.setProperty("name", name);
            tx.success();
        } finally {
            tx.finish();
        }
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

    public void addIdentifes(Node stakeholder, Node concern) {
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
        String name = "test";
        ReqsDb testDb = new ReqsDb("target/testDb");
        testDb.addStakeholder("Mark Austin");
        testDb.addStakeholder("David Prentiss");
        testDb.addConcern("Student wants a good grade", "The student wants to recieve a grade of at least A- for the projct.");
        System.out.println(testDb.getStakeholder("David Prentiss").getProperty("name"));
    }
}
