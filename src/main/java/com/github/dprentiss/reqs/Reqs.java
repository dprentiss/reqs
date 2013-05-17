package com.github.dprentiss.reqs;

import java.io.File;

import org.neo4j.cypher.javacompat.ExecutionResult;

/**
 * Main class for the Reqs project.
 *
 * Reqs is a proof-of-concept application that demonstrates the ability of 
 * graph-based databases to model and query complex entity-relationship concepts.
 * In this case, an embedded database based on Neo4j is used to represent
 * Stakeholders, Concerns, and Viewpoints as described in IEEE 1471 standard
 * describing the architecture of a software-intensive system.
 *
 * The challenge presented by this task is to capture and effectively traverse
 * the many to many relationships between the three entities. For this initial
 * version of Reqs, the user is presented with a graph-based visual 
 * representation of the Stakeholders, Concerns, and Viewpoints in a
 * hypothetical architecture description. The user can graphically select one
 * or more entities and the system responds by highlighting the entities that
 * are related to it.
 *
 * @author David Prentiss
 */
public class Reqs {
    /**
     * Provides execution point for the Reqs project and loads or creates a test database.
     * Loads the test database "reqsDb" from the directory containing the
     * jar file or creates it.
     *
     * @param args Optional. Path to existing or new database.
     */
    public static void main(String[] args) {
        final String STORE_DIR;
        final String TEST_DB = "Project Databases/testDb";
        ReqsDb reqsDb;

        // check args for database path
        if(args.length > 0) {
            STORE_DIR = args[0];
        } else {
            STORE_DIR = TEST_DB;
        }

        // check for data base and populate testing
        File dir = new File(STORE_DIR);
        if (!dir.exists()) {
            System.out.println("Creating a test database at " + STORE_DIR);
            reqsDb = new ReqsDb(STORE_DIR);
            TestDbFactory.create(reqsDb);
        } else {
            System.out.println("Using database at " + STORE_DIR);
            reqsDb = new ReqsDb(STORE_DIR);
        }

        // view the database
        ReqsView view1 = new ReqsView(reqsDb);
        view1.view();
        
        /*
         * Examples of queries using Cypher. Uncomment to use.
         */

        /*
        //Query
        System.out.println(
                "*** All nodes ***"
                );
        testQuery(reqsDb, 
                "start n=node(*) return n"
                );

        //Query
        System.out.println(
                "*** All primary entities ***"
                );
        testQuery(reqsDb, 
                "start n=node:node_auto_index(type=\"PRIMARY_ENTITY\") return n"
                );
        
        //Query
        System.out.println(
                "*** David Prentiss ***"
                );
        testQuery(reqsDb, 
                "start n=node:node_auto_index(name=\"David Prentiss\") return n"
                );
        */
    }


    /**
     * Allows for queries to the database using Cypher. Although this feature 
     * is not used in this version, it is functional and available for testing 
     * and future use. For examples, uncomment the queries above. For more
     * information see {@see <a href="http://docs.neo4j.org/chunked/milestone/cypher-introduction.html">What is Cypher?</a>}.
     *
     * Dumps the query results to System.out but could be modified to return a
     * String.
     *
     * @param reqsDb The Reqs database to be queried
     * @param testString The Cypher query string
     */
    public static void testQuery(ReqsDb reqsDb, String testString) {
        ExecutionResult results; 
        results = reqsDb.cypher.execute(testString);
        System.out.println(testString);
        System.out.print(results.dumpToString());
        System.out.println();
    }
}
