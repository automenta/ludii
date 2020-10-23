// 
// Decompiled by Procyon v0.5.36
// 

package util;

import bridge.Bridge;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.equipment.other.Regions;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import metadata.graphics.util.MetadataImageInfo;
import metadata.graphics.util.PieceStackType;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.TopologyElement;
import view.container.styles.BoardStyle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ContainerUtil
{
    public static int getContainerSite(final Context context, final int site, final SiteType graphElementType) {
        if (site == -1) {
            return -1;
        }
        if (graphElementType == SiteType.Cell) {
            final int contianerId = getContainerId(context, site, graphElementType);
            final int containerSite = site - context.sitesFrom()[contianerId];
            return containerSite;
        }
        return site;
    }
    
    public static int getContainerId(final Context context, final int site, final SiteType graphElementType) {
        if (site == -1) {
            return -1;
        }
        if (graphElementType != SiteType.Cell) {
            return context.board().index();
        }
        return context.containerId()[site];
    }
    
    public static void normaliseGraphElements(final Topology graph) {
        double minX = 9999999.0;
        double minY = 9.9999999E7;
        double maxX = -9.9999999E7;
        double maxY = -9.9999999E7;
        for (int i = 0; i < graph.vertices().size(); ++i) {
            if (graph.vertices().get(i).centroid().getX() < minX) {
                minX = graph.vertices().get(i).centroid().getX();
            }
            if (graph.vertices().get(i).centroid().getY() < minY) {
                minY = graph.vertices().get(i).centroid().getY();
            }
            if (graph.vertices().get(i).centroid().getX() > maxX) {
                maxX = graph.vertices().get(i).centroid().getX();
            }
            if (graph.vertices().get(i).centroid().getY() > maxY) {
                maxY = graph.vertices().get(i).centroid().getY();
            }
        }
        if (maxX - minX > maxY - minY) {
            normaliseGraphElementsBetween((ArrayList)graph.vertices(), minX, maxX);
        }
        else {
            normaliseGraphElementsBetween((ArrayList)graph.vertices(), minY, maxY);
        }
        for (final Edge edge : graph.edges()) {
            final double centroidX = (edge.vA().centroid().getX() + edge.vB().centroid().getX()) / 2.0;
            final double centroidY = (edge.vA().centroid().getY() + edge.vB().centroid().getY()) / 2.0;
            edge.setCentroid(centroidX, centroidY, 0.0);
        }
        for (final Cell cell : graph.cells()) {
            double centroidX = 0.0;
            for (int j = 0; j < cell.vertices().size(); ++j) {
                centroidX += cell.vertices().get(j).centroid().getX();
            }
            centroidX /= cell.vertices().size();
            double centroidY = 0.0;
            for (int k = 0; k < cell.vertices().size(); ++k) {
                centroidY += cell.vertices().get(k).centroid().getY();
            }
            centroidY /= cell.vertices().size();
            cell.setCentroid(centroidX, centroidY, 0.0);
        }
    }
    
    private static void normaliseGraphElementsBetween(final ArrayList<? extends TopologyElement> graphElements, final double min, final double max) {
        for (TopologyElement graphElement : graphElements) {
            final double oldX = graphElement.centroid().getX();
            final double oldY = graphElement.centroid().getY();
            final double newX = (oldX - min) / (max - min);
            final double newY = (oldY - min) / (max - min);
            graphElement.setCentroid(newX, newY, 0.0);
        }
    }
    
    public static void centerGraphElements(final Topology graph) {
        double minX = 9999999.0;
        double minY = 9.9999999E7;
        double maxX = -9.9999999E7;
        double maxY = -9.9999999E7;
        for (int i = 0; i < graph.vertices().size(); ++i) {
            if (graph.vertices().get(i).centroid().getX() < minX) {
                minX = graph.vertices().get(i).centroid().getX();
            }
            if (graph.vertices().get(i).centroid().getY() < minY) {
                minY = graph.vertices().get(i).centroid().getY();
            }
            if (graph.vertices().get(i).centroid().getX() > maxX) {
                maxX = graph.vertices().get(i).centroid().getX();
            }
            if (graph.vertices().get(i).centroid().getY() > maxY) {
                maxY = graph.vertices().get(i).centroid().getY();
            }
        }
        centerGraphElementsBetween((ArrayList)graph.vertices(), minX, maxX, minY, maxY);
        centerGraphElementsBetween((ArrayList)graph.edges(), minX, maxX, minY, maxY);
        centerGraphElementsBetween((ArrayList)graph.cells(), minX, maxX, minY, maxY);
    }
    
    private static void centerGraphElementsBetween(final ArrayList<? extends TopologyElement> graphElements, final double minX, final double maxX, final double minY, final double maxY) {
        final double currentMidX = (maxX + minX) / 2.0;
        final double currentMidY = (maxY + minY) / 2.0;
        final double differenceX = currentMidX - 0.5;
        final double differenceY = currentMidY - 0.5;
        for (TopologyElement graphElement : graphElements) {
            final double oldX = graphElement.centroid().getX();
            final double oldY = graphElement.centroid().getY();
            final double newX = oldX - differenceX;
            final double newY = oldY - differenceY;
            graphElement.setCentroid(newX, newY, 0.0);
        }
    }
    
    public static Point2D.Double calculateStackOffset(final Context context, final Container container, final PieceStackType componentStackType, final int cellRadiusPixelsOriginal, final int level, final int site, final SiteType siteType, final int stackSize, final int state) {
        double stackOffsetX = 0.0;
        double stackOffsetY = 0.0;
        final int cellRadiusPixels = (int)(cellRadiusPixelsOriginal * context.game().metadata().graphics().stackScale(container, context, site, siteType, state));
        final int stackOffsetAmount = (int)(0.4 * cellRadiusPixels);
        if (componentStackType == PieceStackType.Ground) {
            if (level == 0) {
                stackOffsetX = cellRadiusPixels / 2;
                stackOffsetY = cellRadiusPixels / 2;
            }
            if (level == 1) {
                stackOffsetX = -cellRadiusPixels / 2;
                stackOffsetY = cellRadiusPixels / 2;
            }
            if (level == 2) {
                stackOffsetX = cellRadiusPixels / 2;
                stackOffsetY = -cellRadiusPixels / 2;
            }
            if (level == 3) {
                stackOffsetX = -cellRadiusPixels / 2;
                stackOffsetY = -cellRadiusPixels / 2;
            }
        }
        else if (componentStackType != PieceStackType.None) {
            if (componentStackType != PieceStackType.Count) {
                if (componentStackType == PieceStackType.Fan) {
                    stackOffsetX = level * stackOffsetAmount;
                }
                else if (componentStackType == PieceStackType.FanAlternating) {
                    if (level % 2 == 0) {
                        stackOffsetX = level * (stackOffsetAmount / 2) + stackOffsetAmount / 2;
                    }
                    else {
                        stackOffsetX = (level + 1) * -(stackOffsetAmount / 2) + stackOffsetAmount / 2;
                    }
                }
                else if (componentStackType == PieceStackType.Ring) {
                    int cellRadiusStack = cellRadiusPixels;
                    if (siteType == SiteType.Cell) {
                        cellRadiusStack = (int)(GraphUtil.calculateCellRadius(container.topology().cells().get(site)) * Bridge.getContainerStyle(container.index()).placement().getWidth());
                    }
                    int stackSizeNew = stackSize;
                    if (stackSizeNew == 0) {
                        stackSizeNew = 1;
                    }
                    stackOffsetX = 0.7 * cellRadiusStack * Math.cos(6.283185307179586 * level / stackSizeNew);
                    stackOffsetY = 0.7 * cellRadiusStack * Math.sin(6.283185307179586 * level / stackSizeNew);
                }
                else if (componentStackType == PieceStackType.Backgammon) {
                    final int lineNumber = level % 5;
                    final int repeatNumber = level / 5;
                    if (site < container.numSites() / 2) {
                        stackOffsetY = -lineNumber * stackOffsetAmount * 4.8 - repeatNumber * stackOffsetAmount;
                    }
                    else {
                        stackOffsetY = lineNumber * stackOffsetAmount * 4.8 - repeatNumber * stackOffsetAmount;
                    }
                }
                else if (container.isHand()) {
                    stackOffsetX = level * stackOffsetAmount / 30;
                    stackOffsetY = level * stackOffsetAmount / 30;
                }
                else {
                    stackOffsetY = -level * stackOffsetAmount;
                }
            }
        }
        return new Point2D.Double(stackOffsetX, stackOffsetY);
    }
    
    public static ArrayList<Integer> cellsCoveredByPiece(final Context context, final Container container, final Component component, final int site, final int localState) {
        final ArrayList<Integer> cellsCoveredByPiece = new ArrayList<>();
        if (component.isLargePiece()) {
            final TIntArrayList largePieceSites = component.locs(context, site, localState, container.topology());
            for (int i = 0; i < largePieceSites.size(); ++i) {
                cellsCoveredByPiece.add(container.topology().cells().get(largePieceSites.get(i)).index());
            }
        }
        else {
            cellsCoveredByPiece.add(site);
        }
        return cellsCoveredByPiece;
    }
    
    public static Regions getRegionOfEdge(final Context context, final Edge e) {
        for (final Regions region : context.game().equipment().regions()) {
            for (final int site : region.eval(context)) {
                for (final Edge edge : context.board().topology().cells().get(site).edges()) {
                    if (edge.index() == e.index()) {
                        return region;
                    }
                }
            }
        }
        return null;
    }
    
    public static List<Edge> getOuterRegionEdges(final List<MetadataImageInfo> surroundedRegions, final int cellIndex, final BoardStyle boardStyle) {
        final double errorDistanceBuffer = 1.0E-5;
        final ArrayList<Edge> allEdgesToDraw = new ArrayList<>();
        final Topology topology = boardStyle.topology();
        final Cell vertex = boardStyle.topology().cells().get(cellIndex);
        final List<Step> steps = topology.trajectories().steps(SiteType.Cell, cellIndex, SiteType.Cell, AbsoluteDirection.Orthogonal);
        for (final Step step : steps) {
            final Cell neighbour = topology.cells().get(step.to().id());
            if (vertex.orthogonal().contains(neighbour)) {
                boolean neighbourInRegion = false;
                for (MetadataImageInfo surroundedRegion : surroundedRegions) {
                    if (surroundedRegion.site == neighbour.index()) {
                        neighbourInRegion = true;
                    }
                }
                if (neighbourInRegion) {
                    continue;
                }
                Cell dualVertex = null;
                Cell dualNeighbour = null;
                for (final Cell f : boardStyle.topology().cells()) {
                    if (Math.abs(f.centroid().getX() - vertex.centroid().getX()) < 1.0E-5 && Math.abs(f.centroid().getY() - vertex.centroid().getY()) < 1.0E-5) {
                        dualVertex = f;
                    }
                    if (Math.abs(f.centroid().getX() - neighbour.centroid().getX()) < 1.0E-5 && Math.abs(f.centroid().getY() - neighbour.centroid().getY()) < 1.0E-5) {
                        dualNeighbour = f;
                    }
                }
                boolean edgeFound = false;
                if (dualVertex == null || dualNeighbour == null) {
                    continue;
                }
                for (final Edge e1 : dualVertex.edges()) {
                    for (final Edge e2 : dualNeighbour.edges()) {
                        if (Math.abs(e1.centroid().getX() - e2.centroid().getX()) < 1.0E-5 && Math.abs(e1.centroid().getY() - e2.centroid().getY()) < 1.0E-5) {
                            allEdgesToDraw.add(e1);
                            edgeFound = true;
                            break;
                        }
                    }
                    if (edgeFound) {
                        break;
                    }
                }
            }
        }
        Cell regionCell = null;
        for (final Cell f2 : boardStyle.topology().cells()) {
            if (Math.abs(f2.centroid().getX() - vertex.centroid().getX()) < 1.0E-5 && Math.abs(f2.centroid().getY() - vertex.centroid().getY()) < 1.0E-5) {
                regionCell = f2;
            }
        }
        for (final Edge e3 : regionCell.edges()) {
            if (e3.properties().get(2L)) {
                allEdgesToDraw.add(e3);
            }
        }
        return allEdgesToDraw;
    }
}
