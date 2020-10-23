// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

import bridge.Bridge;
import game.rules.end.EndRule;
import game.rules.end.If;
import game.rules.end.Result;
import game.types.board.SiteType;
import game.types.play.ResultType;
import main.collections.FVector;
import main.collections.FastArrayList;
import manager.Manager;
import util.*;
import util.locations.Location;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class MoveVisuals
{
    public static void drawLastMove(final Graphics2D g2d, final Context context, final Rectangle passLocation, final Rectangle swapLocation, final Rectangle otherLocation) {
        final Move lastMove = context.currentInstanceContext().trial().lastMove();
        final int currentMover = context.state().mover();
        if (lastMove != null) {
            final int from = context.trial().lastMove().from();
            final int to = context.trial().lastMove().to();
            final SiteType fromType = lastMove.fromType();
            final SiteType toType = lastMove.toType();
            final int fromContainerIdx = ContainerUtil.getContainerId(context, from, fromType);
            final int toContainerIdx = ContainerUtil.getContainerId(context, to, toType);
            if (from != to) {
                final Point2D fromPosnWorld = Bridge.getContainerStyle(fromContainerIdx).drawnGraphElement(from, fromType).centroid();
                final Point2D ToPosnWorld = Bridge.getContainerStyle(toContainerIdx).drawnGraphElement(to, toType).centroid();
                final Point fromPosnScreen = Bridge.getContainerStyle(fromContainerIdx).screenPosn(fromPosnWorld);
                final Point toPosnScreen = Bridge.getContainerStyle(toContainerIdx).screenPosn(ToPosnWorld);
                final int fromX = fromPosnScreen.x;
                final int fromY = fromPosnScreen.y;
                final int toX = toPosnScreen.x;
                final int toY = toPosnScreen.y;
                final int maxRadius = Math.max(Bridge.getContainerStyle(fromContainerIdx).cellRadiusPixels(), Bridge.getContainerStyle(toContainerIdx).cellRadiusPixels());
                final int arrowWidth = Math.max((int)(maxRadius / 2.5), 1);
                g2d.setColor(new Color(1.0f, 1.0f, 0.0f, 0.5f));
                boolean arrowHidden = false;
                if (context.game().hiddenInformation() && (context.containerState(fromContainerIdx).isInvisible(from, currentMover, fromType) || context.containerState(toContainerIdx).isInvisible(to, currentMover, toType))) {
                    arrowHidden = true;
                }
                if (!arrowHidden) {
                    if (lastMove.isOrientedMove()) {
                        ArrowUtil.drawArrow(g2d, fromX, fromY, toX, toY, arrowWidth, Math.max(arrowWidth, 3), (int)(1.75 * Math.max(arrowWidth, 5)));
                    }
                    else {
                        ArrowUtil.drawArrow(g2d, fromX, fromY, toX, toY, arrowWidth, 0, 0);
                    }
                }
            }
            else if (to != -1) {
                final Point2D ToPosnWorld2 = Bridge.getContainerStyle(toContainerIdx).drawnGraphElement(to, toType).centroid();
                final Point toPosnScreen2 = Bridge.getContainerStyle(toContainerIdx).screenPosn(ToPosnWorld2);
                final int midX = toPosnScreen2.x;
                final int midY = toPosnScreen2.y;
                g2d.setColor(new Color(1.0f, 1.0f, 0.0f, 0.5f));
                final int radius = Bridge.getContainerStyle(toContainerIdx).cellRadiusPixels() / 2;
                g2d.fillOval(midX - radius, midY - radius, 2 * radius, 2 * radius);
            }
            else {
                Rectangle position = null;
                if (lastMove.isPass() || lastMove.containsNextInstance()) {
                    position = passLocation;
                }
                else if (lastMove.isSwap()) {
                    position = swapLocation;
                }
                else if (lastMove.isOtherMove()) {
                    position = otherLocation;
                }
                if (position != null) {
                    g2d.setColor(new Color(1.0f, 1.0f, 0.0f, 0.5f));
                    g2d.fillOval(position.x, position.y, position.width, position.height);
                }
            }
        }
    }
    
    public static void drawAIDistribution(final Graphics2D g2d, final Context context, final Rectangle passLocation, final Rectangle swapLocation, final Rectangle otherLocation) {
        if (!context.trial().over()) {
            if (Manager.liveAIs() == null) {
                return;
            }
            for (final AI visualisationAI : Manager.liveAIs()) {
                if (visualisationAI == null) {
                    continue;
                }
                final AI.AIVisualisationData visData = visualisationAI.aiVisualisationData();
                if (visData == null) {
                    continue;
                }
                final FVector aiDistribution = visData.searchEffort();
                final FVector valueEstimates = visData.valueEstimates();
                final FastArrayList<Move> moves = visData.moves();
                final float maxVal = aiDistribution.max();
                for (int i = 0; i < aiDistribution.dim(); ++i) {
                    final float val = aiDistribution.get(i);
                    float probRatio = 0.05f + 0.95f * val / maxVal;
                    if (probRatio > 1.0f) {
                        probRatio = 1.0f;
                    }
                    if (probRatio < -1.0f) {
                        probRatio = -1.0f;
                    }
                    final Move move = moves.get(i);
                    final int from = move.from();
                    final int to = move.to();
                    final SiteType fromType = move.fromType();
                    final SiteType toType = move.toType();
                    final int fromContainerIdx = ContainerUtil.getContainerId(context, from, fromType);
                    final int toContainerIdx = ContainerUtil.getContainerId(context, to, toType);
                    if (from != to) {
                        final Point2D fromPosnWorld = Bridge.getContainerStyle(fromContainerIdx).drawnGraphElement(from, fromType).centroid();
                        final Point2D ToPosnWorld = Bridge.getContainerStyle(toContainerIdx).drawnGraphElement(to, toType).centroid();
                        final Point fromPosnScreen = Bridge.getContainerStyle(fromContainerIdx).screenPosn(fromPosnWorld);
                        final Point toPosnScreen = Bridge.getContainerStyle(toContainerIdx).screenPosn(ToPosnWorld);
                        final int fromX = fromPosnScreen.x;
                        final int fromY = fromPosnScreen.y;
                        final int toX = toPosnScreen.x;
                        final int toY = toPosnScreen.y;
                        final int maxRadius = Math.max(Bridge.getContainerStyle(fromContainerIdx).cellRadiusPixels(), Bridge.getContainerStyle(toContainerIdx).cellRadiusPixels());
                        final int minRadius = maxRadius / 4;
                        final int arrowWidth = Math.max((int)((minRadius + probRatio * (maxRadius - minRadius)) / 2.5), 1);
                        if (valueEstimates != null) {
                            g2d.setColor(new Color(0.5f - 0.5f * valueEstimates.get(i), 0.0f, 0.5f + 0.5f * valueEstimates.get(i), 0.5f + 0.2f * probRatio * probRatio));
                        }
                        else {
                            g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f + 0.2f * probRatio * probRatio));
                        }
                        if (move.isOrientedMove()) {
                            ArrowUtil.drawArrow(g2d, fromX, fromY, toX, toY, arrowWidth, Math.max(arrowWidth, 3), (int)(1.75 * Math.max(arrowWidth, 5)));
                        }
                        else {
                            ArrowUtil.drawArrow(g2d, fromX, fromY, toX, toY, arrowWidth, 0, 0);
                        }
                    }
                    else if (to != -1) {
                        final int maxRadius2 = Bridge.getContainerStyle(toContainerIdx).cellRadiusPixels();
                        final int minRadius2 = maxRadius2 / 4;
                        final Point2D ToPosnWorld2 = Bridge.getContainerStyle(toContainerIdx).drawnGraphElement(to, toType).centroid();
                        final Point toPosnScreen = Bridge.getContainerStyle(toContainerIdx).screenPosn(ToPosnWorld2);
                        final int midX = toPosnScreen.x;
                        final int midY = toPosnScreen.y;
                        final int radius = (int)(minRadius2 + probRatio * (maxRadius2 - minRadius2));
                        if (valueEstimates != null) {
                            g2d.setColor(new Color(0.5f - 0.5f * valueEstimates.get(i), 0.0f, 0.5f + 0.5f * valueEstimates.get(i), 0.5f + 0.2f * probRatio));
                        }
                        else {
                            g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f + 0.2f * probRatio));
                        }
                        g2d.fillOval(midX - radius, midY - radius, 2 * radius, 2 * radius);
                    }
                    else if (move.isPass() || move.isSwap() || move.isOtherMove() || move.containsNextInstance()) {
                        Rectangle position = null;
                        if (move.isPass() || move.containsNextInstance()) {
                            position = passLocation;
                        }
                        else if (move.isSwap()) {
                            position = swapLocation;
                        }
                        else {
                            position = otherLocation;
                        }
                        if (position != null) {
                            final int maxRadius3 = Math.min(position.width, position.height) / 2;
                            final int minRadius3 = maxRadius3 / 4;
                            final int midX2 = (int)position.getCenterX();
                            final int midY2 = (int)position.getCenterY();
                            final int radius2 = (int)(minRadius3 + probRatio * (maxRadius3 - minRadius3));
                            if (valueEstimates != null) {
                                g2d.setColor(new Color(0.5f - 0.5f * valueEstimates.get(i), 0.0f, 0.5f + 0.5f * valueEstimates.get(i), 0.5f + 0.2f * probRatio));
                            }
                            else {
                                g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f + 0.2f * probRatio));
                            }
                            g2d.fillOval(midX2 - radius2, midY2 - radius2, 2 * radius2, 2 * radius2);
                        }
                    }
                }
            }
        }
    }
    
    public static void drawEndingMove(final Graphics2D g2d, final Context context) {
        final Context copyContext = new Context(context);
        copyContext.state().setMover(context.state().prev());
        copyContext.state().setNext(context.state().mover());
        for (final EndRule endRule : context.game().endRules().endRules()) {
            if (endRule instanceof If && endRule.result() != null && endRule.result().result() != null) {
                final List<Location> endingLocations = ((If)endRule).endCondition().satisfyingSites(copyContext);
                for (final Location location : endingLocations) {
                    drawEndingMoveLocation(g2d, context, endRule.result(), location);
                }
                if (endingLocations.size() > 0) {
                    break;
                }
            }
        }
    }
    
    private static void drawEndingMoveLocation(final Graphics2D g2d, final Context context, final Result result, final Location location) {
        Color colour = new Color(0.0f, 0.0f, 1.0f, 0.5f);
        if (result.result().equals(ResultType.Win)) {
            colour = new Color(0.0f, 1.0f, 0.0f, 0.7f);
        }
        else if (result.result().equals(ResultType.Loss)) {
            colour = new Color(1.0f, 0.0f, 0.0f, 0.7f);
        }
        final int site = location.site();
        final SiteType type = location.siteType();
        final int containerIdx = ContainerUtil.getContainerId(context, site, type);
        final Point2D ToPosnWorld = Bridge.getContainerStyle(containerIdx).drawnGraphElement(site, type).centroid();
        final Point toPosnScreen = Bridge.getContainerStyle(containerIdx).screenPosn(ToPosnWorld);
        final int midX = toPosnScreen.x;
        final int midY = toPosnScreen.y;
        g2d.setColor(Color.BLACK);
        int radius = (int)(Bridge.getContainerStyle(containerIdx).cellRadiusPixels() / 2 * 1.1) + 2;
        g2d.fillOval(midX - radius, midY - radius, 2 * radius, 2 * radius);
        g2d.setColor(colour);
        radius = Bridge.getContainerStyle(containerIdx).cellRadiusPixels() / 2;
        g2d.fillOval(midX - radius, midY - radius, 2 * radius, 2 * radius);
    }
}
