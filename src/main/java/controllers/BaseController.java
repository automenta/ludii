// 
// Decompiled by Procyon v0.5.36
// 

package controllers;

import bridge.Bridge;
import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import main.math.MathRoutines;
import topology.Cell;
import topology.Edge;
import topology.Vertex;
import util.*;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;
import view.container.ContainerStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class BaseController implements Controller
{
    protected final Container container;
    
    public BaseController(final Container container) {
        this.container = container;
    }
    
    @Override
    public Location calculateNearestLocationAll(final Context context, final Point pt) {
        final ArrayList<WorldLocation> allLocations = new ArrayList<>();
        final ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
        if (this.container.index() == 0 && (context.isVertexGame() || context.isEdgeGame())) {
            for (final Vertex v : containerStyle.drawnVertices()) {
                final ContainerState cs = context.state().containerStates()[this.container.index()];
                for (int stackSize = cs.sizeStackVertex(v.index()), i = 0; i <= stackSize; ++i) {
                    allLocations.add(new WorldLocation(new FullLocation(v.index(), i, SiteType.Vertex), v.centroid()));
                }
            }
        }
        if (this.container.index() == 0 && context.isEdgeGame()) {
            for (final Edge e : containerStyle.drawnEdges()) {
                final ContainerState cs = context.state().containerStates()[this.container.index()];
                for (int stackSize = cs.sizeStackEdge(e.index()), i = 0; i <= stackSize; ++i) {
                    allLocations.add(new WorldLocation(new FullLocation(e.index(), i, SiteType.Edge), e.centroid()));
                }
            }
        }
        if (context.isCellGame()) {
            for (final Cell f : containerStyle.drawnCells()) {
                final ContainerState cs = context.state().containerStates()[this.container.index()];
                for (int stackSize = cs.sizeStackCell(f.index()), i = 0; i <= stackSize; ++i) {
                    allLocations.add(new WorldLocation(new FullLocation(f.index(), i, SiteType.Cell), f.centroid()));
                }
            }
        }
        return this.translateClicktoSite(pt, context, allLocations);
    }
    
    @Override
    public Location calculateNearestLocation(final Context context, final Point pt, final boolean moveFrom) {
        final Game game = context.game();
        final ArrayList<WorldLocation> legalLocations = new ArrayList<>();
        if (SettingsVC.selectedLocation.site() != -1 && this.container.index() == ContainerUtil.getContainerId(context, SettingsVC.selectedLocation.site(), SettingsVC.selectedLocation.siteType())) {
            legalLocations.add(new WorldLocation(new FullLocation(SettingsVC.selectedLocation.site(), SettingsVC.selectedLocation.level(), SettingsVC.selectedLocation.siteType()), this.calculateMovePosition(context, SettingsVC.selectedLocation)));
        }
        for (final Move m : game.moves(context).moves()) {
            if (m.to() != -1) {
                if (m.from() == -1) {
                    continue;
                }
                if (moveFrom && this.container.index() != ContainerUtil.getContainerId(context, m.from(), m.fromType())) {
                    continue;
                }
                if (!moveFrom && this.container.index() != ContainerUtil.getContainerId(context, m.to(), m.toType())) {
                    continue;
                }
                final Point2D movePosition = this.calculateMovePosition(context, m, moveFrom);
                if (movePosition == null) {
                    continue;
                }
                int moveIndex;
                int moveLevel;
                SiteType moveType;
                if (moveFrom) {
                    moveIndex = m.from();
                    moveLevel = m.levelFrom();
                    moveType = m.fromType();
                }
                else {
                    moveIndex = m.to();
                    moveLevel = m.levelTo();
                    moveType = m.toType();
                }
                legalLocations.add(new WorldLocation(new FullLocation(moveIndex, moveLevel, moveType), movePosition));
                if (!isEdgeMove(m)) {
                    continue;
                }
                Vertex v1;
                Vertex v2;
                if (moveFrom) {
                    v1 = this.container.topology().edges().get(m.from()).vA();
                    v2 = this.container.topology().edges().get(m.from()).vB();
                }
                else {
                    v1 = this.container.topology().edges().get(m.from()).vB();
                    v2 = this.container.topology().edges().get(m.from()).vA();
                }
                legalLocations.add(new WorldLocation(new FullLocation(v1.index(), 0, SiteType.Vertex), v1.centroid()));
                if (m.isOrientedMove()) {
                    continue;
                }
                legalLocations.add(new WorldLocation(new FullLocation(v2.index(), 0, SiteType.Vertex), v2.centroid()));
            }
        }
        SettingsVC.lastClickedSite = this.calculateNearestLocationAll(context, pt);
        Location location = this.translateClicktoSite(pt, context, legalLocations);
        if (location.site() == -1) {
            location = SettingsVC.lastClickedSite;
        }
        return location;
    }
    
    private double calculateFurthestDistance(final Context context) {
        final Game game = context.game();
        double furthestPossibleDistance = 0.0;
        ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
        if (containerStyle.ignorePieceSelectionLimit() || game.hasLargePiece()) {
            furthestPossibleDistance = Math.max(containerStyle.placement().getWidth(), containerStyle.placement().getHeight());
        }
        else {
            containerStyle = Bridge.getContainerStyle(this.container.index());
            final double cellDistance = containerStyle.cellRadiusPixels() * 0.75;
            furthestPossibleDistance = Math.max(furthestPossibleDistance, cellDistance);
        }
        return furthestPossibleDistance;
    }
    
    protected Location translateClicktoSite(final Point pt, final Context context, final ArrayList<WorldLocation> validLocations) {
        Location location = Bridge.graphicsRenderer().locationOfClickedImage(pt);
        for (final WorldLocation w : validLocations) {
            if (w.location().equalsLoc(location)) {
                return location;
            }
        }
        final ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
        final double furthestPossibleDistance = this.calculateFurthestDistance(context) * 2.0;
        double minDist = 1000.0;
        for (int i = 0; i < validLocations.size(); ++i) {
            double dist = 99999.0;
            final int site = validLocations.get(i).location().site();
            if (validLocations.get(i).location().siteType() == SiteType.Edge) {
                if (validLocations.get(i).location().site() < context.board().topology().edges().size()) {
                    final Vertex va = context.board().topology().edges().get(validLocations.get(i).location().site()).vA();
                    final Vertex vb = context.board().topology().edges().get(validLocations.get(i).location().site()).vB();
                    final Point vaPoint = containerStyle.screenPosn(containerStyle.drawnVertices().get(va.index()).centroid());
                    final Point vbPoint = containerStyle.screenPosn(containerStyle.drawnVertices().get(vb.index()).centroid());
                    final Point2D.Double vaPointDouble = new Point2D.Double(vaPoint.getX(), vaPoint.getY());
                    final Point2D.Double vbPointDouble = new Point2D.Double(vbPoint.getX(), vbPoint.getY());
                    final Point2D.Double clickedPoint = new Point2D.Double(pt.getX(), pt.getY());
                    dist = MathRoutines.distanceToLineSegment(clickedPoint, vaPointDouble, vbPointDouble);
                    dist += Bridge.getContainerStyle(this.container.index()).cellRadiusPixels() / 4;
                }
            }
            else {
                final Point sitePosn = containerStyle.screenPosn(validLocations.get(i).position());
                final int dx = pt.x - sitePosn.x;
                final int dy = pt.y - sitePosn.y;
                dist = Math.sqrt(dx * dx + dy * dy);
            }
            if (dist < minDist && dist < furthestPossibleDistance) {
                location = new FullLocation(site, validLocations.get(i).location().level(), validLocations.get(i).location().siteType());
                minDist = dist;
            }
        }
        return location;
    }
    
    private Point2D calculateMovePosition(final Context context, final Move m, final boolean moveFrom) {
        if (m.from() == -1 || m.to() == -1) {
            return null;
        }
        final ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
        if (moveFrom) {
            if (m.fromType() == SiteType.Cell) {
                final int containerId = ContainerUtil.getContainerId(context, m.from(), m.fromType());
                final int containerStartIndex = context.equipment().sitesFrom()[containerId];
                return containerStyle.drawnCells().get(m.from() - containerStartIndex).centroid();
            }
            if (m.fromType() == SiteType.Edge) {
                return containerStyle.drawnEdges().get(m.from()).centroid();
            }
            if (m.fromType() == SiteType.Vertex) {
                return containerStyle.drawnVertices().get(m.from()).centroid();
            }
        }
        else {
            if (m.toType() == SiteType.Cell) {
                final int containerId = ContainerUtil.getContainerId(context, m.to(), m.toType());
                final int containerStartIndex = context.equipment().sitesFrom()[containerId];
                return containerStyle.drawnCells().get(m.to() - containerStartIndex).centroid();
            }
            if (m.toType() == SiteType.Edge) {
                return containerStyle.drawnEdges().get(m.to()).centroid();
            }
            if (m.toType() == SiteType.Vertex) {
                return containerStyle.drawnVertices().get(m.to()).centroid();
            }
        }
        return null;
    }
    
    private Point2D calculateMovePosition(final Context context, final Location location) {
        final ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
        if (location.siteType() == SiteType.Cell) {
            final int containerId = ContainerUtil.getContainerId(context, location.site(), location.siteType());
            final int containerStartIndex = context.equipment().sitesFrom()[containerId];
            return containerStyle.drawnCells().get(location.site() - containerStartIndex).centroid();
        }
        if (location.siteType() == SiteType.Edge) {
            return containerStyle.drawnEdges().get(location.site()).centroid();
        }
        if (location.siteType() == SiteType.Vertex) {
            return containerStyle.drawnVertices().get(location.site()).centroid();
        }
        return null;
    }
    
    public static boolean isEdgeMove(final Move m) {
        return m.fromType() == SiteType.Edge && m.toType() == SiteType.Edge && m.from() == m.to();
    }
}
