// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import app.DesktopApp;
import bridge.Bridge;
import bridge.PlatformGraphics;
import game.equipment.container.board.Board;
import game.types.board.SiteType;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import metadata.graphics.util.PieceStackType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.*;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;
import view.component.BaseComponentStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DesktopGraphics implements PlatformGraphics
{
    @Override
    public Location locationOfClickedImage(final Point pt) {
        final ArrayList<Location> overlappedLocations = new ArrayList<>();
        for (int imageIndex = 0; imageIndex < GraphicsCache.drawnImageInfo.size(); ++imageIndex) {
            final BufferedImage image = GraphicsCache.drawnImageInfo.get(imageIndex).pieceImage();
            final Point imageDrawPosn = GraphicsCache.drawnImageInfo.get(imageIndex).imageInfo().drawPosn();
            if (BufferedImageUtil.pointOverlapsImage(pt, image, imageDrawPosn)) {
                final int clickedIndex = GraphicsCache.drawnImageInfo.get(imageIndex).imageInfo().site();
                final int clickedLevel = GraphicsCache.drawnImageInfo.get(imageIndex).imageInfo().level();
                final SiteType clickedType = GraphicsCache.drawnImageInfo.get(imageIndex).imageInfo().graphElementType();
                overlappedLocations.add(new FullLocation(clickedIndex, clickedLevel, clickedType));
            }
        }
        if (overlappedLocations.size() == 1) {
            return overlappedLocations.get(0);
        }
        if (overlappedLocations.size() > 1) {
            Location highestLocation = null;
            int highestLevel = -1;
            for (final Location location : overlappedLocations) {
                if (location.level() > highestLevel) {
                    highestLevel = location.level();
                    highestLocation = location;
                }
            }
            return highestLocation;
        }
        return new FullLocation(-1, 0, SiteType.Cell);
    }
    
    @Override
    public void drawSVG(final Graphics2D g2d, final SVGGraphics2D svg, final ImageInfo imageInfo, final BaseComponentStyle componentStyle) {
        BufferedImage componentImage = SVGUtil.createSVGImage(svg.getSVGElement(), imageInfo.imageSize(), imageInfo.imageSize());
        if (componentStyle.flipHorizontal()) {
            componentImage = BufferedImageUtil.createFlippedHorizontally(componentImage);
        }
        if (componentStyle.flipVertical()) {
            componentImage = BufferedImageUtil.createFlippedVertically(componentImage);
        }
        if (componentStyle.rotationDegrees() > 0) {
            componentImage = BufferedImageUtil.rotateImageByDegrees(componentImage, componentStyle.rotationDegrees());
        }
        g2d.drawImage(componentImage, imageInfo.drawPosn().x - imageInfo.imageSize() / 2, imageInfo.drawPosn().y - imageInfo.imageSize() / 2, null);
    }
    
    @Override
    public void drawComponent(final Graphics2D g2d, final Context context, final ImageInfo imageInfo) {
        final State state = context.state();
        final ContainerState cs = state.containerStates()[imageInfo.containerIndex()];
        final int mover = this.getSingleHumanMover(state.mover(), context);
        if (cs.isInvisible(imageInfo.site(), imageInfo.level(), mover, imageInfo.graphElementType())) {
            return;
        }
        boolean hidden = false;
        if (cs.isMasked(imageInfo.site(), imageInfo.level(), mover, imageInfo.graphElementType())) {
            hidden = true;
        }
        final BufferedImage componentImage = GraphicsCache.getComponentImage(imageInfo.containerIndex(), imageInfo.component(), imageInfo.localState(), imageInfo.site(), imageInfo.level(), imageInfo.graphElementType(), imageInfo.imageSize(), context, hidden, false);
        GraphicsCache.drawPiece(g2d, context, componentImage, imageInfo.drawPosn(), imageInfo.site(), imageInfo.level(), imageInfo.graphElementType(), imageInfo.transparency());
        drawPieceCount(g2d, context, imageInfo, mover, cs);
    }
    
    private static void drawPieceCount(final Graphics2D g2d, final Context context, final ImageInfo imageInfo, final int mover, final ContainerState cs) {
        if (context.equipment().components()[cs.what(imageInfo.site(), imageInfo.level(), imageInfo.graphElementType())].isDomino()) {
            return;
        }
        if (imageInfo.count() > 1 && cs.isVisibleCell(imageInfo.site(), imageInfo.level(), mover)) {
            drawValue(g2d, imageInfo, Integer.toString(imageInfo.count()));
        }
        final int localState = cs.state(imageInfo.site(), imageInfo.level(), imageInfo.graphElementType());
        final PieceStackType componentStackType = context.metadata().graphics().stackType(context.equipment().containers()[imageInfo.containerIndex()], context, imageInfo.site(), imageInfo.graphElementType(), localState);
        if (imageInfo.count() > 1 && cs.isVisibleCell(imageInfo.site(), imageInfo.level(), mover)) {
            drawValue(g2d, imageInfo, Integer.toString(imageInfo.count()));
        }
        else if (componentStackType.equals(PieceStackType.Count) && cs.sizeStack(imageInfo.site(), imageInfo.graphElementType()) > 1) {
            drawValue(g2d, imageInfo, Integer.toString(cs.sizeStack(imageInfo.site(), imageInfo.graphElementType())));
        }
    }
    
    private static void drawValue(final Graphics2D g2d, final ImageInfo imageInfo, final String value) {
        g2d.setColor(Bridge.getComponentStyle(imageInfo.component().index()).getSecondaryColour());
        final String countString = "x" + value;
        final Rectangle2D countRect = g2d.getFont().getStringBounds(countString, g2d.getFontRenderContext());
        if (imageInfo.containerIndex() > 0) {
            final int drawPosnX = (int)(imageInfo.drawPosn().x + imageInfo.imageSize() / 2 - countRect.getWidth() / 2.0);
            final int drawPosnY = (int)(imageInfo.drawPosn().y + imageInfo.imageSize() + countRect.getHeight() / 2.0 * 1.5);
            g2d.drawString(countString, drawPosnX, drawPosnY);
        }
        else {
            g2d.setFont(SettingsVC.displayFont);
            final int drawPosnX = imageInfo.drawPosn().x + imageInfo.imageSize() / 2;
            final int drawPosnY = imageInfo.drawPosn().y + imageInfo.imageSize() / 2;
            StringUtil.drawStringAtPoint(g2d, countString, new Point2D.Double(drawPosnX, drawPosnY), true);
        }
    }
    
    @Override
    public void drawBoard(final Graphics2D g2d, final Rectangle2D boardDimensions) {
        if (GraphicsCache.boardImage == null) {
            final Context context = ContextSnapshot.getContext();
            final int resolution = DesktopApp.view().boardSize();
            final Board board = context.board();
            if (resolution > 0) {
                Bridge.getContainerStyle(board.index()).render(PlaneType.BOARD, context);
            }
            final String svg = Bridge.getContainerStyle(board.index()).containerSVGImage();
            if (svg == null) {
                return;
            }
            GraphicsCache.boardImage = SVGUtil.createSVGImage(svg, boardDimensions.getWidth(), boardDimensions.getHeight());
        }
        if (!ContextSnapshot.getContext().game().metadata().graphics().boardHidden()) {
            g2d.drawImage(GraphicsCache.boardImage, 0, 0, null);
        }
    }
    
    @Override
    public void drawGraph(final Graphics2D g2d, final Rectangle2D boardDimensions) {
        if (GraphicsCache.graphImage == null || ContextSnapshot.getContext().board().isBoardless()) {
            final Context context = ContextSnapshot.getContext();
            final int resolution = DesktopApp.view().boardSize();
            final Board board = context.board();
            if (resolution > 0) {
                Bridge.getContainerStyle(board.index()).render(PlaneType.GRAPH, context);
            }
            final String svg = Bridge.getContainerStyle(board.index()).graphSVGImage();
            if (svg == null) {
                return;
            }
            GraphicsCache.graphImage = SVGUtil.createSVGImage(svg, boardDimensions.getWidth(), boardDimensions.getHeight());
        }
        g2d.drawImage(GraphicsCache.graphImage, 0, 0, null);
    }
    
    @Override
    public void drawConnections(final Graphics2D g2d, final Rectangle2D boardDimensions) {
        if (GraphicsCache.connectionsImage == null || ContextSnapshot.getContext().board().isBoardless()) {
            final Context context = ContextSnapshot.getContext();
            final int resolution = DesktopApp.view().boardSize();
            final Board board = context.board();
            if (resolution > 0) {
                Bridge.getContainerStyle(board.index()).render(PlaneType.CONNECTIONS, context);
            }
            final String svg = Bridge.getContainerStyle(board.index()).dualSVGImage();
            if (svg == null) {
                return;
            }
            GraphicsCache.connectionsImage = SVGUtil.createSVGImage(svg, boardDimensions.getWidth(), boardDimensions.getHeight());
        }
        g2d.drawImage(GraphicsCache.connectionsImage, 0, 0, null);
    }
    
    @Override
    public int getSingleHumanMover(final int mover, final Context context) {
        int newMover = mover;
        final State state = context.state();
        if (context.game().isDeductionPuzzle()) {
            return mover;
        }
        if (SettingsNetwork.getNetworkPlayerNumber() > 0) {
            newMover = SettingsNetwork.getNetworkPlayerNumber();
        }
        else if (SettingsManager.hideAiMoves) {
            int humansFound = 0;
            int humanIndex = 0;
            for (int i = 1; i <= context.game().players().count(); ++i) {
                if (DesktopApp.aiSelected()[i].ai() == null) {
                    ++humansFound;
                    humanIndex = state.playerToAgent(i);
                }
            }
            if (humansFound == 1) {
                newMover = humanIndex;
            }
        }
        return newMover;
    }
}
