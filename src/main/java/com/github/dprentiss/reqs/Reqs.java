package com.github.dprentiss.reqs;

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
        ReqsDb reqsDb = new ReqsDb(STORE_DIR);

        // populate a database for testing
        // TestDbFactory.create(reqsDb);

        // view the database
        ReqsView view1 = new ReqsView(reqsDb);
        view1.view();
        
        //Test
        System.out.println(
                "*** All nodes ***"
                );
        testQuery(reqsDb, 
                "start n=node(*) return n"
                );

        //Test
        System.out.println(
                "*** All primary entities ***"
                );
        testQuery(reqsDb, 
                "start n=node:node_auto_index(type=\"PRIMARY_ENTITY\") return n"
                );
        
        //Test
        System.out.println(
                "*** David Prentiss ***"
                );
        testQuery(reqsDb, 
                "start n=node:node_auto_index(name=\"David Prentiss\") return n"
                );
    }

    public static void testQuery(ReqsDb reqsDb, String testString) {
        ExecutionResult results; 
        results = reqsDb.cypher.execute(testString);
        System.out.println(testString);
        System.out.print(results.dumpToString());
        System.out.println();
    }
}
