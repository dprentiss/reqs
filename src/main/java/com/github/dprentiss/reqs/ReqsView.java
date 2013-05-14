package com.github.dprentiss.reqs;

import java.awt.Color;
import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;


/**
 * Provide a view for a Reqs database.
 *
 * @author David Prentiss
 */
public class ReqsView {
    private ReqsDb reqsDb;

    ReqsView(ReqsDb reqsDb) {
        this.reqsDb = reqsDb;
    }

    public void view() {
        // set up JFrame
        JFrame jf = new JFrame("ReqsView");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setBackground(Color.WHITE);

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
    }
}
