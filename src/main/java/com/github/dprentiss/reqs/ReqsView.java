package com.github.dprentiss.reqs;

import java.awt.Color;
import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Node;

/**
 * Provide a view for a Reqs database.
 *
 * @author David Prentiss
 */
public class ReqsView {
    private ReqsDb reqsDb;
    private Graph graph;
    private VisualizationViewer<NodeWrapper, Relationship> vv;
    private AggregateLayout<NodeWrapper, Relationship> layout;

    ReqsView(ReqsDb reqsDb) {
        this.reqsDb = reqsDb;
    }

    public void view() {
        // set up JFrame
        JFrame jf = new JFrame("ReqsView");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jf.setBackground(Color.WHITE);

        // initialize graph view
        graph = new DirectedSparseMultigraph();
        layout = new AggregateLayout<NodeWrapper, Relationship>(
                new FRLayout<NodeWrapper, Relationship>(graph));
        vv = new VisualizationViewer(layout);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.setBackground(Color.white);

        for (Relationship rel : reqsDb.getAllRelationships()) {
            if (rel.isType(ReqsDb.RelTypes.IDENTIFIES)) {
                graph.addEdge(rel, new Stakeholder(rel.getStartNode()),
                        new Concern(rel.getEndNode()));
            }
        }
        /*
        // add all nodes with relationships to the graph
        for (Relationship rel : reqsDb.getAllRelationships()) {
            graph.addEdge(rel, rel.getStartNode(), rel.getEndNode());
        }
        */
        
        /*
        // add all nodes with relationships to the graph
        for (Relationship rel : reqsDb.getAllRelationships()) {
            switch (rel.getType()) {
                case reqsDb.RelTypes.IDENTIFIES:
                    graph.addEdge(rel, new Stakeholder(rel.getStartNode()),
                                new Concern(rel.getEndNode()));
                case reqsDb.RelTypes.IS_MEMBER:
                    graph.addEdge(rel, new Stakeholder(rel.getStartNode()),
                                new Concern(rel.getEndNode()));
            }
        }
        */

        // add visualazation to pane and show frame
        jf.getContentPane().add(vv);
        jf.pack();
        jf.setVisible(true);
    }
}
