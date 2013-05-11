package com.github.dprentiss.reqs;

import org.neo4j.cypher.javacompat.ExecutionResult;

/**
 * Main class for the Reqs project.
 *
 * @author David Prentiss
 */
public class Reqs {
    /**
     * Provides execution point for the Reqs project and loads or creates a test database.
     * Loads the test database "testDb" from the directory containing the
     * jar file or creates it.
     */
    public static void main(String[] args) {
        final String STORE_DIR;
        final String TEST_DB = "Project Databases/testDb";

        // check args for database path
        if(args.length > 0) {
            STORE_DIR = args[0];
        } else {
            STORE_DIR = TEST_DB;
        }

        // open database
        ReqsDb testDb = new ReqsDb(STORE_DIR);

        //Test
        ExecutionResult results; 
        testDb.addPrimaryEntity("Mark Austin");
        testDb.addPrimaryEntity("David Prentiss");
        testDb.addDocument("/project/ddp_concerns.txt", "Student wants a good grade");
        System.out.println(testDb.getPrimaryEntity("David Prentiss").getProperty("name"));
        System.out.println(testDb.getDocument("/project/ddp_concerns.txt").getProperty("URI"));
        testDb.addIdentifies(testDb.getPrimaryEntity("David Prentiss"), testDb.getDocument("/project/ddp_concerns.txt"));
        results = testDb.cypher.execute("start n=node(*) return n");
        System.out.print(results.dumpToString());
        Concern concern = new Concern(testDb.getDocument("/project/ddp_concerns.txt"));
        System.out.println(concern);
    }
}
