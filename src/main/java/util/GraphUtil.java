// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.types.board.SiteType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.*;
import view.container.BaseContainerStyle;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GraphUtil
{
    public static List<Edge> orthogonalCellConnections(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Cell va : topology.cells()) {
            final Point2D drawPosnA = va.centroid();
            for (final Cell vb : va.orthogonal()) {
                if (vb.index() > va.index()) {
                    final Point2D drawPosnB = vb.centroid();
                    connections.add(new Edge(new Vertex(-1, drawPosnA.getX(), drawPosnA.getY(), 0.0), new Vertex(-1, drawPosnB.getX(), drawPosnB.getY(), 0.0)));
                }
            }
        }
        return connections;
    }
    
    public static List<Edge> diagonalCellConnections(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Cell va : topology.cells()) {
            final Point2D drawPosnA = va.centroid();
            for (final Cell vb : va.diagonal()) {
                if (vb.index() > va.index()) {
                    final Point2D drawPosnB = vb.centroid();
                    connections.add(new Edge(new Vertex(-1, drawPosnA.getX(), drawPosnA.getY(), 0.0), new Vertex(-1, drawPosnB.getX(), drawPosnB.getY(), 0.0)));
                }
            }
        }
        return connections;
    }
    
    public static List<Edge> offCellConnections(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Cell va : topology.cells()) {
            final Point2D drawPosnA = va.centroid();
            for (final Cell vb : va.off()) {
                if (vb.index() > va.index()) {
                    final Point2D drawPosnB = vb.centroid();
                    connections.add(new Edge(new Vertex(-1, drawPosnA.getX(), drawPosnA.getY(), 0.0), new Vertex(-1, drawPosnB.getX(), drawPosnB.getY(), 0.0)));
                }
            }
        }
        return connections;
    }
    
    public static List<Edge> orthogonalEdgeRelations(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Vertex va : topology.vertices()) {
            final Point2D drawPosnA = va.centroid();
            for (final Vertex vb : va.orthogonal()) {
                if (vb.index() > va.index()) {
                    final Point2D drawPosnB = vb.centroid();
                    connections.add(new Edge(new Vertex(-1, drawPosnA.getX(), drawPosnA.getY(), 0.0), new Vertex(-1, drawPosnB.getX(), drawPosnB.getY(), 0.0)));
                }
            }
        }
        return connections;
    }
    
    public static List<Edge> diagonalEdgeRelations(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Vertex va : topology.vertices()) {
            final Point2D drawPosnA = va.centroid();
            for (final Vertex vb : va.diagonal()) {
                if (vb.index() > va.index()) {
                    final Point2D drawPosnB = vb.centroid();
                    connections.add(new Edge(new Vertex(va.index(), drawPosnA.getX(), drawPosnA.getY(), 0.0), new Vertex(vb.index(), drawPosnB.getX(), drawPosnB.getY(), 0.0)));
                }
            }
        }
        return connections;
    }
    
    public static List<Edge> innerEdgeRelations(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Edge edge : topology.edges()) {
            if (!edge.properties().get(2L)) {
                connections.add(edge);
            }
        }
        return connections;
    }
    
    public static List<Edge> outerEdgeRelations(final Topology topology) {
        final List<Edge> connections = new ArrayList<>();
        for (final Edge edge : topology.edges()) {
            if (edge.properties().get(2L)) {
                connections.add(edge);
            }
        }
        return connections;
    }
    
    public static String createSVGGraphImage(final BaseContainerStyle boardStyle) {
        final SVGGraphics2D g2d = boardStyle.setSVGRenderingValues();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.setColor(new Color(120, 120, 120));
        final Stroke solid = new BasicStroke(2.0f, 1, 1);
        final Stroke dashed = new BasicStroke(2.0f, 0, 1, 0.0f, new float[] { 10.0f }, 0.0f);
        final Stroke dotted = new BasicStroke(2.0f, 0, 1, 0.0f, new float[] { 3.0f }, 0.0f);
        g2d.setStroke(solid);
        for (final Edge e : boardStyle.drawnEdges()) {
            final Point drawPosnA = boardStyle.screenPosn(e.vA().centroid());
            final Point drawPosnB = boardStyle.screenPosn(e.vB().centroid());
            final Shape line = new Line2D.Double(drawPosnA.x, drawPosnA.y, drawPosnB.x, drawPosnB.y);
            g2d.draw(line);
        }
        g2d.setStroke(dashed);
        for (final Vertex vA : boardStyle.drawnVertices()) {
            for (final Vertex vB : vA.diagonal()) {
                if (vA.index() > vB.index()) {
                    final Point drawPosnA2 = boardStyle.screenPosn(vA.centroid());
                    final Point drawPosnB2 = boardStyle.screenPosn(vB.centroid());
                    final Shape line2 = new Line2D.Double(drawPosnA2.x, drawPosnA2.y, drawPosnB2.x, drawPosnB2.y);
                    g2d.draw(line2);
                }
            }
        }
        g2d.setStroke(dotted);
        for (final Vertex vA : boardStyle.drawnVertices()) {
            for (final Vertex vB : vA.off()) {
                if (vA.index() > vB.index()) {
                    final Point drawPosnA2 = boardStyle.screenPosn(vA.centroid());
                    final Point drawPosnB2 = boardStyle.screenPosn(vB.centroid());
                    final Shape line2 = new Line2D.Double(drawPosnA2.x, drawPosnA2.y, drawPosnB2.x, drawPosnB2.y);
                    g2d.draw(line2);
                }
            }
        }
        g2d.setStroke(solid);
        for (final Vertex va : boardStyle.drawnVertices()) {
            final int r = 4;
            final Point drawPosn = boardStyle.screenPosn(va.centroid());
            g2d.fillArc(drawPosn.x - 4, drawPosn.y - 4, 9, 9, 0, 360);
        }
        return g2d.getSVGDocument();
    }
    
    public static String createSVGConnectionsImage(final BaseContainerStyle boardStyle) {
        final SVGGraphics2D g2d = boardStyle.setSVGRenderingValues();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.setColor(new Color(127, 127, 255));
        final Stroke solid = new BasicStroke(2.0f, 1, 1);
        final Stroke dashed = new BasicStroke(2.0f, 0, 1, 0.0f, new float[] { 10.0f }, 0.0f);
        final Stroke dotted = new BasicStroke(2.0f, 0, 1, 0.0f, new float[] { 3.0f }, 0.0f);
        g2d.setStroke(solid);
        for (final Cell vA : boardStyle.drawnCells()) {
            if (vA.centroid().getX() >= 0.0) {
                if (vA.centroid().getY() < 0.0) {
                    continue;
                }
                for (final Cell vB : vA.orthogonal()) {
                    final Cell vBDrawn = boardStyle.drawnCells().get(vB.index());
                    if (vBDrawn.centroid().getX() >= 0.0) {
                        if (vBDrawn.centroid().getY() < 0.0) {
                            continue;
                        }
                        if (vA.index() <= vBDrawn.index()) {
                            continue;
                        }
                        final Point drawPosnA = boardStyle.screenPosn(vA.centroid());
                        final Point drawPosnB = boardStyle.screenPosn(vBDrawn.centroid());
                        final Shape line = new Line2D.Double(drawPosnA.x, drawPosnA.y, drawPosnB.x, drawPosnB.y);
                        g2d.draw(line);
                    }
                }
            }
        }
        g2d.setStroke(dashed);
        for (final Cell vA : boardStyle.drawnCells()) {
            if (vA.centroid().getX() >= 0.0) {
                if (vA.centroid().getY() < 0.0) {
                    continue;
                }
                for (final Cell vB : vA.diagonal()) {
                    final Cell vBDrawn = boardStyle.drawnCells().get(vB.index());
                    if (vBDrawn.centroid().getX() >= 0.0) {
                        if (vBDrawn.centroid().getY() < 0.0) {
                            continue;
                        }
                        if (vA.index() <= vBDrawn.index()) {
                            continue;
                        }
                        final Point drawPosnA = boardStyle.screenPosn(vA.centroid());
                        final Point drawPosnB = boardStyle.screenPosn(vBDrawn.centroid());
                        final Shape line = new Line2D.Double(drawPosnA.x, drawPosnA.y, drawPosnB.x, drawPosnB.y);
                        g2d.draw(line);
                    }
                }
            }
        }
        g2d.setStroke(dotted);
        for (final Cell vA : boardStyle.drawnCells()) {
            if (vA.centroid().getX() >= 0.0) {
                if (vA.centroid().getY() < 0.0) {
                    continue;
                }
                for (final Cell vB : vA.off()) {
                    final Cell vBDrawn = boardStyle.drawnCells().get(vB.index());
                    if (vBDrawn.centroid().getX() >= 0.0) {
                        if (vBDrawn.centroid().getY() < 0.0) {
                            continue;
                        }
                        if (vA.index() <= vBDrawn.index()) {
                            continue;
                        }
                        final Point drawPosnA = boardStyle.screenPosn(vA.centroid());
                        final Point drawPosnB = boardStyle.screenPosn(vBDrawn.centroid());
                        final Shape line = new Line2D.Double(drawPosnA.x, drawPosnA.y, drawPosnB.x, drawPosnB.y);
                        g2d.draw(line);
                    }
                }
            }
        }
        g2d.setStroke(solid);
        for (final Cell vA : boardStyle.drawnCells()) {
            if (vA.centroid().getX() >= 0.0) {
                if (vA.centroid().getY() < 0.0) {
                    continue;
                }
                final int r = 4;
                final Point drawPosn = boardStyle.screenPosn(vA.centroid());
                g2d.fillArc(drawPosn.x - 4, drawPosn.y - 4, 9, 9, 0, 360);
            }
        }
        return g2d.getSVGDocument();
    }
    
    public static List<TopologyElement> reorderGraphElementsTopDown(final List<TopologyElement> allGraphElements, final Context context) {
        if (context.game().isStacking()) {
            Collections.sort(allGraphElements, Comparator.comparingDouble(o -> o.centroid().getY()));
            Collections.reverse(allGraphElements);
        }
        else if (context.board().topology().layers(SiteType.Vertex).size() > 1) {
            Collections.sort(allGraphElements, Comparator.comparingInt(TopologyElement::layer));
        }
        return allGraphElements;
    }
    
    public static double calculateCellRadius(final Cell cell) {
        double acc = 0.0;
        final List<Edge> ee = cell.edges();
        final int n = ee.size();
        if (n > 0) {
            for (final Edge edge : ee) {
                final Point2D midpoint = edge.centroid();
                final Point2D cc = cell.centroid();
                final double dx = midpoint.getX() - cc.getX();
                final double dy = midpoint.getY() - cc.getY();
                final double dist = Math.sqrt(dx * dx + dy * dy);
                acc += dist;
            }
            acc /= n;
        }
        return acc;
    }
}
