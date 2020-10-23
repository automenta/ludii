// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import main.math.MathRoutines;
import main.math.Vector;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SpiralDesign extends BoardDesign
{
    private int numSites;
    private int numTurns;
    private double[] thetas;
    
    public SpiralDesign(final BoardStyle boardStyle) {
        super(boardStyle, null);
        this.numSites = 1;
        this.numTurns = 1;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 1.0f * swThin;
        this.setStrokesAndColours(context, new Color(0, 0, 0), new Color(150, 75, 0), new Color(200, 150, 75), new Color(250, 221, 144), new Color(223, 178, 110), null, null, swThin, swThick);
        this.numTurns = context.board().graph().dim()[0];
        this.numSites = this.topology().vertices().size();
        this.setThetas();
        this.drawSpiralBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    void setThetas() {
        final int base = this.baseNumber();
        this.thetas = new double[2 * this.numSites];
        int index = 1;
        int steps = base;
        for (int ring = 1; ring <= this.numTurns + 1; ++ring) {
            final double dTheta = 6.283185307179586 / steps;
            double theta = 6.283185307179586 * ring;
            if (ring <= 2 || ring % 2 == 1) {
                theta -= dTheta / 2.0;
            }
            for (int step = 0; step < steps; ++step) {
                this.thetas[index++] = theta;
                theta += dTheta;
            }
            if (ring <= 2) {
                steps *= 2;
            }
        }
        for (int vid = 2; vid < this.numSites; ++vid) {
            this.thetas[vid] = (this.thetas[vid - 1] + this.thetas[vid + 1]) / 2.0;
        }
        for (int vid = 2; vid < this.numSites; ++vid) {
            this.thetas[vid] = (this.thetas[vid - 1] + this.thetas[vid + 1]) / 2.0;
        }
        final double[] thetas = this.thetas;
        final int n = 1;
        thetas[n] -= 0.5 * (this.thetas[2] - this.thetas[1]);
    }
    
    private int baseNumber() {
        for (int base = 1; base < this.numSites; ++base) {
            int steps = base;
            int total = 1;
            int ring = 1;
            while (ring < this.numTurns) {
                total += steps;
                if (total > this.numSites) {
                    if (ring <= this.numTurns) {
                        return base - 1;
                    }
                    break;
                }
                else {
                    if (ring <= 2) {
                        steps *= 2;
                    }
                    ++ring;
                }
            }
        }
        System.out.println("** Error: Couldn't find base number for spiral.");
        return 0;
    }
    
    void drawSpiralBoard(final Graphics2D g2d) {
        final int rd = 2;
        g2d.setColor(new Color(0, 127, 255));
        for (final Vertex vertex : this.topology().vertices()) {
            final Point pt = this.boardStyle.screenPosn(vertex.centroid());
            g2d.fillOval(pt.x - 2, pt.y - 2, 4, 4);
        }
        final double a = 0.05;
        final double b = 1.0 / (2.0 * this.numTurns * this.numTurns) * 0.8;
        final double end = (this.thetas[this.thetas.length / 2 - 1] + this.thetas[this.thetas.length / 2]) / 2.0;
        final double nudge = 0.005;
        final double x0 = this.topology().vertices().get(0).centroid().getX();
        final double y0 = this.topology().vertices().get(0).centroid().getY();
        final List<Point> pts = new ArrayList<>();
        for (double theta = -0.05; theta < end + 1.0; theta += 0.2) {
            final double clipTheta = ((theta > end) ? end : theta) - 0.005;
            final double r = 0.05 + b * clipTheta;
            final double x2 = x0 - r * Math.cos(clipTheta);
            final double y2 = y0 + r * Math.sin(clipTheta);
            final Point2D.Double xy = new Point2D.Double(x2, y2);
            final Point pt2 = this.boardStyle.screenPosn(xy);
            pts.add(pt2);
            if (theta > end) {
                final double r2 = 0.05 + b * theta;
                final double x3 = x0 - r2 * Math.cos(theta);
                final double y3 = y0 + r2 * Math.sin(theta);
                final Point2D.Double xy2 = new Point2D.Double(x3, y3);
                final Point pt3 = this.boardStyle.screenPosn(xy2);
                pts.add(pt3);
                final double r3 = -0.05 + b * (end + 0.005);
                final double x4 = x0 - r3 * Math.cos(end + 0.005);
                final double y4 = y0 + r3 * Math.sin(end + 0.005);
                final Point2D.Double xy3 = new Point2D.Double(x4, y4);
                final Point pt4 = this.boardStyle.screenPosn(xy3);
                pts.add(pt4);
                break;
            }
        }
        GeneralPath path = new GeneralPath();
        for (int n = 0; n < pts.size() - 3; ++n) {
            final Point pt5 = pts.get(n);
            if (n == 0) {
                path.moveTo((float)pt5.x, (float)pt5.y);
            }
            else {
                final Point ptA = pts.get(n - 1);
                final Point ptB = pts.get(n);
                final Point ptC = pts.get(n + 1);
                final Point ptD = pts.get(n + 2);
                final Vector vecAC = new Vector(ptC.x - ptA.x, ptC.y - ptA.y);
                vecAC.normalise();
                final Vector vecDB = new Vector(ptB.x - ptD.x, ptB.y - ptD.y);
                vecDB.normalise();
                final double distBC = MathRoutines.distance(ptB, ptC);
                final double off = 0.3 * distBC;
                final double bx = ptB.x + vecAC.x() * off;
                final double by = ptB.y + vecAC.y() * off;
                final double cx = ptC.x + vecDB.x() * off;
                final double cy = ptC.y + vecDB.y() * off;
                final double dx = ptC.x;
                final double dy = ptC.y;
                path.curveTo(bx, by, cx, cy, dx, dy);
            }
        }
        final Point ptN = pts.get(pts.size() - 1);
        path.lineTo((float)ptN.x, (float)ptN.y);
        g2d.setColor(new Color(255, 240, 220));
        g2d.fill(path);
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        g2d.setColor(new Color(220, 180, 120));
        g2d.draw(path);
        final Point ptC2 = this.boardStyle.screenPosn(this.topology().vertices().get(1).centroid());
        final Point ptC3 = this.boardStyle.screenPosn(this.topology().vertices().get(2).centroid());
        final double u = MathRoutines.distance(ptC2.x, ptC2.y, ptC3.x, ptC3.y);
        path = new GeneralPath();
        final Point ptA2 = pts.get(0);
        final Point ptB2 = pts.get(22);
        path.moveTo((float)ptA2.x, (float)ptA2.y);
        path.curveTo((float)(ptA2.x + (int)(0.25 * u)), (float)(ptA2.y + (int)(0.5 * u)), (float)(ptB2.x + (int)(0.0 * u)), (float)(ptB2.y - (int)(0.5 * u)), (float)ptB2.x, (float)ptB2.y);
        g2d.draw(path);
        for (int vid = 1; vid < this.thetas.length / 2; ++vid) {
            final double theta2 = (this.thetas[vid] + this.thetas[vid + 1]) / 2.0;
            final double r4 = -0.05 + b * (theta2 + 0.005);
            final double x5 = x0 - r4 * Math.cos(theta2 + 0.005);
            final double y5 = y0 + r4 * Math.sin(theta2 + 0.005);
            final Point2D.Double xy4 = new Point2D.Double(x5, y5);
            final Point pt6 = this.boardStyle.screenPosn(xy4);
            final double r5 = 0.05 + b * (theta2 - 0.005);
            final double x6 = x0 - r5 * Math.cos(theta2 - 0.005);
            final double y6 = y0 + r5 * Math.sin(theta2 - 0.005);
            final Point2D.Double xy5 = new Point2D.Double(x6, y6);
            final Point pt7 = this.boardStyle.screenPosn(xy5);
            g2d.drawLine(pt6.x, pt6.y, pt7.x, pt7.y);
        }
    }
    
    Point2D.Double ptOnRing(final double x0, final double y0, final double a, final double b, final double theta, final double scale) {
        final double thetaPrev = theta - 6.283185307179586;
        final double r = a + b * theta;
        final double rPrev = a + b * thetaPrev;
        final Point2D.Double pt = new Point2D.Double(x0 + r * Math.cos(theta), y0 - r * Math.sin(theta));
        final Point2D.Double ptP = new Point2D.Double(x0 + rPrev * Math.cos(thetaPrev), y0 - rPrev * Math.sin(thetaPrev));
        return new Point2D.Double((pt.x + ptP.x) / 2.0 * scale, (pt.y + ptP.y) / 2.0 * scale);
    }
}
