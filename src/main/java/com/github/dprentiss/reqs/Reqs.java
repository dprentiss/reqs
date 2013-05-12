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
        System.out.println(
                "*** All nodes ***"
                );
        testQuery(testDb, 
                "start n=node(*) return n"
                );

        //Test
        System.out.println(
                "*** All primary entities ***"
                );
        testQuery(testDb, 
                "start n=node:nodeIndexByType(type=\"PRIMARY_ENTITY\") return n"
                );
    }

    public static void testQuery(ReqsDb testDb, String testString) {
        ExecutionResult results; 
        results = testDb.cypher.execute(testString);
        System.out.println(testString);
        System.out.print(results.dumpToString());
        System.out.println();
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

        // create some documents
        String[] uris = {
            "ftp://example.org/concern01.txt",
            "ftp://example.org/concern02.txt",
            "ftp://example.org/concern03.txt",
            "ftp://example.org/concern04.txt",
            "ftp://example.org/concern05.txt",
            "http://example.org/concern01.txt",
            "http://example.org/concern02.txt",
            "http://example.org/concern03.txt",
            "http://example.org/concern04.txt",
            "http://example.org/concern05.txt",
            "ftp://umd.edu/concern01.txt",
            "ftp://umd.edu/concern02.txt",
            "ftp://umd.edu/concern03.txt",
            "ftp://umd.edu/concern04.txt",
            "ftp://umd.edu/concern05.txt",
            "http://umd.edu/concern01.txt",
            "http://umd.edu/concern02.txt",
            "http://umd.edu/concern03.txt",
            "http://umd.edu/concern04.txt",
            "http://umd.edu/concern05.txt",
            "ftp://example.org/project/concern01.txt",
            "ftp://example.org/project/concern02.txt",
            "ftp://example.org/project/concern03.txt",
            "ftp://example.org/project/concern04.txt",
            "ftp://example.org/project/concern05.txt",
            "http://example.org/project/concern01.txt",
            "http://example.org/project/concern02.txt",
            "http://example.org/project/concern03.txt",
            "http://example.org/project/concern04.txt",
            "http://example.org/project/concern05.txt",
            "ftp://umd.edu/project/concern01.txt",
            "ftp://umd.edu/project/concern02.txt",
            "ftp://umd.edu/project/concern03.txt",
            "ftp://umd.edu/project/concern04.txt",
            "ftp://umd.edu/project/concern05.txt",
            "http://umd.edu/project/concern01.txt",
            "http://umd.edu/project/concern02.txt",
            "http://umd.edu/project/concern03.txt",
            "http://umd.edu/project/concern04.txt",
            "http://umd.edu/project/concern05.txt",
            "ftp://example.org/project/viewpoint01.txt",
            "ftp://example.org/project/viewpoint02.txt",
            "ftp://example.org/project/viewpoint03.txt",
            "ftp://example.org/project/viewpoint04.txt",
            "ftp://example.org/project/viewpoint05.txt",
            "http://example.org/project/viewpoint01.txt",
            "http://example.org/project/viewpoint02.txt",
            "http://example.org/project/viewpoint03.txt",
            "http://example.org/project/viewpoint04.txt",
            "http://example.org/project/viewpoint05.txt",
            "ftp://umd.edu/project/viewpoint01.txt",
            "ftp://umd.edu/project/viewpoint02.txt",
            "ftp://umd.edu/project/viewpoint03.txt",
            "ftp://umd.edu/project/viewpoint04.txt",
            "ftp://umd.edu/project/viewpoint05.txt",
            "http://umd.edu/project/viewpoint01.txt",
            "http://umd.edu/project/viewpoint02.txt",
            "http://umd.edu/project/viewpoint03.txt",
            "http://umd.edu/project/viewpoint04.txt",
            "http://umd.edu/project/viewpoint05.txt"
        };

        for (int i = 0; i < 39; i++) {
            Document newDocument = 
                new Document(testDb.createDocument());
            newDocument.setURI(uris[i]);
            newDocument.setSummary("Lorem ipsum dolor sit amet.");
        }
    }
}
