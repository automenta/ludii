// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.util.BufferedImageUtil;
import app.display.util.GUIUtil;
import app.display.util.GraphicsCache;
import app.display.views.tools.ToolView;
import bridge.Bridge;
import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import graphics.ImageProcessing;
import manager.Manager;
import manager.network.SettingsNetwork;
import manager.utils.AnimationUtil;
import manager.utils.ContextSnapshot;
import manager.utils.MoveVisuals;
import manager.utils.SettingsManager;
import metadata.graphics.util.PieceStackType;
import topology.TopologyElement;
import util.ContainerUtil;
import util.Context;
import util.Move;
import util.SettingsVC;
import util.action.Action;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public final class OverlayView extends View
{
    protected Font fontForDisplay;
    
    @Override
    public void paint(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final ToolView toolview = MainWindow.toolPanel();
        final Rectangle passRect = toolview.buttons.get(0).rect();
        final Rectangle swapRect = toolview.buttons.get(1).rect();
        final Rectangle otherRect = toolview.buttons.get(2).rect();
        final Context context = ContextSnapshot.getContext();
        if (SettingsVC.sandboxMode) {
            this.drawSandBoxIcon(g2d);
        }
        drawLoginDisc(g2d);
        if (SettingsVC.thisFrameIsAnimated) {
            moveAnimation(g2d, AnimationUtil.animationFromLocation, AnimationUtil.animationToLocation, AnimationUtil.drawingMovingPieceTime);
        }
        else {
            this.calculateFont();
            if (Manager.liveAIs() != null && SettingsManager.showAIDistribution) {
                MoveVisuals.drawAIDistribution(g2d, context, passRect, swapRect, otherRect);
            }
            if (SettingsManager.showLastMove && context.currentInstanceContext().trial().numMoves() > context.currentInstanceContext().trial().numInitialPlacementMoves()) {
                MoveVisuals.drawLastMove(g2d, context, passRect, swapRect, otherRect);
            }
            if (SettingsManager.showEndingMove && context.currentInstanceContext().trial().moveNumber() > 0 && context.game().endRules() != null) {
                MoveVisuals.drawEndingMove(g2d, context);
            }
            if (SettingsVC.selectedLocation.site() != -1 && !context.trial().over() && SettingsVC.pieceBeingDragged && DesktopApp.view().getMousePosition() != null) {
                drawMovePiece(g2d, SettingsVC.selectedLocation, DesktopApp.view().getMousePosition().x, DesktopApp.view().getMousePosition().y, 0.0, false);
            }
        }
        this.drawExtraGameInformation(g2d, context);
        this.paintDebug(g2d, Color.BLACK);
    }
    
    private void drawSandBoxIcon(final Graphics2D g2d) {
        final URL resource = this.getClass().getResource("/sandbox.png");
        try {
            BufferedImage sandboxImage = ImageIO.read(resource);
            sandboxImage = BufferedImageUtil.resize(sandboxImage, this.placement.height / 15, this.placement.height / 15);
            g2d.drawImage(sandboxImage, sandboxImage.getWidth() / 10, sandboxImage.getHeight() / 10, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected static void drawLoginDisc(final Graphics2D g2d) {
        final int r = 7;
        final Color markerColour = (SettingsNetwork.getLoginId() == 0) ? Color.RED : Color.GREEN;
        ImageProcessing.ballImage(g2d, DesktopApp.view().getWidth() - 14 - 7, 7, 7, markerColour);
    }
    
    private void calculateFont() {
        int maxVertices = 0;
        int maxEdges = 0;
        int maxFaces = 0;
        final Context context = ContextSnapshot.getContext();
        for (int i = 0; i < context.numContainers(); ++i) {
            final Container container = context.equipment().containers()[i];
            maxVertices += container.topology().cells().size();
            maxEdges += container.topology().edges().size();
            maxFaces += container.topology().vertices().size();
        }
        final int maxDisplayNumber = Math.max(maxVertices, Math.max(maxEdges, maxFaces));
        int fontSize;
        final int fontMultiplier = fontSize = (int)(Bridge.getContainerStyle(context.board().index()).cellRadius() * 2.0 * DesktopApp.view().boardSize());
        if (maxDisplayNumber > 9) {
            fontSize = fontMultiplier / 2;
        }
        if (maxDisplayNumber > 99) {
            fontSize = fontMultiplier / 3;
        }
        if (maxDisplayNumber > 999) {
            fontSize = fontMultiplier / 4;
        }
        this.fontForDisplay = new Font("Arial", 1, fontSize);
    }
    
    private void drawExtraGameInformation(final Graphics2D g2d, final Context context) {
        if (MainWindow.volatileMessage().length() > 0) {
            this.drawStringBelowBoard(g2d, MainWindow.volatileMessage(), 0.98);
        }
        else if (MainWindow.temporaryMessage().length() > 0) {
            this.drawStringBelowBoard(g2d, MainWindow.temporaryMessage(), 0.98);
        }
        if (context.game().requiresBet()) {
            final String str = "Pot: $" + context.state().pot();
            this.drawStringBelowBoard(g2d, str, 0.95);
        }
    }
    
    private void drawStringBelowBoard(final Graphics2D g2d, final String message, final double percentageBelow) {
        final int pixels = MainWindow.getStatePanel().placement.width;
        final Font font = new Font("Arial", 0, 16);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(message, g2d);
        g2d.drawString(message, (int)(0.5 * pixels - bounds.getWidth() / 2.0), (int)(percentageBelow * pixels + this.placement.y * 2));
    }
    
    static void drawMovePiece(final Graphics2D g2d, final Location selectedLocation, final int x, final int y, final double transparency, final boolean drawingAnimation) {
        final Context context = ContextSnapshot.getContext();
        final Game game = context.game();
        for (int i = 0; i < context.numContainers(); ++i) {
            final Container container = context.equipment().containers()[i];
            final int containerIndex = container.index();
            final List<TopologyElement> graphElements = Bridge.getContainerStyle(containerIndex).drawnGraphElements();
            final State state = ContextSnapshot.getContext().state();
            final ContainerState cs = state.containerStates()[i];
            for (final TopologyElement graphElement : graphElements) {
                if (graphElement.index() == selectedLocation.site() && graphElement.elementType() == selectedLocation.siteType()) {
                    int lowestSelectedLevel = -1;
                    for (int level = selectedLocation.level(); level < 32; ++level) {
                        final int localState = cs.state(selectedLocation.site(), level, selectedLocation.siteType());
                        final PieceStackType componentStackType = context.metadata().graphics().stackType(container, context, selectedLocation.site(), selectedLocation.siteType(), localState);
                        if (level > selectedLocation.level() && componentStackType != null && componentStackType.midStackSelectionValid()) {
                            break;
                        }
                        int what = cs.what(graphElement.index(), level, graphElement.elementType());
                        if (what == 0) {
                            for (final Move m : context.game().moves(context).moves()) {
                                if (m.getFromLocation().equalsLoc(selectedLocation)) {
                                    for (final Action a : m.actions()) {
                                        final Location actionLocationA = new FullLocation(a.from(), a.levelFrom(), a.fromType());
                                        final Location actionLocationB = new FullLocation(a.to(), a.levelTo(), a.toType());
                                        final Location testingLocation = new FullLocation(selectedLocation.site(), level, selectedLocation.siteType());
                                        if (actionLocationA.equalsLoc(testingLocation) && actionLocationB.equalsLoc(testingLocation)) {
                                            what = a.what();
                                            break;
                                        }
                                    }
                                }
                                if (what != 0) {
                                    break;
                                }
                            }
                        }
                        if (what <= 0) {
                            break;
                        }
                        SettingsManager.dragComponent = ContextSnapshot.getContext().equipment().components()[what];
                        try {
                            if (lowestSelectedLevel == -1) {
                                lowestSelectedLevel = level;
                            }
                            if (drawingAnimation) {
                                lowestSelectedLevel = 0;
                            }
                            BufferedImage pieceImage = null;
                            final int cellSize = Bridge.getContainerStyle(0).cellRadiusPixels();
                            final int imageSize = (int)(cellSize * 2 * Bridge.getContainerStyle(containerIndex).pieceScale() * Bridge.getComponentStyle(SettingsManager.dragComponent.index()).scale());
                            if (SettingsManager.dragComponent.isLargePiece()) {
                                if (cs.state(graphElement.index(), graphElement.elementType()) + MainWindow.currentWalkExtra >= SettingsManager.dragComponent.walk().length * (ContextSnapshot.getContext().board().topology().supportedDirections(SiteType.Cell).size() / 2)) {
                                    MainWindow.currentWalkExtra = -cs.state(graphElement.index(), graphElement.elementType());
                                }
                                pieceImage = GraphicsCache.getComponentImage(0, SettingsManager.dragComponent, cs.state(graphElement.index(), graphElement.elementType()) + MainWindow.currentWalkExtra, graphElement.index(), 0, graphElement.elementType(), imageSize, ContextSnapshot.getContext(), false, true);
                                SettingsManager.dragComponentState = cs.state(graphElement.index(), graphElement.elementType()) + MainWindow.currentWalkExtra;
                            }
                            else {
                                pieceImage = GraphicsCache.getComponentImage(i, SettingsManager.dragComponent, cs.state(graphElement.index(), graphElement.elementType()), graphElement.index(), 0, graphElement.elementType(), imageSize, ContextSnapshot.getContext(), false, true);
                            }
                            final Point2D.Double dragPosition = new Point2D.Double(x - pieceImage.getWidth() / 2, y - pieceImage.getHeight() / 2);
                            final int stackSize = cs.sizeStack(selectedLocation.site(), selectedLocation.siteType());
                            final Point2D.Double offsetDistance = ContainerUtil.calculateStackOffset(context, container, componentStackType, cellSize, level - lowestSelectedLevel, selectedLocation.site(), selectedLocation.siteType(), stackSize, localState);
                            if (transparency > 0.0) {
                                pieceImage = BufferedImageUtil.makeImageTranslucent(pieceImage, transparency);
                            }
                            g2d.drawImage(pieceImage, (int)(dragPosition.x + offsetDistance.x), (int)(dragPosition.y + offsetDistance.y), null);
                            if (!game.isStacking()) {
                                return;
                            }
                        }
                        catch (NullPointerException ex) {}
                    }
                    return;
                }
            }
        }
    }
    
    public static void moveAnimation(final Graphics2D g2d, final Location moveFrom, final Location moveTo, final int time) {
        final Context context = ContextSnapshot.getContext();
        try {
            Point startPoint = null;
            Point endPoint = null;
            if (moveFrom.site() != moveTo.site()) {
                final int containerIdFrom = ContainerUtil.getContainerId(context, moveFrom.site(), moveFrom.siteType());
                final int containerIdTo = ContainerUtil.getContainerId(context, moveTo.site(), moveTo.siteType());
                final Point2D graphPointStart = Bridge.getContainerStyle(containerIdFrom).drawnGraphElement(moveFrom.site(), moveFrom.siteType()).centroid();
                final Point2D graphEndStart = Bridge.getContainerStyle(containerIdTo).drawnGraphElement(moveTo.site(), moveTo.siteType()).centroid();
                startPoint = Bridge.getContainerStyle(containerIdFrom).screenPosn(graphPointStart);
                endPoint = Bridge.getContainerStyle(containerIdTo).screenPosn(graphEndStart);
                final Point2D.Double pointOnTimeLine = new Point2D.Double();
                double multiplyFactor = time / 30.0;
                multiplyFactor = (Math.cos(multiplyFactor * 3.141592653589793 + 3.141592653589793) + 1.0) / 2.0;
                pointOnTimeLine.x = startPoint.x + (endPoint.x - startPoint.x) * multiplyFactor;
                pointOnTimeLine.y = startPoint.y + (endPoint.y - startPoint.y) * multiplyFactor;
                drawMovePiece(g2d, moveFrom, (int)pointOnTimeLine.x, (int)pointOnTimeLine.y, 0.0, true);
            }
            else {
                double currentflashValue = 0.0;
                final int flashCycleValue = AnimationUtil.drawingMovingPieceTime % 20;
                if (flashCycleValue >= 10) {
                    currentflashValue = 1.0 - AnimationUtil.drawingMovingPieceTime % 10 / 10.0;
                }
                else {
                    currentflashValue = AnimationUtil.drawingMovingPieceTime % 10 / 10.0;
                }
                if (!AnimationUtil.fadeIn) {
                    currentflashValue = 1.0 - currentflashValue;
                }
                final Point2D graphPointStart2 = Bridge.getContainerStyle(0).drawnGraphElement(moveFrom.site(), moveFrom.siteType()).centroid();
                endPoint = (startPoint = Bridge.getContainerStyle(0).screenPosn(graphPointStart2));
                drawMovePiece(g2d, moveFrom, startPoint.x, startPoint.y, currentflashValue, true);
            }
            GUIUtil.repaintComponentBetweenPoints(context, moveFrom, startPoint, endPoint);
        }
        catch (Exception e) {
            AnimationUtil.drawingMovingPieceTime = 30;
        }
    }
}
