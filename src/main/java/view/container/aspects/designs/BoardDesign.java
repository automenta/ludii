// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs;

import game.equipment.other.Regions;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import graphics.svg.SVGtoImage;
import math.MathRoutines;
import math.Vector;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.MetadataImageInfo;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.*;
import util.*;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BoardDesign extends ContainerDesign
{
    protected final BoardStyle boardStyle;
    protected final BoardPlacement boardPlacement;
    protected Color colorInner;
    protected Color colorOuter;
    protected Color colorVertices;
    protected Color colorVerticesOuter;
    protected Color colorFillPhase0;
    protected Color colorFillPhase1;
    protected Color colorFillPhase2;
    protected Color colorFillPhase3;
    protected BasicStroke strokeThin;
    protected BasicStroke strokeThick;
    protected boolean checkeredBoard;
    protected boolean straightLines;
    public List<MetadataImageInfo> symbols;
    public List<List<MetadataImageInfo>> regionsToFill;
    public List<List<MetadataImageInfo>> regionsToColourEdges;
    private Color colorDecoration;
    
    public BoardDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        this.symbols = new ArrayList<>();
        this.regionsToFill = new ArrayList<>();
        this.regionsToColourEdges = new ArrayList<>();
        this.boardStyle = boardStyle;
        this.boardPlacement = boardPlacement;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final double boardLineThickness = this.boardStyle.cellRadiusPixels() / 15.0;
        this.checkeredBoard = context.game().metadata().graphics().checkeredBoard();
        this.straightLines = context.game().metadata().graphics().straightRingLines();
        final float swThick;
        final float swThin = swThick = (float)Math.max(1.0, boardLineThickness);
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(120, 190, 240), new Color(210, 230, 255), new Color(210, 0, 0), new Color(0, 230, 0), new Color(0, 0, 255), new Color(0, 0, 0), swThin, swThick);
        this.drawGround(g2d, context, true);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        this.setSymbols(context);
        this.drawSymbolCellEdges(g2d, context, this.strokeThick());
        this.drawSymbols(g2d);
        this.drawGround(g2d, context, false);
        return g2d.getSVGDocument();
    }
    
    protected void setStrokesAndColours(final Context context, final Color colorIn, final Color colorOut, final Color colorFill1, final Color colorFill2, final Color colorFill3, final Color colorFill4, final Color colorDecoration, final float swThin, final float swThick) {
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()] = colorIn;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()] = colorOut;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Vertices.value()] = colorIn;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterVertices.value()] = colorIn;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()] = colorFill1;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()] = colorFill2;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()] = colorFill3;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()] = colorFill4;
        SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()] = colorDecoration;
        for (int bid = 0; bid < SettingsColour.getCustomBoardColour().length; ++bid) {
            Color colour = context.game().metadata().graphics().boardColour(BoardGraphicsType.getTypeFromValue(bid));
            if (colour != null) {
                if (colour.getAlpha() == 0) {
                    colour = null;
                }
                if (!SettingsColour.isCustomBoardColoursFound()) {
                    SettingsColour.getCustomBoardColour()[bid] = colour;
                }
                SettingsColour.getDefaultBoardColour()[bid] = colour;
            }
        }
        SettingsColour.setCustomBoardColoursFound(true);
        final float lineThickness = swThin * context.game().metadata().graphics().boardThickness(BoardGraphicsType.InnerEdges);
        final float borderThickness = swThick * context.game().metadata().graphics().boardThickness(BoardGraphicsType.OuterEdges);
        this.colorInner = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()];
        this.colorOuter = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()];
        this.colorVertices = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Vertices.value()];
        this.colorVerticesOuter = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterVertices.value()];
        this.colorFillPhase0 = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()];
        this.colorFillPhase1 = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()];
        this.colorFillPhase2 = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()];
        this.colorFillPhase3 = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()];
        this.setColorDecoration(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()]);
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] != null) {
            this.colorInner = SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] != null) {
            this.colorOuter = SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Vertices.value()] != null) {
            this.colorVertices = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Vertices.value()];
            this.colorVerticesOuter = SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterVertices.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterVertices.value()] != null) {
            this.colorVerticesOuter = SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterVertices.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] != null) {
            this.colorFillPhase0 = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] != null) {
            this.colorFillPhase1 = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] != null) {
            this.colorFillPhase2 = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] != null) {
            this.colorFillPhase3 = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()];
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] != null) {
            this.setColorDecoration(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()]);
        }
        this.strokeThin = new BasicStroke(lineThickness, 0, 0);
        this.strokeThick = new BasicStroke(borderThickness, 0, 0);
    }
    
    protected void drawGround(final SVGGraphics2D g2d, final Context context, final boolean background) {
        List<MetadataImageInfo> allGroundImages = new ArrayList<>();
        if (background) {
            allGroundImages = context.metadata().graphics().boardBackground(context);
        }
        else {
            allGroundImages = context.metadata().graphics().boardForeground(context);
        }
        for (final MetadataImageInfo groundImageInfo : allGroundImages) {
            final String fullPath = ImageUtil.getImageFullPath(groundImageInfo.path);
            final Rectangle2D rect = SVGtoImage.getBounds(fullPath, (int)(groundImageInfo.scale * this.boardStyle.placement().width));
            final Point drawPosn = this.boardStyle.screenPosn(new Point2D.Double(0.5, 0.5));
            drawPosn.x += (int)(groundImageInfo.offestX * this.boardStyle.placement().width);
            drawPosn.y += (int)(groundImageInfo.offestY * this.boardStyle.placement().height);
            Color edgeColour = this.colorOuter;
            Color fillColour = this.colorFillPhase0;
            if (groundImageInfo.mainColour != null) {
                fillColour = groundImageInfo.mainColour;
            }
            if (groundImageInfo.secondaryColour != null) {
                edgeColour = groundImageInfo.secondaryColour;
            }
            final int rotation = groundImageInfo.rotation;
            ImageUtil.drawImageAtPosn(g2d, fullPath, drawPosn.x, drawPosn.y, rect, edgeColour, fillColour, rotation);
        }
    }
    
    private void drawEdge(final GeneralPath path, final Edge edge, final boolean forwards) {
        final Vertex vertexA = forwards ? edge.vA() : edge.vB();
        final Vertex vertexB = forwards ? edge.vB() : edge.vA();
        final Vector tangentA = forwards ? edge.tangentA() : edge.tangentB();
        final Vector tangentB = forwards ? edge.tangentB() : edge.tangentA();
        final Point ptB = this.boardStyle.screenPosn(vertexB.centroid());
        if (tangentA != null && tangentB != null && !this.straightLines) {
            final double dist = MathRoutines.distance(vertexA.centroid(), vertexB.centroid());
            final double aax = vertexA.centroid().getX() + 0.333 * dist * tangentA.x();
            final double aay = vertexA.centroid().getY() + 0.333 * dist * tangentA.y();
            final double bbx = vertexB.centroid().getX() + 0.333 * dist * tangentB.x();
            final double bby = vertexB.centroid().getY() + 0.333 * dist * tangentB.y();
            final Point ptAA = this.boardStyle.screenPosn(new Point2D.Double(aax, aay));
            final Point ptBB = this.boardStyle.screenPosn(new Point2D.Double(bbx, bby));
            path.curveTo(ptAA.x, ptAA.y, ptBB.x, ptBB.y, ptB.x, ptB.y);
        }
        else {
            path.lineTo(ptB.x, ptB.y);
        }
    }
    
    protected void fillCells(final Graphics2D g2d) {
        g2d.setStroke(this.strokeThin);
        final List<Cell> cells = this.topology().cells();
        for (final Cell cell : cells) {
            final GeneralPath path = new GeneralPath();
            for (int v = 0; v < cell.vertices().size(); ++v) {
                final Vertex vertexA = cell.vertices().get(v);
                final Vertex vertexB = cell.vertices().get((v + 1) % cell.vertices().size());
                if (v == 0) {
                    final Point ptA = this.boardStyle.screenPosn(vertexA.centroid());
                    path.moveTo(ptA.x, ptA.y);
                }
                Edge edge = null;
                for (final Edge edgeA : vertexA.edges()) {
                    if (edgeA.vA().index() == vertexB.index() || edgeA.vB().index() == vertexB.index()) {
                        edge = edgeA;
                        break;
                    }
                }
                this.drawEdge(path, edge, edge.vA().index() == vertexA.index());
            }
            g2d.setColor(this.colorFillPhase0);
            if (this.checkeredBoard) {
                this.setCellColourCheckered(g2d, cell.index());
            }
            for (final List<MetadataImageInfo> regionInfo : this.regionsToFill) {
                for (final MetadataImageInfo d : regionInfo) {
                    if (d.element == SiteType.Cell && d.site == cell.index() && d.path == null) {
                        g2d.setColor(d.mainColour);
                        break;
                    }
                }
            }
            g2d.fill(path);
        }
    }
    
    private void setCellColourCheckered(final Graphics2D g2d, final int index) {
        final Cell cell = this.topology().cells().get(index);
        if (this.topology().phases(SiteType.Cell).get(0).contains(cell)) {
            g2d.setColor(this.colorFillPhase0);
        }
        else if (this.topology().phases(SiteType.Cell).get(1).contains(cell)) {
            g2d.setColor(this.colorFillPhase1);
        }
        else if (this.topology().phases(SiteType.Cell).get(2).contains(cell)) {
            g2d.setColor(this.colorFillPhase2);
        }
        else {
            g2d.setColor(this.colorFillPhase3);
        }
    }
    
    protected void drawEdges(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke, final List<Edge> edges) {
        final double errorDistanceBuffer = 1.0E-4;
        g2d.setStroke(lineStroke);
        g2d.setColor(lineColour);
        final GeneralPath path = new GeneralPath();
        final List<Edge> edgesToDraw = new ArrayList<>();
        for (final Edge edge : edges) {
            if (this.checkEdgeVisible(context, edge)) {
                edgesToDraw.add(edge);
            }
        }
        while (!edgesToDraw.isEmpty()) {
            Edge edge2 = edgesToDraw.get(0);
            boolean nextEdgeFound = true;
            Vertex vertexA = edge2.vA();
            final Point2D centroidA = edge2.vA().centroid();
            final Point drawPosnA = this.boardStyle.screenPosn(centroidA);
            path.moveTo(drawPosnA.x, drawPosnA.y);
            Vertex vertexB = edge2.vB();
            Point2D centroidB = edge2.vB().centroid();
            while (nextEdgeFound) {
                nextEdgeFound = false;
                this.drawEdge(path, edge2, edge2.vA().index() == vertexA.index());
                edgesToDraw.remove(edge2);
                for (final Edge nextEdge : edgesToDraw) {
                    if (Math.abs(centroidB.getX() - nextEdge.vA().centroid().getX()) < 1.0E-4 && Math.abs(centroidB.getY() - nextEdge.vA().centroid().getY()) < 1.0E-4) {
                        nextEdgeFound = true;
                        edge2 = nextEdge;
                        vertexA = edge2.vA();
                        vertexB = edge2.vB();
                        centroidB = vertexB.centroid();
                        break;
                    }
                    if (Math.abs(centroidB.getX() - nextEdge.vB().centroid().getX()) < 1.0E-4 && Math.abs(centroidB.getY() - nextEdge.vB().centroid().getY()) < 1.0E-4) {
                        nextEdgeFound = true;
                        edge2 = nextEdge;
                        vertexA = edge2.vB();
                        vertexB = edge2.vA();
                        centroidB = vertexB.centroid();
                        break;
                    }
                }
            }
            if (Math.abs(centroidA.getX() - centroidB.getX()) < 1.0E-4 && Math.abs(centroidA.getY() - centroidB.getY()) < 1.0E-4) {
                path.closePath();
            }
        }
        g2d.draw(path);
    }
    
    private boolean checkEdgeVisible(final Context context, final Edge edge) {
        for (final MetadataImageInfo s : this.symbols) {
            if (s.line != null && s.line.length >= 2 && ((edge.vA().index() == s.line[0] && edge.vB().index() == s.line[1]) || (edge.vB().index() == s.line[0] && edge.vA().index() == s.line[1]))) {
                return false;
            }
        }
        return true;
    }
    
    protected void drawVertices(final Graphics2D g2d, final Context context, final double radius) {
        if (context.game().metadata().graphics().showRegionOwner() && !context.game().isDeductionPuzzle()) {
            final Regions[] regionsList = context.game().equipment().regions();
            final Color borderColor = new Color(127, 127, 127);
            final double rI = radius * 2.0;
            final double rO = rI + 2.0;
            for (final Regions currentRegions : regionsList) {
                final int owner = currentRegions.owner();
                final int[] eval;
                final int[] sites = eval = currentRegions.eval(context);
                for (final int sid : eval) {
                    final Vertex vertex = this.topology().vertices().get(sid);
                    final Point pt = this.boardStyle.screenPosn(vertex.centroid());
                    g2d.setColor(borderColor);
                    final Shape ellipseO = new Ellipse2D.Double(pt.x - rO, pt.y - rO, 2.0 * rO, 2.0 * rO);
                    g2d.fill(ellipseO);
                    final Color playerColour = SettingsColour.playerColour(owner, context);
                    g2d.setColor(playerColour);
                    final Shape ellipseI = new Ellipse2D.Double(pt.x - rI, pt.y - rI, 2.0 * rI, 2.0 * rI);
                    g2d.fill(ellipseI);
                }
            }
        }
        for (final Vertex vertex2 : this.topology().vertices()) {
            g2d.setColor(this.colorVertices);
            g2d.setStroke(this.strokeThin);
            if (vertex2.properties().get(2L)) {
                g2d.setColor(this.colorVerticesOuter);
            }
            for (final List<MetadataImageInfo> regionInfo : this.regionsToFill) {
                for (final MetadataImageInfo d : regionInfo) {
                    if (d.element == SiteType.Vertex && d.site == vertex2.index() && d.path == null) {
                        g2d.setColor(d.mainColour);
                        break;
                    }
                }
            }
            final Point pt2 = this.boardStyle.screenPosn(vertex2.centroid());
            final Shape ellipseO2 = new Ellipse2D.Double(pt2.x - radius, pt2.y - radius, 2.0 * radius, 2.0 * radius);
            g2d.fill(ellipseO2);
        }
    }
    
    public void drawBoardOutline(final SVGGraphics2D g2d) {
        final List<Vertex> cells = this.topology().vertices();
        g2d.setStroke(this.strokeThin);
        double minX = 9999.0;
        double minY = 9999.0;
        double maxX = -9999.0;
        double maxY = -9999.0;
        final GeneralPath path = new GeneralPath();
        for (final Vertex cell : cells) {
            for (int v = 0; v < cell.cells().size(); ++v) {
                final Cell corner = cell.cells().get(v);
                final Point posn = this.boardStyle.screenPosn(corner.centroid());
                final int x = posn.x;
                final int y = posn.y;
                if (minX > x) {
                    minX = x;
                }
                if (minY > y) {
                    minY = y;
                }
                if (maxX < x) {
                    maxX = x;
                }
                if (maxY < y) {
                    maxY = y;
                }
            }
            g2d.setColor(this.colorFillPhase0);
        }
        final int OuterBufferDistance = this.boardStyle.cellRadiusPixels() * 2;
        path.moveTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        g2d.fill(path);
    }
    
    protected void fillCells(final Graphics2D g2d, final int pixels, final Color fillColor, final Color borderColor, final BasicStroke stroke, final TIntArrayList validLocations, final Color colorInvalid, final boolean addDiagonal) {
        final List<Cell> cells = this.topology().cells();
        g2d.setStroke(stroke);
        for (final Cell cell : cells) {
            final GeneralPath path = new GeneralPath();
            g2d.setColor(fillColor);
            for (int v = 0; v < cell.vertices().size(); ++v) {
                if (path.getCurrentPoint() == null) {
                    final Vertex prev = cell.vertices().get(cell.vertices().size() - 1);
                    final Point prevPosn = this.boardStyle.screenPosn(prev.centroid());
                    path.moveTo(prevPosn.x, prevPosn.y);
                }
                final Vertex corner = cell.vertices().get(v);
                final Point cornerPosn = this.boardStyle.screenPosn(corner.centroid());
                path.lineTo(cornerPosn.x, cornerPosn.y);
            }
            if (validLocations != null && !validLocations.contains(cell.index())) {
                g2d.setColor(colorInvalid);
            }
            g2d.fill(path);
            if (addDiagonal && validLocations != null && !validLocations.contains(cell.index())) {
                g2d.setColor(borderColor);
                final Point firstCorner = this.boardStyle.screenPosn(cell.vertices().get(1).centroid());
                final Point secondCorner = this.boardStyle.screenPosn(cell.vertices().get(3).centroid());
                path.moveTo(firstCorner.getX(), firstCorner.y);
                path.lineTo(secondCorner.x, secondCorner.y);
                g2d.draw(path);
            }
        }
    }
    
    public void setSymbols(final Context context) {
        this.symbols.clear();
        this.regionsToFill.clear();
        this.regionsToColourEdges.clear();
        this.symbols.addAll(context.game().metadata().graphics().drawSymbol(context));
        this.regionsToFill.addAll(context.game().metadata().graphics().regionsToFill(context, SiteType.Cell));
        this.regionsToFill.addAll(context.game().metadata().graphics().regionsToFill(context, SiteType.Vertex));
        final List<List<MetadataImageInfo>> regionsToColourEdgesTemp = new ArrayList<>(context.game().metadata().graphics().regionsToFill(context, SiteType.Edge));
        for (int regionIndex = 0; regionIndex < regionsToColourEdgesTemp.size(); ++regionIndex) {
            this.regionsToColourEdges.add(new ArrayList<>());
            final List<MetadataImageInfo> regionInfo = regionsToColourEdgesTemp.get(regionIndex);
            for (final MetadataImageInfo imageInfo : regionInfo) {
                final List<Edge> cellEdges = ContainerUtil.getOuterRegionEdges(regionInfo, imageInfo.site, this.boardStyle);
                for (Edge cellEdge : cellEdges) {
                    Color colour = imageInfo.mainColour;
                    if (colour == null) {
                        final Regions r = ContainerUtil.getRegionOfEdge(context, cellEdge);
                        if (r != null) {
                            colour = SettingsColour.getDefaultPlayerColours()[r.role().owner()];
                        } else {
                            colour = this.colorDecoration();
                        }
                    }
                    if (cellEdge.properties().get(2L) && (imageInfo.boardGraphicsType == null || imageInfo.boardGraphicsType == BoardGraphicsType.OuterEdges)) {
                        this.regionsToColourEdges.get(regionIndex).add(new MetadataImageInfo(cellEdge.index(), SiteType.Edge, BoardGraphicsType.OuterEdges, colour));
                    } else if (!cellEdge.properties().get(2L) && (imageInfo.boardGraphicsType == null || imageInfo.boardGraphicsType == BoardGraphicsType.InnerEdges)) {
                        this.regionsToColourEdges.get(regionIndex).add(new MetadataImageInfo(cellEdge.index(), SiteType.Edge, BoardGraphicsType.InnerEdges, colour));
                    }
                }
            }
        }
        this.symbols.addAll(context.game().metadata().graphics().drawLines(context));
    }
    
    public void drawSymbolCellEdges(final Graphics2D g2d, final Context context, final BasicStroke stroke) {
        for (int regionIndex = 0; regionIndex < this.regionsToColourEdges.size(); ++regionIndex) {
            final List<Edge> symbolEdges = new ArrayList<>();
            final Color edgeColor = this.colorDecoration;
            this.drawEdges(g2d, context, edgeColor, stroke, symbolEdges);
        }
    }
    
    public void drawSymbols(final Graphics2D g2d) {
        for (final MetadataImageInfo s : this.symbols) {
            if (s.path != null) {
                if (s.site == -1) {
                    continue;
                }
                TopologyElement e = null;
                if (s.element == SiteType.Cell) {
                    e = this.boardStyle.topology().cells().get(s.site);
                }
                else if (s.element == SiteType.Edge) {
                    e = this.boardStyle.topology().edges().get(s.site);
                }
                else if (s.element == SiteType.Vertex) {
                    e = this.boardStyle.topology().vertices().get(s.site);
                }
                final String fullPath = ImageUtil.getImageFullPath(s.path);
                final Rectangle2D rect = SVGtoImage.getBounds(fullPath, (int)(s.scale * this.boardStyle.cellRadiusPixels() * 2.0f));
                final Point drawPosn = this.boardStyle.screenPosn(e.centroid());
                Color edgeColour = this.colorDecoration();
                Color fillColour = null;
                if (s.mainColour != null) {
                    fillColour = s.mainColour;
                }
                if (s.secondaryColour != null) {
                    edgeColour = s.secondaryColour;
                }
                final int rotation = s.rotation;
                ImageUtil.drawImageAtPosn(g2d, fullPath, drawPosn.x, drawPosn.y, rect, edgeColour, fillColour, rotation);
            }
        }
        for (final MetadataImageInfo s : this.symbols) {
            if (s.line != null && s.line.length >= 2) {
                Color colour = s.mainColour;
                final float scale = s.scale;
                if (colour == null) {
                    colour = this.colorOuter;
                }
                g2d.setColor(colour);
                final Stroke strokeLine = new BasicStroke(this.strokeThick.getLineWidth() * scale, 0, 0);
                final Vertex v1 = this.boardStyle.container().topology().vertices().get(s.line[0]);
                final Vertex v2 = this.boardStyle.container().topology().vertices().get(s.line[1]);
                g2d.setStroke(strokeLine);
                if (s.curve == null) {
                    g2d.drawLine(this.boardStyle.screenPosn(v1.centroid()).x, this.boardStyle.screenPosn(v1.centroid()).y, this.boardStyle.screenPosn(v2.centroid()).x, this.boardStyle.screenPosn(v2.centroid()).y);
                }
                else {
                    final GeneralPath path = new GeneralPath();
                    path.moveTo(this.boardStyle.screenPosn(v1.centroid()).x, this.boardStyle.screenPosn(v1.centroid()).y);
                    final Point2D curvePoint1 = new Point2D.Float(s.curve[0], s.curve[1]);
                    final Point2D curvePoint2 = new Point2D.Float(s.curve[2], s.curve[3]);
                    path.curveTo(this.boardStyle.screenPosn(curvePoint1).x, this.boardStyle.screenPosn(curvePoint1).y, this.boardStyle.screenPosn(curvePoint2).x, this.boardStyle.screenPosn(curvePoint2).y, this.boardStyle.screenPosn(v2.centroid()).x, this.boardStyle.screenPosn(v2.centroid()).y);
                    g2d.draw(path);
                }
            }
        }
    }
    
    protected void drawInnerCellEdges(final Graphics2D g2d, final Context context) {
        this.drawInnerCellEdges(g2d, context, this.colorInner, this.strokeThin);
    }
    
    protected void drawInnerCellEdges(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.innerEdgeRelations(this.topology()));
    }
    
    protected void drawOuterCellEdges(final Graphics2D g2d, final Context context) {
        this.drawOuterCellEdges(g2d, context, this.colorOuter, this.strokeThick());
    }
    
    protected void drawOuterCellEdges(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.outerEdgeRelations(this.topology()));
    }
    
    protected void drawDiagonalEdges(final Graphics2D g2d, final Context context) {
        this.drawDiagonalEdges(g2d, context, this.colorInner, this.strokeThin);
    }
    
    protected void drawDiagonalEdges(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.diagonalEdgeRelations(this.topology()));
    }
    
    protected void drawOrthogonalConnections(final Graphics2D g2d, final Context context) {
        this.drawOrthogonalConnections(g2d, context, this.colorInner, this.strokeThin);
    }
    
    protected void drawOrthogonalConnections(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.orthogonalCellConnections(this.topology()));
    }
    
    protected void drawDiagonalConnections(final Graphics2D g2d, final Context context) {
        this.drawDiagonalConnections(g2d, context, this.colorInner, this.strokeThin);
    }
    
    protected void drawDiagonalConnections(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.diagonalCellConnections(this.topology()));
    }
    
    protected void drawOffDiagonalConnections(final Graphics2D g2d, final Context context) {
        this.drawOffDiagonalConnections(g2d, context, this.colorInner, this.strokeThin);
    }
    
    protected void drawOffDiagonalConnections(final Graphics2D g2d, final Context context, final Color lineColour, final Stroke lineStroke) {
        this.drawEdges(g2d, context, lineColour, lineStroke, GraphUtil.offCellConnections(this.topology()));
    }
    
    public BasicStroke strokeThick() {
        return this.strokeThick;
    }
    
    public Color colorDecoration() {
        return this.colorDecoration;
    }
    
    public void setColorDecoration(final Color colorDecoration) {
        this.colorDecoration = colorDecoration;
    }
    
    public Topology topology() {
        return this.boardStyle.topology();
    }
    
    public int cellRadiusPixels() {
        return this.boardStyle.cellRadiusPixels();
    }
    
    public Point screenPosn(final Point2D posn) {
        return this.boardStyle.screenPosn(posn);
    }
}
