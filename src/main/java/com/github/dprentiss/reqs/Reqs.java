package com.github.dprentiss.reqs;

import org.neo4j.cypher.javacompat.ExecutionResult;

public class Reqs {
    public static void main(String[] args) {
        ExecutionResult results; 
        ReqsDb testDb = new ReqsDb("target/testDb");
        testDb.addStakeholder("Mark Austin");
        testDb.addStakeholder("David Prentiss");
        testDb.addConcern("Student wants a good grade", "The student wants to recieve a grade of at least A- for the projct.");
        System.out.println(testDb.getStakeholder("David Prentiss").getProperty("name"));
        System.out.println(testDb.getConcern("Student wants a good grade").getProperty("title"));
        testDb.addIdentifies(testDb.getStakeholder("David Prentiss"), testDb.getConcern("Student wants a good grade"));
        results = testDb.cypher.execute("start n=node(*) return n");
        System.out.print(results.dumpToString());
    }
}
