package com.github.dprentiss.reqs;

import java.awt.Color;
import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;


/**
 * Main class for the Reqs project.
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

        // set up JFrame
        JFrame jf = new JFrame("ReqsView");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setBackground(Color.WHITE);

        // check args for database path
        if(args.length > 0) {
            STORE_DIR = args[0];
        } else {
            STORE_DIR = TEST_DB;
        }

        // open database
        ReqsDb reqsDb = new ReqsDb(STORE_DIR);

        // populate a database for testing
        TestDbFactory.create(reqsDb);
        
        // initialize graph view
        Graph g = new DirectedSparseMultigraph();
        VisualizationViewer vv = new VisualizationViewer(new FRLayout(g));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        // add all nodes with relationships to the graph
        for (Relationship rel : reqsDb.getAllRelationships()) {
            g.addEdge(rel, rel.getStartNode(), rel.getEndNode());
    }
        jf.getContentPane().add(vv);
        jf.pack();
        jf.setVisible(true);

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
