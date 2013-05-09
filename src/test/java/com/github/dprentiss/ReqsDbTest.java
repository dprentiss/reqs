package com.github.dprentiss;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class ReqsDbTest extends TestCase {
    private ReqsDb testDb = new ReqsDb("target/testDb");

    public ReqsDbTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ReqsDbTest.class);
    }

    public void testDbService() {
        assertNotNull(testDb);
    }
    
    /*
    public void testAddStakeholder() {
       Node stakeholder = testDb.createNode();
       stakeholder.setProperty("name", "Mark Austin");
       Node concern = testDb.createNode();
       stakeholder.setProperty("concern", "The system must make use of graph-based data structures");
       testDb.addStakeholder(stakeholder, concern);
    }
    */

    public void tearDown() {
        testDb.shutDown();
    }
}
