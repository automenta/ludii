// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.equipment.other.Regions;
import game.functions.region.RegionFunction;
import math.MathRoutines;
import topology.Cell;
import topology.Edge;
import util.Context;
import util.SettingsColour;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.BitSet;

public class ConnectiveGoalDesign extends BoardDesign
{
    public ConnectiveGoalDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    protected void drawOuterCellEdges(final Graphics2D g2d, final Context context) {
        final Regions[] regionsList = context.game().equipment().regions();
        final int numPlayers = context.game().players().count();
        final Point2D.Double centre = this.topology().centrePoint();
        final Point ptCentre = this.screenPosn(centre);
        final GeneralPath[] paths = new GeneralPath[context.game().players().count() + 1];
        for (int pid = 0; pid < paths.length; ++pid) {
            paths[pid] = new GeneralPath();
        }
        final BitSet[] shared = new BitSet[numPlayers + 2];
        for (int pid2 = 0; pid2 < shared.length; ++pid2) {
            shared[pid2] = new BitSet();
        }
        for (final Regions regionsO : regionsList) {
            final int pid3 = regionsO.owner();
            final int[] eval;
            final int[] sites = eval = regionsO.eval(context);
            for (final int cid : eval) {
                shared[pid3].set(cid, true);
            }
        }
        for (int pidA = 1; pidA < shared.length; ++pidA) {
            for (int pidB = 2; pidB < shared.length; ++pidB) {
                if (pidA != pidB) {
                    final BitSet bs = (BitSet)shared[pidA].clone();
                    bs.and(shared[pidB]);
                    shared[0].or(bs);
                }
            }
        }
        for (int pid2 = 1; pid2 < shared.length; ++pid2) {
            shared[pid2].and(shared[0]);
        }
        for (final Regions regionsO : regionsList) {
            final int pid3 = regionsO.owner();
            final int[] eval2;
            final int[] sites = eval2 = regionsO.eval(context);
            for (final int site : eval2) {
                final Cell cell = this.topology().cells().get(site);
                for (final Edge edge : cell.edges()) {
                    if (edge.properties().get(2L)) {
                        final Point2D va = edge.vA().centroid();
                        final Point2D vb = edge.vB().centroid();
                        final Point ptA = this.screenPosn(va);
                        final Point ptB = this.screenPosn(vb);
                        paths[pid3].moveTo(ptA.x, ptA.y);
                        paths[pid3].lineTo(ptB.x, ptB.y);
                    }
                }
            }
        }
        final int thickness = (int)(4.0f * this.strokeThick().getLineWidth());
        final BasicStroke borderStroke = new BasicStroke((thickness + 2), 1, 0);
        final Stroke playerStroke = new BasicStroke(thickness, 1, 0);
        final Shape oldClip = g2d.getClip();
        for (final Regions regionsO2 : regionsList) {
            if (regionsO2.region() != null) {
                final int pid4 = regionsO2.owner();
                final Color playerColour = SettingsColour.playerColour(pid4, context);
                final Color borderColour = MathRoutines.shade(playerColour, 0.75);
                for (final RegionFunction region : regionsO2.region()) {
                    final int[] sites2 = region.eval(context).sites();
                    final GeneralPath path = new GeneralPath();
                    Point ptSharedA = null;
                    Point ptSharedB = null;
                    for (final int site2 : sites2) {
                        final Cell cell2 = this.topology().cells().get(site2);
                        if (shared[0].get(site2)) {
                            final Point pt = this.screenPosn(cell2.centroid());
                            if (ptSharedA == null) {
                                ptSharedA = pt;
                            }
                            else {
                                ptSharedB = pt;
                            }
                        }
                        for (final Edge edge2 : cell2.edges()) {
                            if (edge2.properties().get(2L)) {
                                final Point2D va2 = edge2.vA().centroid();
                                final Point2D vb2 = edge2.vB().centroid();
                                final Point ptA2 = this.screenPosn(va2);
                                final Point ptB2 = this.screenPosn(vb2);
                                path.moveTo(ptA2.x, ptA2.y);
                                path.lineTo(ptB2.x, ptB2.y);
                            }
                        }
                    }
                    g2d.setClip(oldClip);
                    if (ptSharedA != null && ptSharedB != null) {
                        final int ax = ptCentre.x + 2 * (ptSharedA.x - ptCentre.x);
                        final int ay = ptCentre.y + 2 * (ptSharedA.y - ptCentre.y);
                        final Point ptA3 = new Point(ax, ay);
                        final int bx = ptCentre.x + 2 * (ptSharedB.x - ptCentre.x);
                        final int by = ptCentre.y + 2 * (ptSharedB.y - ptCentre.y);
                        final Point ptB3 = new Point(bx, by);
                        final GeneralPath pathClip = new GeneralPath();
                        pathClip.moveTo(ptCentre.x, ptCentre.y);
                        pathClip.lineTo(ptA3.x, ptA3.y);
                        pathClip.lineTo(ptB3.x, ptB3.y);
                        pathClip.closePath();
                        g2d.setClip(pathClip);
                    }
                    else if (ptSharedA != null) {
                        final int ax = ptCentre.x + 2 * (ptSharedA.x - ptCentre.x);
                        final int ay = ptCentre.y + 2 * (ptSharedA.y - ptCentre.y);
                        final Point ptA3 = new Point(ax, ay);
                        Point ptBestB = null;
                        double maxDist = 0.0;
                        for (final int site3 : sites2) {
                            final Cell cell3 = this.topology().cells().get(site3);
                            final Point pt2 = this.screenPosn(cell3.centroid());
                            final double dist = MathRoutines.distance(pt2, ptA3);
                            if (dist > maxDist) {
                                ptBestB = new Point(pt2.x, pt2.y);
                                maxDist = dist;
                            }
                        }
                        if (ptBestB == null) {
                            System.out.println("** Failed to find furthest point.");
                            return;
                        }
                        final int bestBx = ptA3.x + (int)(1.25 * (ptBestB.x - ptA3.x));
                        final int bestBy = ptA3.y + (int)(1.25 * (ptBestB.y - ptA3.y));
                        ptBestB = new Point(bestBx, bestBy);
                        final int bx2 = ptCentre.x + 2 * (ptBestB.x - ptCentre.x);
                        final int by2 = ptCentre.y + 2 * (ptBestB.y - ptCentre.y);
                        final Point ptB2 = new Point(bx2, by2);
                        final GeneralPath pathClip2 = new GeneralPath();
                        pathClip2.moveTo(ptCentre.x, ptCentre.y);
                        pathClip2.lineTo(ptA3.x, ptA3.y);
                        pathClip2.lineTo(ptB2.x, ptB2.y);
                        pathClip2.closePath();
                        g2d.setClip(pathClip2);
                    }
                    g2d.setColor(borderColour);
                    g2d.setStroke(borderStroke);
                    g2d.draw(path);
                    g2d.setColor(playerColour);
                    g2d.setStroke(playerStroke);
                    g2d.draw(path);
                }
            }
        }
        g2d.setClip(oldClip);
    }
}
