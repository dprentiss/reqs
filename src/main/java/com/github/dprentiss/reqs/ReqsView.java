package com.github.dprentiss.reqs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
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
 * Provide a view and control interface for a Reqs database.
 *
 * @author David Prentiss
 */
public class ReqsView {
    private ReqsDb reqsDb;
    private Graph<NodeWrapper, Relationship> graph;
    private Dimension graphSize;
    private VisualizationModel<NodeWrapper, Relationship> vm;
    private VisualizationViewer<NodeWrapper, Relationship> vv;
    private AggregateLayout<NodeWrapper, Relationship> layout;
    private DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
    private PickedState<NodeWrapper> pickedState;
    private JFrame jf;
    private Map<Relationship, Paint> edgePaints;
    private Map<NodeWrapper, Paint> vertexPaints;

    ReqsView(ReqsDb reqsDb) {
        this.reqsDb = reqsDb;
    }

    public void view() {
        // set up JFrame
        jf = new JFrame("ReqsView");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialize graph view
        edgePaints = LazyMap.<Relationship, Paint>decorate(
                new HashMap<Relationship, Paint>(),
                new ConstantTransformer(Color.blue));
        vertexPaints = LazyMap.<NodeWrapper, Paint>decorate(
                new HashMap<NodeWrapper, Paint>(),
                new ConstantTransformer(Color.white));
        graph = new DirectedSparseGraph<NodeWrapper, Relationship>();
        graphSize = new Dimension(1200, 800);
        layout = new AggregateLayout<NodeWrapper, Relationship>(
                new FRLayout<NodeWrapper, Relationship>(graph));
        vm = new DefaultVisualizationModel<NodeWrapper, Relationship>(
                layout, graphSize);
        vv = new VisualizationViewer<NodeWrapper, Relationship>(vm, graphSize);
        vv.setGraphMouse(mouse);
        vv.setBackground(Color.white);

        vv.getPickedVertexState().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Object subject = e.getItem();
                if (subject instanceof NodeWrapper) {
                    NodeWrapper vertex = (NodeWrapper) subject;
                    if (vv.getPickedVertexState().isPicked(vertex)) {
                        for (Relationship rel : vertex.getRelationships()) {
                            edgePaints.put(rel, Color.black);
                        }
                    } else {
                        for (Relationship rel : graph.getEdges()) {;
                            edgePaints.put(rel, Color.lightGray);
                        }
                    }
                }
            }
        });

        // setup node painting
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(
                MapTransformer.<NodeWrapper, Paint>getInstance(vertexPaints));
        vv.getRenderContext().setVertexDrawPaintTransformer(
                new Transformer<NodeWrapper, Paint>() {
                    public Paint transform(NodeWrapper n) {
                        if (vv.getPickedVertexState().isPicked(n)) {
                            return Color.white;
                        } else {
                            return Color.black;
                        }
                    }
                });

        // setup edge painting
        vv.getRenderContext().setEdgeDrawPaintTransformer(
                MapTransformer.<Relationship, Paint>getInstance(edgePaints));
        vv.getRenderContext().setEdgeStrokeTransformer(
                new Transformer<Relationship, Stroke>() {
                    protected final Stroke THIN = new BasicStroke(1);
                    protected final Stroke THICK = new BasicStroke(2);
                    public Stroke transform(Relationship e) {
                        Paint c = edgePaints.get(e);
                        if (c == Color.lightGray) {
                            return THIN;
                        } else {
                            return THICK;
                        }
                    }
                });

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
            edgePaints.put(rel, Color.lightGray);
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
        Dimension dim = new Dimension(200, 700);
        center = new Point2D.Double(100, 400);
        cluster(stakeholders, layout, dim, center, Color.green);
        center = new Point2D.Double(500, 400);
        cluster(concerns, layout, dim, center, Color.red);
        center = new Point2D.Double(900, 400);
        cluster(viewpoints, layout, dim, center, Color.blue);
        vv.repaint();
    }

    private void cluster(Set<NodeWrapper> group, AggregateLayout<NodeWrapper, Relationship> layout, Dimension layoutSize, Point2D center, Color color) {

        Graph<NodeWrapper, Relationship> subGraph =
            DirectedSparseGraph
            .<NodeWrapper, Relationship>getFactory()
            .create();

        for(NodeWrapper n : group) {
            subGraph.addVertex(n);
            vertexPaints.put(n, color);
        }
        System.out.print(subGraph.toString());
        Layout<NodeWrapper, Relationship> subLayout =
            new ISOMLayout<NodeWrapper, Relationship>(subGraph);
        subLayout.setInitializer(vv.getGraphLayout());
        subLayout.setSize(layoutSize);
        layout.put(subLayout, center);
        vv.setGraphLayout(layout);
        vv.repaint();
    }
}
