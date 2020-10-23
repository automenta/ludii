// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.components.board;

import game.types.board.SiteType;
import topology.Cell;
import topology.Edge;
import topology.Vertex;
import util.Context;
import util.SettingsColour;
import util.state.containerState.ContainerState;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.designs.board.puzzle.PuzzleDesign;
import view.container.styles.board.graph.PenAndPaperStyle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class PenAndPaperComponents extends PuzzleComponents
{
    final PenAndPaperStyle graphStyle;
    final BoardDesign boardDesign;
    
    public PenAndPaperComponents(final PenAndPaperStyle containerStyle, final PuzzleDesign boardDesign) {
        super(containerStyle, boardDesign);
        this.graphStyle = containerStyle;
        this.boardDesign = boardDesign;
    }
    
    @Override
    public void drawComponents(final Graphics2D g2d, final Context context) {
        final List<Vertex> vertices = this.graphStyle.topology().vertices();
        final BasicStroke strokeThick = this.boardDesign.strokeThick();
        final ContainerState cs = context.state().containerStates()[0];
        if (context.metadata().graphics().onlyEdges()) {
            super.drawComponents(g2d, context, (ArrayList)this.graphStyle.topology().cells());
            super.drawComponents(g2d, context, (ArrayList)this.graphStyle.topology().vertices());
        }
        else {
            this.fillCells(g2d, context);
        }
        g2d.setColor(Color.BLACK);
        final BasicStroke slightlyThickerStroke = new BasicStroke(strokeThick.getLineWidth() * 1.0f + 4.0f, 1, 0);
        g2d.setStroke(slightlyThickerStroke);
        for (final Vertex va : vertices) {
            final Point vaPosn = this.graphStyle.screenPosn(va.centroid());
            for (final Vertex vb : va.orthogonal()) {
                for (int e = 0; e < context.topology().edges().size(); ++e) {
                    final Edge edge = context.topology().edges().get(e);
                    if (((edge.vA() == va && edge.vB() == vb) || (edge.vA() == vb && edge.vB() == va)) && cs.whatEdge(e) != 0) {
                        final Point vbPosn = this.graphStyle.screenPosn(vb.centroid());
                        final Shape line = new Line2D.Double(vaPosn.x, vaPosn.y, vbPosn.x, vbPosn.y);
                        g2d.draw(line);
                    }
                }
            }
        }
        final BasicStroke roundedThinStroke = new BasicStroke(strokeThick.getLineWidth() * 2.0f, 1, 0);
        g2d.setStroke(roundedThinStroke);
        for (final Vertex va2 : vertices) {
            final Point vaPosn2 = this.graphStyle.screenPosn(va2.centroid());
            for (final Vertex vb2 : va2.orthogonal()) {
                for (int e2 = 0; e2 < context.topology().edges().size(); ++e2) {
                    final Edge edge2 = context.topology().edges().get(e2);
                    if (((edge2.vA() == va2 && edge2.vB() == vb2) || (edge2.vA() == vb2 && edge2.vB() == va2)) && cs.whatEdge(e2) != 0) {
                        final Point vbPosn2 = this.graphStyle.screenPosn(vb2.centroid());
                        final Shape line2 = new Line2D.Double(vaPosn2.x, vaPosn2.y, vbPosn2.x, vbPosn2.y);
                        g2d.setColor(SettingsColour.playerColour(cs.whoEdge(e2), context));
                        g2d.draw(line2);
                    }
                }
            }
        }
        final int dim = this.puzzleStyle.topology().rows(context.board().defaultSite()).size();
        final int bigFontSize = (int)(0.75 * this.puzzleStyle.placement().getHeight() / dim + 0.5);
        final Font bigFont = new Font("Arial", 1, bigFontSize);
        g2d.setFont(bigFont);
        for (final Edge e3 : this.graphStyle.topology().edges()) {
            if (cs.isResolved(e3.index(), SiteType.Edge) && cs.what(e3.index(), SiteType.Edge) == 0) {
                final Point drawPosn = this.graphStyle.screenPosn(e3.centroid());
                final Rectangle bounds = g2d.getFontMetrics().getStringBounds("X", g2d).getBounds();
                g2d.drawString("X", drawPosn.x - bounds.width / 2, drawPosn.y + bounds.height / 3);
            }
        }
        final double rO = this.graphStyle.baseVertexRadius();
        for (final Vertex vertex : vertices) {
            if (cs.what(vertex.index(), SiteType.Vertex) != 0) {
                g2d.setColor(SettingsColour.playerColour(cs.who(vertex.index(), SiteType.Vertex), context));
            }
            else {
                g2d.setColor(this.graphStyle.baseGraphColour());
            }
            final Point circlePosn = this.graphStyle.screenPosn(vertex.centroid());
            final Shape ellipseO = new Ellipse2D.Double(circlePosn.x - rO, circlePosn.y - rO, 2.0 * rO, 2.0 * rO);
            g2d.fill(ellipseO);
        }
    }
    
    public void fillCells(final Graphics2D g2d, final Context context) {
        final ContainerState cs = context.state().containerStates()[0];
        for (int f = 0; f < context.topology().cells().size(); ++f) {
            if (cs.whoCell(f) != 0) {
                final Cell face = context.topology().cells().get(f);
                final GeneralPath path = new GeneralPath();
                for (int v = 0; v < face.vertices().size(); ++v) {
                    if (path.getCurrentPoint() == null) {
                        final Vertex prev = face.vertices().get(face.vertices().size() - 1);
                        final Point drawPrev = this.graphStyle.screenPosn(prev.centroid());
                        path.moveTo(drawPrev.x, drawPrev.y);
                    }
                    final Vertex corner = face.vertices().get(v);
                    final Point drawCorner = this.graphStyle.screenPosn(corner.centroid());
                    path.lineTo(drawCorner.x, drawCorner.y);
                }
                g2d.setColor(SettingsColour.playerColour(cs.whoCell(f), context));
                g2d.fill(path);
            }
        }
    }
}
