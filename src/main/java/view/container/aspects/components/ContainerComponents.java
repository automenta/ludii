// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.components;

import bridge.Bridge;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.types.board.SiteType;
import collections.FastArrayList;
import metadata.graphics.util.PieceStackType;
import topology.Cell;
import topology.TopologyElement;
import topology.Vertex;
import util.*;
import util.action.Action;
import util.action.die.ActionUpdateDice;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;
import view.container.BaseContainerStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ContainerComponents
{
    final BaseContainerStyle containerStyle;
    private double pieceScale;
    
    public ContainerComponents(final BaseContainerStyle containerStyle) {
        this.pieceScale = 1.0;
        this.containerStyle = containerStyle;
    }
    
    public void drawComponents(final Graphics2D g2d, final Context context) {
        final List<TopologyElement> allGraphElements = GraphUtil.reorderGraphElementsTopDown(this.containerStyle.drawnGraphElements(), context);
        this.drawComponents(g2d, context, (ArrayList)allGraphElements);
    }
    
    public void drawComponents(final Graphics2D g2d, final Context context, final ArrayList<? extends TopologyElement> allGraphElements) {
        final State state = context.state();
        final Container container = this.containerStyle.container();
        final int cellRadiusPixels = this.containerStyle.cellRadiusPixels();
        if (container != null && state.containerStates().length > container.index()) {
            final ContainerState cs = state.containerStates()[container.index()];
            for (final TopologyElement graphElement : allGraphElements) {
                final Point2D posn = graphElement.centroid();
                final int site = graphElement.index();
                int what = cs.what(site, graphElement.elementType());
                int count = cs.count(site, graphElement.elementType());
                if (what != 0) {
                    if (what == -1) {
                        System.out.println("** GameView.drawState(): Couldn't find item for value " + what + ".");
                    } else {
                        for (int stackSize = cs.sizeStack(site, graphElement.elementType()), level = 0; level < stackSize; ++level) {
                            double transparency = 0.0;
                            final int rotation = 0;
                            what = cs.what(site, level, graphElement.elementType());
                            if (what != 0) {
                                final Component component = context.equipment().components()[what];
                                final Point drawPosn = this.containerStyle.screenPosn(posn);
                                int localState = cs.state(site, level, graphElement.elementType());
                                if (component.isDie()) {
                                    final FastArrayList<Move> moves = new FastArrayList<>(context.game().moves(context).moves());
                                    if (!moves.isEmpty()) {
                                        final ArrayList<Action> allSameActionsOld = new ArrayList<>(moves.get(0).actions());
                                        final ArrayList<Action> allSameActionsNew = new ArrayList<>();
                                        final ArrayList<Action> allSameActionsNew2 = new ArrayList<>();
                                        for (final Move m : moves) {
                                            boolean differentAction = false;
                                            for (int k = 0; k < allSameActionsOld.size(); ++k) {
                                                if (k >= m.actions().size() || allSameActionsOld.get(k) != m.actions().get(k)) {
                                                    differentAction = true;
                                                }
                                                if (!differentAction) {
                                                    allSameActionsNew.add(allSameActionsOld.get(k));
                                                }
                                            }
                                        }
                                        for (Action action : allSameActionsNew) {
                                            if (action instanceof ActionUpdateDice) {
                                                allSameActionsNew2.add(action);
                                            }
                                        }
                                        for (final Action a : allSameActionsNew2) {
                                            if (a.from() == site && a.state() != -1) {
                                                localState = a.state();
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (SettingsVC.selectedLocation.site() == site && SettingsVC.selectedLocation.level() == level && SettingsVC.selectedLocation.siteType() == graphElement.elementType()) {
                                    transparency = 0.5;
                                }
                                int imageSize = (int) (cellRadiusPixels * 2 * this.pieceScale() * Bridge.getComponentStyle(component.index()).scale());
                                if (container.index() > 0 && context.metadata().graphics().noHandScale()) {
                                    imageSize /= (int) Bridge.getComponentStyle(component.index()).scale();
                                }
                                imageSize = Math.max(imageSize, 2);
                                final PieceStackType componentStackType = context.metadata().graphics().stackType(container, context, site, graphElement.elementType(), localState);
                                final Point2D.Double stackOffset = ContainerUtil.calculateStackOffset(context, container, componentStackType, cellRadiusPixels, level, site, graphElement.elementType(), stackSize, localState);
                                final Point point = drawPosn;
                                point.x += (int) stackOffset.x;
                                final Point point2 = drawPosn;
                                point2.y += (int) stackOffset.y;
                                final Point point3 = drawPosn;
                                point3.x -= imageSize / 2;
                                final Point point4 = drawPosn;
                                point4.y -= imageSize / 2;
                                if (component.isLargePiece() && Bridge.getComponentStyle(component.index()).origin().size() > localState && container.index() == 0) {
                                    final Point origin = Bridge.getComponentStyle(component.index()).origin().get(localState);
                                    if (origin != null) {
                                        final Point point5 = drawPosn;
                                        point5.x -= origin.x;
                                        final Point point6 = drawPosn;
                                        point6.y -= origin.y;
                                    }
                                }
                                if (SettingsVC.pieceBeingDragged || SettingsVC.thisFrameIsAnimated) {
                                    Location location;
                                    if (SettingsVC.pieceBeingDragged) {
                                        location = SettingsVC.selectedLocation;
                                    } else {
                                        location = SettingsVC.animatedLocation;
                                    }
                                    if (location.equalsLoc(new FullLocation(site, level, graphElement.elementType())) || (location.site() == site && location.siteType() == graphElement.elementType() && location.level() < level && !componentStackType.midStackSelectionValid())) {
                                        if (count <= 1) {
                                            continue;
                                        }
                                        --count;
                                    }
                                }
                                if (component.isTile()) {
                                    for (final Integer cellIndex : ContainerUtil.cellsCoveredByPiece(context, container, component, site, localState)) {
                                        drawTilePiece(g2d, context, component, cellIndex, stackOffset.x, stackOffset.y, container, localState);
                                    }
                                }
                                Bridge.graphicsRenderer().drawComponent(g2d, context, new ImageInfo(drawPosn, site, level, graphElement.elementType(), component, localState, transparency, 0, container.index(), imageSize, count));
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void drawTilePiece(final Graphics2D g2d, final Context context, final Component component, final int site, final double stackOffsetX, final double stackOffsetY, final Container container, final int localState) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(0.0f, 1, 1));
        final GeneralPath path = new GeneralPath();
        final int containerSite = ContainerUtil.getContainerSite(context, site, SiteType.Cell);
        final Cell cellToFill = Bridge.getContainerStyle(container.index()).drawnCells().get(containerSite);
        Point nextPoint = Bridge.getContainerStyle(container.index()).screenPosn(cellToFill.vertices().get(0).centroid());
        path.moveTo(nextPoint.x + stackOffsetX, nextPoint.y + stackOffsetY);
        for (final Vertex vertex : cellToFill.vertices()) {
            nextPoint = Bridge.getContainerStyle(container.index()).screenPosn(vertex.centroid());
            path.lineTo(nextPoint.x + stackOffsetX, nextPoint.y + stackOffsetY);
        }
        path.closePath();
        g2d.draw(path);
        if (context.game().metadata().graphics().pieceFillColour(component.owner(), component.name(), context, localState) != null) {
            g2d.setColor(context.game().metadata().graphics().pieceFillColour(component.owner(), component.name(), context, localState));
        }
        else {
            g2d.setColor(SettingsColour.playerColour(component.owner(), context));
        }
        g2d.fill(path);
    }
    
    public void drawPuzzleValue(final int value, final int site, final Context context, final Graphics2D g2d, final Point drawPosn, final int imageSize) {
    }
    
    public double pieceScale() {
        return this.pieceScale;
    }
    
    public void setPieceScale(final double pieceScale) {
        this.pieceScale = pieceScale;
    }
}
