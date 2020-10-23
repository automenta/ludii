// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import bridge.Bridge;
import game.equipment.component.Component;
import game.equipment.container.other.Dice;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import util.ImageInfo;
import util.Move;
import util.action.Action;
import util.action.die.ActionUpdateDice;
import util.action.die.ActionUseDie;
import view.component.ComponentStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphicsCache
{
    private static ArrayList<ArrayList<ArrayList<ArrayList<BufferedImage>>>> allComponentImages;
    private static ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> allComponentImagesSizes;
    private static ArrayList<ArrayList<ArrayList<ArrayList<BufferedImage>>>> allComponentImagesSecondary;
    private static ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> allComponentImagesSecondarySizes;
    public static BufferedImage boardImage;
    public static BufferedImage graphImage;
    public static BufferedImage connectionsImage;
    public static BufferedImage[] allPlayerOnlineDiscs;
    public static BufferedImage loginDisc;
    public static ArrayList<DrawnImageInfo> drawnImageInfo;
    
    public static BufferedImage getComponentImage(final int containerId, final Component component, final int localState, final int site, final int level, final SiteType graphElementType, final int imageSize, final Context context, final boolean masked, final boolean secondary) {
        final int componentId = component.index();
        final int maskedValue = masked ? 1 : 0;
        final ComponentStyle componentStyle = Bridge.getComponentStyle(component.index());
        final ArrayList<ArrayList<ArrayList<ArrayList<BufferedImage>>>> componentImageArray = setupCache(containerId, component, localState, secondary, GraphicsCache.allComponentImages, GraphicsCache.allComponentImagesSecondary);
        final ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> componentImageSizeArray = setupCache(containerId, component, localState, secondary, GraphicsCache.allComponentImagesSizes, GraphicsCache.allComponentImagesSecondarySizes);
        final ArrayList<BufferedImage> containerComponentStateMaskedImages = componentImageArray.get(containerId).get(componentId).get(localState);
        final ArrayList<Integer> containerComponentStateMaskedImagesSizes = componentImageSizeArray.get(containerId).get(componentId).get(localState);
        if (containerComponentStateMaskedImages.size() <= maskedValue || containerComponentStateMaskedImages.get(maskedValue) == null || containerComponentStateMaskedImagesSizes.get(maskedValue) != imageSize) {
            while (containerComponentStateMaskedImages.size() <= maskedValue) {
                containerComponentStateMaskedImages.add(null);
                containerComponentStateMaskedImagesSizes.add(0);
            }
            for (int i = componentStyle.getAllImageSVGs().size(); i <= maskedValue; ++i) {
                componentStyle.setImageSVG(i, null);
            }
            if (containerId > 0 && component.isLargePiece()) {
                componentStyle.renderImageSVG(context, Bridge.getContainerStyle(0).cellRadiusPixels() * 2, localState, true, maskedValue);
            }
            else {
                componentStyle.renderImageSVG(context, imageSize, localState, secondary, maskedValue);
            }
            final SVGGraphics2D svg = componentStyle.getImageSVG(localState);
            final BufferedImage componentImage = getComponentBufferedImage(svg, component, componentStyle, context, containerId, imageSize, localState, secondary);
            containerComponentStateMaskedImages.set(maskedValue, componentImage);
            containerComponentStateMaskedImagesSizes.set(maskedValue, imageSize);
        }
        if (component.isDie()) {
            return getDiceImage(containerId, component, localState, site, context, componentId, containerComponentStateMaskedImages);
        }
        if (context.containerState(containerId).rotation(site, level, graphElementType) != 0) {
            final int maxRotation = 360 / context.game().maximalRotationStates();
            final BufferedImage rotatedImage = BufferedImageUtil.rotateImageByDegrees(containerComponentStateMaskedImages.get(maskedValue), context.containerState(containerId).rotation(site, level, graphElementType) * maxRotation);
            return rotatedImage;
        }
        return containerComponentStateMaskedImages.get(maskedValue);
    }
    
    private static <T> ArrayList<ArrayList<ArrayList<ArrayList<T>>>> setupCache(final int containerId, final Component component, final int localState, final boolean secondary, final ArrayList<ArrayList<ArrayList<ArrayList<T>>>> allComponentImages2, final ArrayList<ArrayList<ArrayList<ArrayList<T>>>> allComponentImagesSecondary2) {
        final int componentId = component.index();
        ArrayList<ArrayList<ArrayList<ArrayList<T>>>> componentImageArray;
        if (secondary && component.isTile()) {
            componentImageArray = allComponentImagesSecondary2;
        }
        else {
            componentImageArray = allComponentImages2;
        }
        while (componentImageArray.size() <= containerId) {
            componentImageArray.add(new ArrayList<>());
        }
        while (componentImageArray.get(containerId).size() <= componentId) {
            componentImageArray.get(containerId).add(new ArrayList<>());
        }
        while (componentImageArray.get(containerId).get(componentId).size() <= localState) {
            componentImageArray.get(containerId).get(componentId).add(new ArrayList<>());
        }
        return componentImageArray;
    }
    
    private static BufferedImage getComponentBufferedImage(final SVGGraphics2D svg, final Component component, final ComponentStyle componentStyle, final Context context, final int containerId, final int imageSize, final int localState, final boolean secondary) {
        BufferedImage componentImage = null;
        if (svg != null) {
            if (component.isLargePiece()) {
                componentImage = SVGUtil.createSVGImage(svg.getSVGDocument(), componentStyle.largePieceSize().x, componentStyle.largePieceSize().y);
                if (containerId != 0) {
                    final int maxSize = Math.max(componentStyle.largePieceSize().x, componentStyle.largePieceSize().y);
                    final double scaleFactor = 0.9 * imageSize / maxSize;
                    componentImage = BufferedImageUtil.resize(componentImage, (int)(scaleFactor * componentStyle.largePieceSize().x), (int)(scaleFactor * componentStyle.largePieceSize().y));
                }
            }
            else {
                componentImage = SVGUtil.createSVGImage(svg.getSVGDocument(), imageSize, imageSize);
            }
            if (componentStyle.flipHorizontal()) {
                componentImage = BufferedImageUtil.createFlippedHorizontally(componentImage);
            }
            if (componentStyle.flipVertical()) {
                componentImage = BufferedImageUtil.createFlippedVertically(componentImage);
            }
            if (componentStyle.rotationDegrees() > 0) {
                componentImage = BufferedImageUtil.rotateImageByDegrees(componentImage, componentStyle.rotationDegrees());
            }
        }
        return componentImage;
    }
    
    private static BufferedImage getDiceImage(final int containerId, final Component component, final int localState, final int site, final Context context, final int componentId, final ArrayList<BufferedImage> containerComponentStateMaskedImages) {
        int handDiceIndex = -1;
        for (int j = 0; j < context.handDice().size(); ++j) {
            final Dice dice = context.handDice().get(j);
            if (dice.index() == containerId) {
                handDiceIndex = j;
                break;
            }
        }
        int previousValue = context.state().currentDice()[handDiceIndex][site - context.sitesFrom()[containerId]];
        int stateValue = localState;
        final Moves moves = context.game().moves(context);
        boolean useDieDetected = false;
        if (!moves.moves().isEmpty()) {
            final ArrayList<Action> allSameActions = new ArrayList<>(moves.moves().get(0).actions());
            for (final Move m : moves.moves()) {
                boolean differentAction = false;
                for (int i = allSameActions.size() - 1; i >= 0 && m.actions().size() > i; --i) {
                    if (allSameActions.get(i) != m.actions().get(i)) {
                        differentAction = true;
                    }
                    if (differentAction) {
                        allSameActions.remove(i);
                    }
                }
            }
            for (final Move m : moves.moves()) {
                for (final Action action : m.actions()) {
                    if (action instanceof ActionUseDie) {
                        useDieDetected = true;
                        break;
                    }
                }
                if (useDieDetected) {
                    break;
                }
            }
            for (int k = 0; k < allSameActions.size(); ++k) {
                if (!(allSameActions.get(k) instanceof ActionUpdateDice)) {
                    allSameActions.remove(k);
                }
            }
            final int loc = context.sitesFrom()[containerId] + site;
            for (final Action a : allSameActions) {
                if (a.from() == loc && stateValue != -1) {
                    stateValue = a.state();
                    previousValue = context.components()[component.index()].getFaces()[stateValue];
                }
            }
        }
        if (context.state().mover() == context.state().prev() && previousValue == 0 && useDieDetected) {
            return BufferedImageUtil.makeImageTranslucent(containerComponentStateMaskedImages.get(0), 0.2);
        }
        return containerComponentStateMaskedImages.get(0);
    }
    
    public static void drawPiece(final Graphics2D g2d, final Context context, final BufferedImage pieceImage, final Point posn, final int site, final int level, final SiteType type, final double transparency) {
        BufferedImage imageToDraw = pieceImage;
        if (transparency != 0.0) {
            imageToDraw = BufferedImageUtil.makeImageTranslucent(pieceImage, transparency);
        }
        final ImageInfo imageInfo = new ImageInfo(posn, site, level, type);
        GraphicsCache.drawnImageInfo.add(new DrawnImageInfo(pieceImage, imageInfo));
        g2d.drawImage(imageToDraw, posn.x, posn.y, null);
    }
    
    public static void clearAllCachedImages() {
        GraphicsCache.allComponentImages.clear();
        GraphicsCache.boardImage = null;
        GraphicsCache.graphImage = null;
        GraphicsCache.connectionsImage = null;
        GraphicsCache.allPlayerOnlineDiscs = new BufferedImage[17];
        GraphicsCache.loginDisc = null;
        GraphicsCache.drawnImageInfo.clear();
    }
    
    public static BufferedImage getComponentImage(final int containerId, final int componentId, final int localState, final int masked) {
        return GraphicsCache.allComponentImages.get(containerId).get(componentId).get(localState).get(masked);
    }
    
    static {
        GraphicsCache.allComponentImages = new ArrayList<>();
        GraphicsCache.allComponentImagesSizes = new ArrayList<>();
        GraphicsCache.allComponentImagesSecondary = new ArrayList<>();
        GraphicsCache.allComponentImagesSecondarySizes = new ArrayList<>();
        GraphicsCache.boardImage = null;
        GraphicsCache.graphImage = null;
        GraphicsCache.connectionsImage = null;
        GraphicsCache.allPlayerOnlineDiscs = new BufferedImage[17];
        GraphicsCache.loginDisc = null;
        GraphicsCache.drawnImageInfo = new ArrayList<>();
    }
}
