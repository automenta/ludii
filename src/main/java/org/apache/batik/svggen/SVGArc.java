// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import org.w3c.dom.Element;
import java.awt.geom.Arc2D;

public class SVGArc extends SVGGraphicObjectConverter
{
    private SVGLine svgLine;
    private SVGEllipse svgEllipse;
    
    public SVGArc(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    public Element toSVG(final Arc2D arc) {
        final double ext = arc.getAngleExtent();
        final double width = arc.getWidth();
        final double height = arc.getHeight();
        if (width == 0.0 || height == 0.0) {
            final Line2D line = new Line2D.Double(arc.getX(), arc.getY(), arc.getX() + width, arc.getY() + height);
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        if (ext >= 360.0 || ext <= -360.0) {
            final Ellipse2D ellipse = new Ellipse2D.Double(arc.getX(), arc.getY(), width, height);
            if (this.svgEllipse == null) {
                this.svgEllipse = new SVGEllipse(this.generatorContext);
            }
            return this.svgEllipse.toSVG(ellipse);
        }
        final Element svgPath = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "path");
        final StringBuffer d = new StringBuffer(64);
        final Point2D startPt = arc.getStartPoint();
        final Point2D endPt = arc.getEndPoint();
        final int type = arc.getArcType();
        d.append("M");
        d.append(this.doubleString(startPt.getX()));
        d.append(" ");
        d.append(this.doubleString(startPt.getY()));
        d.append(" ");
        d.append("A");
        d.append(this.doubleString(width / 2.0));
        d.append(" ");
        d.append(this.doubleString(height / 2.0));
        d.append(" ");
        d.append('0');
        d.append(" ");
        if (ext > 0.0) {
            if (ext > 180.0) {
                d.append('1');
            }
            else {
                d.append('0');
            }
            d.append(" ");
            d.append('0');
        }
        else {
            if (ext < -180.0) {
                d.append('1');
            }
            else {
                d.append('0');
            }
            d.append(" ");
            d.append('1');
        }
        d.append(" ");
        d.append(this.doubleString(endPt.getX()));
        d.append(" ");
        d.append(this.doubleString(endPt.getY()));
        if (type == 1) {
            d.append("Z");
        }
        else if (type == 2) {
            final double cx = arc.getX() + width / 2.0;
            final double cy = arc.getY() + height / 2.0;
            d.append("L");
            d.append(" ");
            d.append(this.doubleString(cx));
            d.append(" ");
            d.append(this.doubleString(cy));
            d.append(" ");
            d.append("Z");
        }
        svgPath.setAttributeNS(null, "d", d.toString());
        return svgPath;
    }
}
