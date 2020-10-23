// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import topology.*;
import view.container.ContainerStyle;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeveloperGUI
{
    public static void drawPregeneration(final Graphics2D g2d, final Context context, final ContainerStyle containerStyle) {
        try {
            final int cellRadiusPixels = containerStyle.cellRadiusPixels();
            if (SettingsVC.lastClickedSite != null) {
                final Topology graph = context.board().topology();
                if (SettingsVC.lastClickedSite.siteType() == SiteType.Cell) {
                    if (SettingsVC.drawNeighboursCells) {
                        drawNeighbours(g2d, SettingsVC.lastClickedSite.site(), true, containerStyle);
                    }
                    if (SettingsVC.drawRadialsCells) {
                        drawRadials(g2d, context, SettingsVC.lastClickedSite.site(), containerStyle, SiteType.Cell);
                    }
                    if (SettingsVC.drawDistanceCells) {
                        drawDistance(g2d, context, SettingsVC.lastClickedSite.site(), SettingsVC.lastClickedSite.siteType(), containerStyle);
                    }
                }
                if (SettingsVC.lastClickedSite.siteType() == SiteType.Vertex) {
                    if (SettingsVC.drawNeighboursVertices) {
                        drawNeighbours(g2d, SettingsVC.lastClickedSite.site(), false, containerStyle);
                    }
                    if (SettingsVC.drawRadialsVertices) {
                        drawRadials(g2d, context, SettingsVC.lastClickedSite.site(), containerStyle, SiteType.Vertex);
                    }
                    if (SettingsVC.drawDistanceVertices) {
                        drawDistance(g2d, context, SettingsVC.lastClickedSite.site(), SettingsVC.lastClickedSite.siteType(), containerStyle);
                    }
                }
                if (SettingsVC.lastClickedSite.siteType() == SiteType.Edge && SettingsVC.drawDistanceEdges) {
                    drawDistance(g2d, context, SettingsVC.lastClickedSite.site(), SettingsVC.lastClickedSite.siteType(), containerStyle);
                }
                if (SettingsVC.drawVerticesOfEdges && SettingsVC.lastClickedSite.siteType() == SiteType.Edge) {
                    for (final Vertex v : graph.edges().get(SettingsVC.lastClickedSite.site()).vertices()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(v.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
                if (SettingsVC.drawVerticesOfFaces && SettingsVC.lastClickedSite.siteType() == SiteType.Cell) {
                    for (final Vertex v : graph.cells().get(SettingsVC.lastClickedSite.site()).vertices()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(v.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
                if (SettingsVC.drawEdgesOfFaces && SettingsVC.lastClickedSite.siteType() == SiteType.Cell) {
                    for (final Edge e : graph.cells().get(SettingsVC.lastClickedSite.site()).edges()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(e.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
                if (SettingsVC.drawEdgesOfVertices && SettingsVC.lastClickedSite.siteType() == SiteType.Vertex) {
                    for (final Edge e : graph.vertices().get(SettingsVC.lastClickedSite.site()).edges()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(e.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
                if (SettingsVC.drawFacesOfEdges && SettingsVC.lastClickedSite.siteType() == SiteType.Edge) {
                    for (final Cell c : graph.edges().get(SettingsVC.lastClickedSite.site()).cells()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(c.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
                if (SettingsVC.drawFacesOfVertices && SettingsVC.lastClickedSite.siteType() == SiteType.Vertex) {
                    for (final Cell c : graph.vertices().get(SettingsVC.lastClickedSite.site()).cells()) {
                        g2d.setColor(new Color(0, 255, 255, 125));
                        final Point drawPosn = containerStyle.screenPosn(c.centroid());
                        g2d.fillOval(drawPosn.x - cellRadiusPixels / 2, drawPosn.y - cellRadiusPixels / 2, cellRadiusPixels, cellRadiusPixels);
                    }
                }
            }
        }
        catch (Exception E) {
            return;
        }
        drawPregenerationRegions(g2d, context, containerStyle);
    }
    
    private static void drawPregenerationRegions(final Graphics2D g2d, final Context context, final ContainerStyle containerStyle) {
        final Topology graph = context.board().topology();
        g2d.setStroke(new BasicStroke(2.0f, 0, 1));
        final List<TopologyElement> allGraphElementsToDraw = new ArrayList<>();
        if (SettingsVC.drawCornerCells) {
            allGraphElementsToDraw.addAll(graph.corners(SiteType.Cell));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConcaveCells) {
            allGraphElementsToDraw.addAll(graph.cornersConcave(SiteType.Cell));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConvexCells) {
            allGraphElementsToDraw.addAll(graph.cornersConvex(SiteType.Cell));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawOuterCells) {
            allGraphElementsToDraw.addAll(graph.outer(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMajorCells) {
            allGraphElementsToDraw.addAll(graph.major(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMinorCells) {
            allGraphElementsToDraw.addAll(graph.minor(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawPerimeterCells) {
            allGraphElementsToDraw.addAll(graph.perimeter(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawInnerCells) {
            allGraphElementsToDraw.addAll(graph.inner(SiteType.Cell));
        }
        g2d.setColor(new Color(127, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawTopCells) {
            allGraphElementsToDraw.addAll(graph.top(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 127, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawBottomCells) {
            allGraphElementsToDraw.addAll(graph.bottom(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 0, 127, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawLeftCells) {
            allGraphElementsToDraw.addAll(graph.left(SiteType.Cell));
        }
        g2d.setColor(new Color(255, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawRightCells) {
            allGraphElementsToDraw.addAll(graph.right(SiteType.Cell));
        }
        g2d.setColor(new Color(255, 0, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCenterCells) {
            allGraphElementsToDraw.addAll(graph.centre(SiteType.Cell));
        }
        g2d.setColor(new Color(0, 127, 127, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        if (SettingsVC.drawPhasesCells) {
            g2d.setColor(new Color(255, 0, 0, 125));
            drawPhase(g2d, context, SiteType.Cell, containerStyle);
        }
        allGraphElementsToDraw.clear();
        for (final Map.Entry<DirectionFacing, List<TopologyElement>> entry : graph.sides(SiteType.Cell).entrySet()) {
            final String DirectionName = entry.getKey().uniqueName().toString();
            if (SettingsVC.drawSideCells.containsKey(DirectionName) && SettingsVC.drawSideCells.get(DirectionName)) {
                try {
                    allGraphElementsToDraw.addAll(entry.getValue());
                }
                catch (Exception ex) {}
            }
        }
        g2d.setColor(new Color(255, 50, 50, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerVertices) {
            allGraphElementsToDraw.addAll(graph.corners(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConcaveVertices) {
            allGraphElementsToDraw.addAll(graph.cornersConcave(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConvexVertices) {
            allGraphElementsToDraw.addAll(graph.cornersConvex(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMajorVertices) {
            allGraphElementsToDraw.addAll(graph.major(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMinorVertices) {
            allGraphElementsToDraw.addAll(graph.minor(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawPerimeterVertices) {
            allGraphElementsToDraw.addAll(graph.perimeter(SiteType.Vertex));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawOuterVertices) {
            allGraphElementsToDraw.addAll(graph.outer(SiteType.Vertex));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawInnerVertices) {
            allGraphElementsToDraw.addAll(graph.inner(SiteType.Vertex));
        }
        g2d.setColor(new Color(127, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawTopVertices) {
            allGraphElementsToDraw.addAll(graph.top(SiteType.Vertex));
        }
        g2d.setColor(new Color(0, 127, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawBottomVertices) {
            allGraphElementsToDraw.addAll(graph.bottom(SiteType.Vertex));
        }
        g2d.setColor(new Color(0, 0, 127, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawLeftVertices) {
            allGraphElementsToDraw.addAll(graph.left(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawRightVertices) {
            allGraphElementsToDraw.addAll(graph.right(SiteType.Vertex));
        }
        g2d.setColor(new Color(255, 0, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCenterVertices) {
            allGraphElementsToDraw.addAll(graph.centre(SiteType.Vertex));
        }
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        if (SettingsVC.drawPhasesVertices) {
            g2d.setColor(new Color(0, 255, 0, 125));
            drawPhase(g2d, context, SiteType.Vertex, containerStyle);
        }
        allGraphElementsToDraw.clear();
        for (final Map.Entry<DirectionFacing, List<TopologyElement>> entry : graph.sides(SiteType.Vertex).entrySet()) {
            final String DirectionName = entry.getKey().uniqueName().toString();
            if (SettingsVC.drawSideVertices.containsKey(DirectionName) && SettingsVC.drawSideVertices.get(DirectionName)) {
                try {
                    allGraphElementsToDraw.addAll(entry.getValue());
                }
                catch (Exception ex2) {}
            }
        }
        g2d.setColor(new Color(255, 50, 50, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        for (int i = 0; i < SettingsVC.drawColumnsCells.size(); ++i) {
            if (SettingsVC.drawColumnsCells.get(i)) {
                try {
                    allGraphElementsToDraw.addAll(graph.columns(SiteType.Cell).get(i));
                }
                catch (Exception ex3) {}
            }
        }
        g2d.setColor(new Color(0, 255, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        for (int i = 0; i < SettingsVC.drawColumnsVertices.size(); ++i) {
            if (SettingsVC.drawColumnsVertices.get(i)) {
                try {
                    allGraphElementsToDraw.addAll(graph.columns(SiteType.Vertex).get(i));
                }
                catch (Exception ex4) {}
            }
        }
        g2d.setColor(new Color(0, 255, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        for (int i = 0; i < SettingsVC.drawRowsCells.size(); ++i) {
            if (SettingsVC.drawRowsCells.get(i)) {
                try {
                    allGraphElementsToDraw.addAll(graph.rows(SiteType.Cell).get(i));
                }
                catch (Exception ex5) {}
            }
        }
        g2d.setColor(new Color(0, 255, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        for (int i = 0; i < SettingsVC.drawRowsVertices.size(); ++i) {
            if (SettingsVC.drawRowsVertices.get(i)) {
                try {
                    allGraphElementsToDraw.addAll(graph.rows(SiteType.Vertex).get(i));
                }
                catch (Exception ex6) {}
            }
        }
        g2d.setColor(new Color(0, 255, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerEdges) {
            allGraphElementsToDraw.addAll(graph.corners(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConcaveEdges) {
            allGraphElementsToDraw.addAll(graph.cornersConcave(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCornerConvexEdges) {
            allGraphElementsToDraw.addAll(graph.cornersConvex(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMajorEdges) {
            allGraphElementsToDraw.addAll(graph.major(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawMinorEdges) {
            allGraphElementsToDraw.addAll(graph.minor(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawAxialEdges) {
            allGraphElementsToDraw.addAll(graph.axial(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawHorizontalEdges) {
            allGraphElementsToDraw.addAll(graph.horizontal(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawVerticalEdges) {
            allGraphElementsToDraw.addAll(graph.vertical(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawAngledEdges) {
            allGraphElementsToDraw.addAll(graph.angled(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawSlashEdges) {
            allGraphElementsToDraw.addAll(graph.slash(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawSloshEdges) {
            allGraphElementsToDraw.addAll(graph.slosh(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawPerimeterEdges) {
            allGraphElementsToDraw.addAll(graph.perimeter(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawOuterEdges) {
            allGraphElementsToDraw.addAll(graph.outer(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawInnerEdges) {
            allGraphElementsToDraw.addAll(graph.inner(SiteType.Edge));
        }
        g2d.setColor(new Color(127, 0, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawTopEdges) {
            allGraphElementsToDraw.addAll(graph.top(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 127, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawBottomEdges) {
            allGraphElementsToDraw.addAll(graph.bottom(SiteType.Edge));
        }
        g2d.setColor(new Color(0, 0, 127, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawLeftEdges) {
            allGraphElementsToDraw.addAll(graph.left(SiteType.Edge));
        }
        g2d.setColor(new Color(255, 255, 0, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawRightEdges) {
            allGraphElementsToDraw.addAll(graph.right(SiteType.Edge));
        }
        g2d.setColor(new Color(255, 0, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        allGraphElementsToDraw.clear();
        if (SettingsVC.drawCentreEdges) {
            allGraphElementsToDraw.addAll(graph.centre(SiteType.Edge));
        }
        g2d.setColor(new Color(255, 0, 255, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
        if (SettingsVC.drawPhasesEdges) {
            g2d.setColor(new Color(0, 0, 255, 125));
            drawPhase(g2d, context, SiteType.Edge, containerStyle);
        }
        allGraphElementsToDraw.clear();
        for (final Map.Entry<DirectionFacing, List<TopologyElement>> entry : graph.sides(SiteType.Edge).entrySet()) {
            final String DirectionName = entry.getKey().uniqueName().toString();
            if (SettingsVC.drawSideEdges.containsKey(DirectionName) && SettingsVC.drawSideEdges.get(DirectionName)) {
                try {
                    allGraphElementsToDraw.addAll(entry.getValue());
                }
                catch (Exception ex7) {}
            }
        }
        g2d.setColor(new Color(255, 50, 50, 125));
        drawGraphElementList(g2d, allGraphElementsToDraw, containerStyle);
    }
    
    private static void drawGraphElementList(final Graphics2D g2d, final List<TopologyElement> graphElementList, final ContainerStyle containerStyle) {
        for (TopologyElement topologyElement : graphElementList) {
            final int circleSize = 20;
            final Point drawPosn = containerStyle.screenPosn(topologyElement.centroid());
            g2d.drawOval(drawPosn.x - 10, drawPosn.y - 10, 20, 20);
        }
    }
    
    public static void drawPhase(final Graphics2D g2d, final Context context, final SiteType type, final ContainerStyle containerStyle) {
        try {
            g2d.setFont(SettingsVC.displayFont);
            final List<List<TopologyElement>> phases = context.topology().phases(type);
            for (int phase = 0; phase < phases.size(); ++phase) {
                for (final TopologyElement elementToPrint : phases.get(phase)) {
                    final String str = String.valueOf(phase);
                    final Point drawPosn = containerStyle.screenPosn(elementToPrint.centroid());
                    g2d.drawString(str, drawPosn.x, drawPosn.y);
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static void drawDistance(final Graphics2D g2d, final Context context, final int index, final SiteType type, final ContainerStyle containerStyle) {
        try {
            g2d.setFont(SettingsVC.displayFont);
            final TopologyElement element = context.board().topology().getGraphElement(type, index);
            final int[] distance = context.board().topology().distancesToOtherSite(type)[element.index()];
            for (int i = 0; i < distance.length; ++i) {
                final TopologyElement elementToPrint = context.board().topology().getGraphElement(type, i);
                final String str = String.valueOf(distance[i]);
                final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(str, g2d);
                final Point drawPosn = containerStyle.screenPosn(elementToPrint.centroid());
                g2d.drawString(str, (int)(drawPosn.x - bounds.getWidth()), (int)(drawPosn.y + bounds.getHeight()));
            }
        }
        catch (Exception ex) {}
    }
    
    public static void drawRadials(final Graphics2D g2d, final Context context, final int indexElem, final ContainerStyle containerStyle, final SiteType type) {
        try {
            final Topology topology = context.board().topology();
            final List<DirectionFacing> directions = topology.supportedDirections(type);
            for (final DirectionFacing direction : directions) {
                final AbsoluteDirection absDirection = direction.toAbsolute();
                final List<Radial> radials = topology.trajectories().radials(type, indexElem, absDirection);
                final String directionString = direction.toString();
                g2d.setFont(SettingsVC.displayFont);
                g2d.setColor(Color.BLACK);
                for (final Radial radial : radials) {
                    for (int distance = 1; distance < radial.steps().length; ++distance) {
                        final int indexElementRadial = radial.steps()[distance].id();
                        final TopologyElement elementRadial = topology.getGraphElement(type, indexElementRadial);
                        final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(directionString + distance, g2d);
                        final Point drawPosn = containerStyle.screenPosn(elementRadial.centroid());
                        g2d.drawString(directionString + distance, (int)(drawPosn.x - bounds.getWidth() / 2.0), (int)(drawPosn.y + bounds.getHeight() / 2.0));
                    }
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static void drawNeighbours(final Graphics2D g2d, final int vertexIndex, final boolean drawCells, final ContainerStyle containerStyle) {
        try {
            List<? extends TopologyElement> adjacentNeighbours;
            List<? extends TopologyElement> orthogonalNeighbours;
            List<? extends TopologyElement> secondaryNeighbours;
            List<? extends TopologyElement> diagonalNeighbours;
            if (drawCells) {
                final Cell cell = containerStyle.drawnCells().get(vertexIndex);
                adjacentNeighbours = cell.adjacent();
                orthogonalNeighbours = cell.orthogonal();
                secondaryNeighbours = cell.off();
                diagonalNeighbours = cell.diagonal();
            }
            else {
                final Vertex vertex = containerStyle.drawnVertices().get(vertexIndex);
                adjacentNeighbours = vertex.adjacent();
                orthogonalNeighbours = vertex.orthogonal();
                secondaryNeighbours = vertex.off();
                diagonalNeighbours = vertex.diagonal();
            }
            final int circleSize = containerStyle.cellRadiusPixels();
            if (adjacentNeighbours != null) {
                for (final TopologyElement v : adjacentNeighbours) {
                    g2d.setColor(new Color(255, 0, 0, 125));
                    final Point drawPosn = containerStyle.screenPosn(v.centroid());
                    g2d.fillOval(drawPosn.x - circleSize / 2, drawPosn.y - circleSize / 2, circleSize, circleSize);
                }
            }
            if (orthogonalNeighbours != null) {
                for (final TopologyElement v : orthogonalNeighbours) {
                    g2d.setColor(new Color(0, 255, 0, 125));
                    final Point drawPosn = containerStyle.screenPosn(v.centroid());
                    g2d.fillOval(drawPosn.x - circleSize / 2, drawPosn.y - circleSize / 2, circleSize, circleSize);
                }
            }
            if (secondaryNeighbours != null) {
                for (final TopologyElement v : secondaryNeighbours) {
                    g2d.setColor(new Color(0, 0, 255, 125));
                    final Point drawPosn = containerStyle.screenPosn(v.centroid());
                    g2d.fillOval(drawPosn.x - circleSize / 2, drawPosn.y - circleSize / 2, circleSize, circleSize);
                }
            }
            if (diagonalNeighbours != null) {
                for (final TopologyElement v : diagonalNeighbours) {
                    g2d.setColor(new Color(0, 255, 255, 125));
                    final Point drawPosn = containerStyle.screenPosn(v.centroid());
                    g2d.fillOval(drawPosn.x - circleSize / 2, drawPosn.y - circleSize / 2, circleSize, circleSize);
                }
            }
        }
        catch (Exception ex) {}
    }
}
