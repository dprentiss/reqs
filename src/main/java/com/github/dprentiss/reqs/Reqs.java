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

        // populate a database for testing
        createTestDb(testDb);

        //Test
        ExecutionResult results; 
        results = testDb.cypher.execute("start n=node(*) return n");
        System.out.print(results.dumpToString());
    }

    public static void createTestDb(ReqsDb testDb) {
        // create some primary entities
        String[] names = {
            "Binyam Abeye",
            "Chris Binkley",
            "Sijia Cao",
            "Peter Linnehan",
            "Apurv Mittal",
            "Iris Mu",
            "Alan Nguyen",
            "David Prentiss",
            "Liang Qiao",
            "James Vaughn",
            "Hsi-Hsien Wei",
            "Mark Austin",
            "UMD",
            "ENCE688R",
            "ISR",
            "CEE",
            "Civil Systems",
            "Team A",
            "Team B"
        };

        for (int i = 0; i < names.length; i++) {
            PrimaryEntity newEntity = 
                new PrimaryEntity(testDb.createPrimaryEntity());
            newEntity.setName(names[i]);
        }
    }
}
