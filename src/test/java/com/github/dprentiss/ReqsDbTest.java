package com.github.dprentiss;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ReqsDbTest extends TestCase {
    protected ReqsDb testDb = new ReqsDb("target/testDb");

    public ReqsDbTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ReqsDbTest.class);
    }

    public void testDbService() {
        assertNotNull(testDb);
    }

    public void tearDown() {
        testDb.shutDown();
    }
}
