package com.github.dprentiss.reqs;

import org.neo4j.cypher.javacompat.ExecutionResult;

/**
 * Reqs is the the main class for the Reqs project.
 *
 * @author David Prentiss
 * @version %I%, %G%
 * @since 1.0
 */
public class Reqs {
    /**
     * Provides execution point for the Reqs project and loads or creates a test database.
     * Loads the test database "testDb" from the directory containing the
     * jar file or creates it.
     */
    public static void main(String[] args) {
        ReqsDb testDb = new ReqsDb("testDb");
        

        //Test
        ExecutionResult results; 
        testDb.addPrimaryEntity("Mark Austin");
        testDb.addPrimaryEntity("David Prentiss");
        testDb.addConcern("Student wants a good grade", "The student wants to recieve a grade of at least A- for the projct.");
        System.out.println(testDb.getPrimaryEntity("David Prentiss").getProperty("name"));
        System.out.println(testDb.getConcern("Student wants a good grade").getProperty("title"));
        testDb.addIdentifies(testDb.getPrimaryEntity("David Prentiss"), testDb.getConcern("Student wants a good grade"));
        results = testDb.cypher.execute("start n=node(*) return n");
        System.out.print(results.dumpToString());
    }
}
