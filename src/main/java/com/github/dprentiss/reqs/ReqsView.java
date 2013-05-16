package com.github.dprentiss.reqs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
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
    private Dimension graphSize;
    private VisualizationModel<NodeWrapper, Relationship> vm;
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
        graphSize = new Dimension(1200, 800);
        layout = new AggregateLayout<NodeWrapper, Relationship>(
                new FRLayout<NodeWrapper, Relationship>(graph));
        vm = new DefaultVisualizationModel<NodeWrapper, Relationship>(
                layout, graphSize);
        vv = new VisualizationViewer<NodeWrapper, Relationship>(vm, graphSize);
        pickedState = vv.getPickedVertexState();

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(
                new PickableVertexPaintTransformer<NodeWrapper>(
                    vv.getPickedVertexState(), Color.red, Color. yellow));
        vv.setGraphMouse(mouse);
        vv.setBackground(Color.white);
        
        // add visualization to pane and show frame
        jf.getContentPane().add(vv);
        jf.pack();
        jf.setVisible(true);

        mouse.setMode(ModalGraphMouse.Mode.PICKING);

        // load graph
        Set<NodeWrapper> stakeholders = new HashSet<NodeWrapper>();
        NodeWrapper stakeholder;
        Set<NodeWrapper> concerns = new HashSet<NodeWrapper>();
        NodeWrapper concern;
        Set<NodeWrapper> viewpoints = new HashSet<NodeWrapper>();
        NodeWrapper viewpoint;
        for (Relationship rel : reqsDb.getAllRelationships()) {
            if (rel.isType(ReqsDb.RelTypes.IDENTIFIES)) {
                stakeholder = new Stakeholder(rel.getStartNode());
                concern = new Concern(rel.getEndNode());
                graph.addEdge(rel, stakeholder, concern);
                stakeholders.add(stakeholder);
                concerns.add(concern);
            }
            if (rel.isType(ReqsDb.RelTypes.COVERS)) {
                viewpoint = new Viewpoint(rel.getStartNode());
                graph.addEdge(rel, viewpoint,
                        new Concern(rel.getEndNode()));
                viewpoints.add(viewpoint);
            }
            if (rel.isType(ReqsDb.RelTypes.IS_MEMBER)) {
                graph.addEdge(rel, new PrimaryEntity(rel.getStartNode()),
                        new PrimaryEntity(rel.getEndNode()));
            }
        }
        
        Point2D center;
        Dimension dim = new Dimension(200, 800);
        center = new Point2D.Double(200, 400);
        cluster(stakeholders, layout, dim, center);
        center = new Point2D.Double(600, 400);
        cluster(concerns, layout, dim, center);
        center = new Point2D.Double(1000, 400);
        cluster(viewpoints, layout, dim, center);
    }

    private void cluster(Set<NodeWrapper> group, AggregateLayout<NodeWrapper, Relationship> layout, Dimension layoutSize, Point2D center) {

        Graph<NodeWrapper, Relationship> subGraph =
            DirectedSparseMultigraph
            .<NodeWrapper, Relationship>getFactory()
            .create();

        for(NodeWrapper n : group) {
            subGraph.addVertex(n);
        }
        System.out.print(subGraph.toString());
        Layout<NodeWrapper, Relationship> subLayout =
            new ISOMLayout<NodeWrapper, Relationship>(subGraph);
        subLayout.setInitializer(vv.getGraphLayout());
        subLayout.setSize(layoutSize);
        layout.put(subLayout, center);
        vv.setGraphLayout(layout);
    }
}
