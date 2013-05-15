package com.github.dprentiss.reqs;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;

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
    private DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
    private PickedState<NodeWrapper> pickedState;

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
        vv = new VisualizationViewer(layout, new Dimension(1200, 800));
        pickedState = vv.getPickedVertexState();
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(
                new PickableVertexPaintTransformer<NodeWrapper>(
                    vv.getPickedVertexState(), Color.red, Color. yellow));
        vv.setGraphMouse(mouse);
        vv.setBackground(Color.white);
        
        mouse.setMode(ModalGraphMouse.Mode.PICKING);

        // load graph
        for (Relationship rel : reqsDb.getAllRelationships()) {
            if (rel.isType(ReqsDb.RelTypes.IDENTIFIES)) {
                graph.addEdge(rel, new Stakeholder(rel.getStartNode()),
                        new Concern(rel.getEndNode()));
            }
            if (rel.isType(ReqsDb.RelTypes.COVERS)) {
                    graph.addEdge(rel, new Viewpoint(rel.getStartNode()),
                                new Concern(rel.getEndNode()));
            }
            if (rel.isType(ReqsDb.RelTypes.IS_MEMBER)) {
                    graph.addEdge(rel, new PrimaryEntity(rel.getStartNode()),
                                new PrimaryEntity(rel.getEndNode()));
            }
        }

        // add all nodes with relationships to the graph
        /*
        for (Relationship rel : reqsDb.getAllRelationships()) {
            graph.addEdge(rel, rel.getStartNode(), rel.getEndNode());
        }
        */
        
        // add visualization to pane and show frame
        jf.getContentPane().add(vv);
        jf.pack();
        jf.setVisible(true);
    }
}
