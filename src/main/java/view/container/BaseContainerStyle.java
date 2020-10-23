// 
// Decompiled by Procyon v0.5.36
// 

package view.container;

import bridge.Bridge;
import game.Game;
import game.equipment.container.Container;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import metadata.graphics.util.PieceStackType;
import metadata.graphics.util.PuzzleHintType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.*;
import util.*;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;
import view.container.aspects.axes.ContainerAxis;
import view.container.aspects.components.ContainerComponents;
import view.container.aspects.designs.ContainerDesign;
import view.container.aspects.placement.ContainerPlacement;
import view.container.aspects.tracks.ContainerTrack;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainerStyle implements ContainerStyle
{
    protected ContainerComponents containerComponents;
    protected ContainerTrack containerTrack;
    protected ContainerAxis containerAxis;
    protected ContainerDesign containerDesign;
    protected ContainerPlacement containerPlacement;
    private final Container container;
    protected String imageSVGString;
    protected String graphSVGString;
    protected String connectionsSVGString;
    
    public BaseContainerStyle(final Container container) {
        this.imageSVGString = null;
        this.graphSVGString = null;
        this.connectionsSVGString = null;
        this.container = container;
        ContainerUtil.normaliseGraphElements(this.topology());
        ContainerUtil.centerGraphElements(this.topology());
        this.containerPlacement = new ContainerPlacement(this);
        this.containerComponents = new ContainerComponents(this);
        this.containerTrack = new ContainerTrack();
        this.containerAxis = new ContainerAxis();
        this.containerDesign = new ContainerDesign();
    }
    
    public SVGGraphics2D setSVGRenderingValues() {
        final SVGGraphics2D g2d = new SVGGraphics2D((int)this.containerPlacement.unscaledPlacement().getWidth(), (int)this.containerPlacement.unscaledPlacement().getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2d;
    }
    
    @Override
    public void draw(final Graphics2D g2d, final PlaneType plane, final Context context) {
        try {
            switch (plane) {
                case BOARD: {
                    Bridge.graphicsRenderer().drawBoard(g2d, this.containerPlacement.unscaledPlacement());
                    break;
                }
                case TRACK: {
                    this.containerTrack.drawBoardTrack(g2d, context, this);
                    break;
                }
                case AXES: {
                    this.containerAxis.drawAxes(g2d);
                    break;
                }
                case GRAPH: {
                    Bridge.graphicsRenderer().drawGraph(g2d, this.containerPlacement.unscaledPlacement());
                    break;
                }
                case CONNECTIONS: {
                    Bridge.graphicsRenderer().drawConnections(g2d, this.containerPlacement.unscaledPlacement());
                    break;
                }
                case HINTS: {
                    if (context.game().metadata().graphics().hintType() == PuzzleHintType.None) {
                        break;
                    }
                    this.containerDesign.drawPuzzleHints(g2d, context);
                    break;
                }
                case CANDIDATES: {
                    this.containerDesign.drawPuzzleCandidates(g2d, context);
                    break;
                }
                case COMPONENTS: {
                    this.containerComponents.drawComponents(g2d, context);
                    break;
                }
                case PREGENERATION: {
                    DeveloperGUI.drawPregeneration(g2d, context, this);
                    break;
                }
                case INDICES: {
                    this.drawIndices(g2d, context);
                    break;
                }
                case POSSIBLEMOVES: {
                    this.drawPossibleMoves(g2d, context);
                    break;
                }
                case COSTS: {
                    this.drawElementCost(g2d, context);
                    break;
                }
            }
        }
        catch (Exception e) {
            SettingsVC.errorReport = SettingsVC.errorReport + "VC_ERROR: Error detected when attempting to draw " + plane.name() + "\n";
            e.printStackTrace();
        }
    }
    
    @Override
    public void render(final PlaneType plane, final Context context) {
        try {
            switch (plane) {
                case BOARD: {
                    this.imageSVGString = this.containerDesign.createSVGImage(context);
                }
                case TRACK: {}
                case GRAPH: {
                    this.graphSVGString = GraphUtil.createSVGGraphImage(this);
                    break;
                }
                case CONNECTIONS: {
                    this.connectionsSVGString = GraphUtil.createSVGConnectionsImage(this);
                }
                case HINTS: {}
                case CANDIDATES: {}
                case COMPONENTS: {}
                case PREGENERATION: {}
                case INDICES: {}
                case POSSIBLEMOVES: {}
            }
        }
        catch (Exception e) {
            SettingsVC.errorReport = SettingsVC.errorReport + "VC_ERROR: Error detected when attempting to render " + plane.name() + "\n";
            e.printStackTrace();
        }
    }
    
    public void drawElementCost(final Graphics2D g2d, final Context context) {
        if (context.metadata().graphics().showCost()) {
            g2d.setFont(new Font(g2d.getFont().getFontName(), 1, this.cellRadiusPixels() / 5));
            for (final TopologyElement graphElement : this.drawnCells()) {
                g2d.setColor(new Color(0, 200, 0));
                StringUtil.drawStringAtSite(g2d, String.valueOf(graphElement.cost()), graphElement, this.screenPosn(graphElement.centroid()), true);
            }
            for (final TopologyElement graphElement : this.drawnEdges()) {
                g2d.setColor(new Color(100, 0, 100));
                StringUtil.drawStringAtSite(g2d, String.valueOf(graphElement.cost()), graphElement, this.screenPosn(graphElement.centroid()), true);
            }
            for (final TopologyElement graphElement : this.drawnVertices()) {
                g2d.setColor(new Color(255, 0, 0));
                StringUtil.drawStringAtSite(g2d, String.valueOf(graphElement.cost()), graphElement, this.screenPosn(graphElement.centroid()), true);
            }
        }
    }
    
    public void drawIndices(final Graphics2D g2d, final Context context) {
        g2d.setFont(SettingsVC.displayFont);
        final List<TopologyElement> possibleElements = this.drawnGraphElements();
        for (final TopologyElement graphElement : possibleElements) {
            if (graphElement.elementType() == SiteType.Cell) {
                g2d.setColor(new Color(0, 200, 0));
            }
            if (graphElement.elementType() == SiteType.Edge) {
                g2d.setColor(new Color(100, 0, 100));
            }
            if (graphElement.elementType() == SiteType.Vertex) {
                g2d.setColor(new Color(255, 0, 0));
            }
            if (this.container.index() > 0 || context.board().defaultSite() == graphElement.elementType()) {
                this.drawIndexIfRequired(SettingsVC.showIndices, SettingsVC.showCoordinates, g2d, graphElement);
            }
            if (graphElement.elementType() == SiteType.Cell) {
                this.drawIndexIfRequired(SettingsVC.showCellIndices, SettingsVC.showCellCoordinates, g2d, graphElement);
            }
            if (graphElement.elementType() == SiteType.Edge) {
                this.drawIndexIfRequired(SettingsVC.showEdgeIndices, SettingsVC.showEdgeCoordinates, g2d, graphElement);
            }
            if (graphElement.elementType() == SiteType.Vertex) {
                this.drawIndexIfRequired(SettingsVC.showVertexIndices, SettingsVC.showVertexCoordinates, g2d, graphElement);
            }
        }
    }
    
    private void drawIndexIfRequired(final boolean showIndices, final boolean showCoordinates, final Graphics2D g2d, final TopologyElement graphElement) {
        if (showIndices) {
            StringUtil.drawStringAtSite(g2d, "" + graphElement.index(), graphElement, this.screenPosn(graphElement.centroid()), SettingsVC.coordWithOutline);
        }
        if (showCoordinates) {
            StringUtil.drawStringAtSite(g2d, "" + graphElement.label(), graphElement, this.screenPosn(graphElement.centroid()), SettingsVC.coordWithOutline);
        }
    }
    
    public void drawPossibleMoves(final Graphics2D g2d, final Context context) {
        if (SettingsVC.thisFrameIsAnimated || SettingsVC.sandboxMode || context.game().isDeductionPuzzle()) {
            return;
        }
        final int transparencyAmount = 125;
        final int sz = Math.min(16, (int)(0.4 * this.containerPlacement.cellRadiusPixels()));
        if (SettingsVC.SelectingConsequenceMove) {
            for (Location possibleToLocation : SettingsVC.possibleToLocations) {
                if (possibleToLocation.siteType() == null) {
                    possibleToLocation = new FullLocation(possibleToLocation.site(), possibleToLocation.level(), SiteType.Cell);
                }
                if (ContainerUtil.getContainerId(context, possibleToLocation.site(), possibleToLocation.siteType()) == this.container().index()) {
                    final int indexOnContainer = ContainerUtil.getContainerSite(context, possibleToLocation.site(), possibleToLocation.siteType());
                    Point drawPosn = null;
                    if (possibleToLocation.siteType() == SiteType.Cell) {
                        drawPosn = this.screenPosn(this.drawnCells().get(indexOnContainer).centroid());
                    }
                    if (possibleToLocation.siteType() == SiteType.Edge) {
                        drawPosn = this.screenPosn(this.drawnEdges().get(indexOnContainer).centroid());
                    }
                    if (possibleToLocation.siteType() == SiteType.Vertex) {
                        drawPosn = this.screenPosn(this.drawnVertices().get(indexOnContainer).centroid());
                    }
                    final ContainerState cs = context.state().containerStates()[this.container().index()];
                    final int localState = cs.state(possibleToLocation.site(), possibleToLocation.level(), possibleToLocation.siteType());
                    final PieceStackType componentStackType = context.metadata().graphics().stackType(this.container(), context, possibleToLocation.site(), possibleToLocation.siteType(), localState);
                    final int stackSize = cs.sizeStack(possibleToLocation.site(), possibleToLocation.siteType());
                    final Point2D.Double offsetDistance = ContainerUtil.calculateStackOffset(context, this.container(), componentStackType, this.containerPlacement.cellRadiusPixels(), possibleToLocation.level(), possibleToLocation.site(), possibleToLocation.siteType(), stackSize, localState);
                    g2d.setColor(new Color(0, 0, 0, 125));
                    g2d.fillOval((int)(drawPosn.x - 2 - sz / 2 + offsetDistance.x), (int)(drawPosn.y - 2 - sz / 2 + offsetDistance.y), sz + 4, sz + 4);
                    g2d.setColor(new Color(249, 166, 0, 125));
                    g2d.fillOval((int)(drawPosn.x - sz / 2 + offsetDistance.x), (int)(drawPosn.y - sz / 2 + offsetDistance.y), sz, sz);
                }
            }
        }
        else if (SettingsVC.selectedLocation.site() == -1 && !context.trial().over()) {
            for (final WorldLocation worldLocation : this.calculatePossibleMoveSites(true, context)) {
                final Point p = new Point((int)worldLocation.position().getX(), (int)worldLocation.position().getY());
                final ContainerState cs2 = context.state().containerStates()[this.container().index()];
                final int localState2 = cs2.state(worldLocation.location().site(), worldLocation.location().level(), worldLocation.location().siteType());
                final PieceStackType componentStackType2 = context.metadata().graphics().stackType(this.container(), context, worldLocation.location().site(), worldLocation.location().siteType(), localState2);
                final int stackSize2 = cs2.sizeStack(worldLocation.location().site(), worldLocation.location().siteType());
                final Point2D.Double offsetDistance2 = ContainerUtil.calculateStackOffset(context, this.container(), componentStackType2, this.containerPlacement.cellRadiusPixels(), worldLocation.location().level(), worldLocation.location().site(), worldLocation.location().siteType(), stackSize2, localState2);
                g2d.setColor(new Color(0, 0, 0, 125));
                g2d.fillOval((int)(p.x - 2 - sz / 2 + offsetDistance2.x), (int)(p.y - 2 - sz / 2 + offsetDistance2.y), sz + 4, sz + 4);
                g2d.setColor(new Color(0, 127, 255, 125));
                g2d.fillOval((int)(p.x - sz / 2 + offsetDistance2.x), (int)(p.y - sz / 2 + offsetDistance2.y), sz, sz);
            }
        }
        if (!SettingsVC.SelectingConsequenceMove && SettingsVC.selectedLocation.site() != -1 && !context.trial().over()) {
            for (final WorldLocation worldLocation : this.calculatePossibleMoveSites(false, context)) {
                final Point p = new Point((int)worldLocation.position().getX(), (int)worldLocation.position().getY());
                final ContainerState cs2 = context.state().containerStates()[this.container().index()];
                final int localState2 = cs2.state(worldLocation.location().site(), worldLocation.location().level(), worldLocation.location().siteType());
                final PieceStackType componentStackType2 = context.metadata().graphics().stackType(this.container(), context, worldLocation.location().site(), worldLocation.location().siteType(), localState2);
                final int stackSize2 = cs2.sizeStack(worldLocation.location().site(), worldLocation.location().siteType());
                final Point2D.Double offsetDistance2 = ContainerUtil.calculateStackOffset(context, this.container(), componentStackType2, this.containerPlacement.cellRadiusPixels(), worldLocation.location().level(), worldLocation.location().site(), worldLocation.location().siteType(), stackSize2, localState2);
                g2d.setColor(new Color(0, 0, 0, 125));
                g2d.fillOval((int)(p.x - 2 - sz / 2 + offsetDistance2.x), (int)(p.y - 2 - sz / 2 + offsetDistance2.y), sz + 4, sz + 4);
                g2d.setColor(new Color(255, 0, 0, 125));
                g2d.fillOval((int)(p.x - sz / 2 + offsetDistance2.x), (int)(p.y - sz / 2 + offsetDistance2.y), sz, sz);
            }
        }
    }
    
    public ArrayList<WorldLocation> calculatePossibleMoveSites(final boolean moveFrom, final Context context) {
        final ArrayList<WorldLocation> possibleMoveSites = new ArrayList<>();
        if (SettingsVC.showPossibleMoves || context.metadata().graphics().showPossibleMoves()) {
            final Game game = context.game();
            if (!game.finishedPreprocessing()) {
                return null;
            }
            final Moves legal = game.moves(context);
            for (final Move move : legal.moves()) {
                if (move.mover() != Bridge.graphicsRenderer().getSingleHumanMover(move.mover(), context)) {
                    continue;
                }
                int siteIndex;
                int siteLevel;
                SiteType siteType;
                Point drawPosn;
                if (moveFrom) {
                    final int containerId = this.container().index();
                    final int containerSite = ContainerUtil.getContainerSite(context, move.from(), move.fromType());
                    siteIndex = move.from();
                    siteLevel = move.levelFrom();
                    siteType = move.fromType();
                    if (containerId == -1) {
                        continue;
                    }
                    if (containerSite == -1) {
                        continue;
                    }
                    if (containerId != ContainerUtil.getContainerId(context, move.from(), move.fromType())) {
                        continue;
                    }
                    if (move.fromType() == SiteType.Vertex) {
                        if (containerSite >= this.drawnVertices().size()) {
                            System.out.println("** BaseContainerStyle.calculatePossibleMoveSites(): containerSite " + containerSite + " out of vertex range.");
                            return possibleMoveSites;
                        }
                        drawPosn = this.screenPosn(this.drawnVertices().get(containerSite).centroid());
                    }
                    else if (move.fromType() == SiteType.Edge) {
                        if (containerSite >= this.drawnEdges().size()) {
                            System.out.println("** BaseContainerStyle.calculatePossibleMoveSites(): containerSite " + containerSite + " out of edge range.");
                            return possibleMoveSites;
                        }
                        drawPosn = this.screenPosn(this.drawnEdges().get(containerSite).centroid());
                    }
                    else {
                        if (containerSite >= this.drawnCells().size()) {
                            System.out.println("** BaseContainerStyle.calculatePossibleMoveSites(): containerSite " + containerSite + " out of cell range.");
                            return possibleMoveSites;
                        }
                        drawPosn = this.screenPosn(this.drawnCells().get(containerSite).centroid());
                    }
                }
                else {
                    if (move.from() != SettingsVC.selectedLocation.site() || move.levelFrom() != SettingsVC.selectedLocation.level() || move.fromType() != SettingsVC.selectedLocation.siteType()) {
                        continue;
                    }
                    final int containerId = this.container().index();
                    final int containerSite = ContainerUtil.getContainerSite(context, move.to(), move.toType());
                    siteIndex = move.to();
                    siteLevel = move.levelTo();
                    siteType = move.toType();
                    if (containerId == -1) {
                        continue;
                    }
                    if (containerSite == -1) {
                        continue;
                    }
                    if (containerId != ContainerUtil.getContainerId(context, move.to(), move.toType())) {
                        continue;
                    }
                    if (move.toType() == SiteType.Vertex) {
                        drawPosn = this.screenPosn(this.drawnVertices().get(containerSite).centroid());
                    }
                    else if (move.toType() == SiteType.Edge) {
                        drawPosn = this.screenPosn(this.drawnEdges().get(containerSite).centroid());
                    }
                    else {
                        drawPosn = this.screenPosn(this.drawnCells().get(containerSite).centroid());
                    }
                }
                boolean alreadyContainsPoint = false;
                for (final WorldLocation worldLocation : possibleMoveSites) {
                    if (worldLocation.position().getX() == drawPosn.x && worldLocation.position().getY() == drawPosn.y) {
                        alreadyContainsPoint = true;
                    }
                }
                if (alreadyContainsPoint) {
                    continue;
                }
                possibleMoveSites.add(new WorldLocation(new FullLocation(siteIndex, siteLevel, siteType), new Point2D.Double(drawPosn.x, drawPosn.y)));
            }
        }
        return possibleMoveSites;
    }
    
    @Override
    public List<TopologyElement> drawnGraphElements() {
        final List<TopologyElement> allGraphElements = new ArrayList<>();
        for (final TopologyElement g : this.drawnCells()) {
            allGraphElements.add(g);
        }
        for (final TopologyElement g : this.drawnEdges()) {
            allGraphElements.add(g);
        }
        for (final TopologyElement g : this.drawnVertices()) {
            allGraphElements.add(g);
        }
        return allGraphElements;
    }
    
    @Override
    public TopologyElement drawnGraphElement(final int index, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell) {
            for (final TopologyElement g : this.drawnCells()) {
                if (g.index() == index) {
                    return g;
                }
            }
        }
        if (graphElementType == SiteType.Edge) {
            for (final TopologyElement g : this.drawnEdges()) {
                if (g.index() == index) {
                    return g;
                }
            }
        }
        if (graphElementType == SiteType.Vertex) {
            for (final TopologyElement g : this.drawnVertices()) {
                if (g.index() == index) {
                    return g;
                }
            }
        }
        return null;
    }
    
    public SiteType getElementType(final int index) {
        for (final TopologyElement element : this.drawnGraphElements()) {
            if (element.index() == index) {
                return element.elementType();
            }
        }
        return null;
    }
    
    @Override
    public String graphSVGImage() {
        return this.graphSVGString;
    }
    
    @Override
    public String dualSVGImage() {
        return this.connectionsSVGString;
    }
    
    @Override
    public String containerSVGImage() {
        return this.imageSVGString;
    }
    
    @Override
    public List<Cell> drawnCells() {
        return this.containerPlacement.drawnCells();
    }
    
    @Override
    public List<Edge> drawnEdges() {
        return this.containerPlacement.drawnEdges();
    }
    
    @Override
    public List<Vertex> drawnVertices() {
        return this.containerPlacement.drawnVertices();
    }
    
    @Override
    public Topology topology() {
        return this.container().topology();
    }
    
    @Override
    public final double pieceScale() {
        return this.containerComponents.pieceScale();
    }
    
    @Override
    public double containerZoom() {
        return this.containerPlacement.containerZoom();
    }
    
    @Override
    public void drawPuzzleValue(final int value, final int site, final Context context, final Graphics2D g2d, final Point drawPosn, final int imageSize) {
        this.containerComponents.drawPuzzleValue(value, site, context, g2d, drawPosn, imageSize);
    }
    
    public Container container() {
        return this.container;
    }
    
    @Override
    public Rectangle placement() {
        return this.containerPlacement.placement();
    }
    
    @Override
    public double cellRadius() {
        return this.containerPlacement.cellRadius();
    }
    
    @Override
    public int cellRadiusPixels() {
        return this.containerPlacement.cellRadiusPixels();
    }
    
    @Override
    public Point screenPosn(final Point2D posn) {
        return this.containerPlacement.screenPosn(posn);
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        this.containerPlacement.setPlacement(context, placement);
    }
    
    @Override
    public final double containerScale() {
        return this.containerPlacement.containerScale();
    }
    
    @Override
    public boolean ignorePieceSelectionLimit() {
        return this.containerDesign.ignorePieceSelectionLimit();
    }
}
