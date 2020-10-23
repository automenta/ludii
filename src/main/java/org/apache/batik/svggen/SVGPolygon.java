// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Element;
import java.awt.Polygon;

public class SVGPolygon extends SVGGraphicObjectConverter
{
    public SVGPolygon(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    public Element toSVG(final Polygon polygon) {
        final Element svgPolygon = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "polygon");
        final StringBuffer points = new StringBuffer(" ");
        final PathIterator pi = polygon.getPathIterator(null);
        final float[] seg = new float[6];
        while (!pi.isDone()) {
            final int segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    this.appendPoint(points, seg[0], seg[1]);
                    break;
                }
                case 1: {
                    this.appendPoint(points, seg[0], seg[1]);
                    break;
                }
                case 4: {
                    break;
                }
                default: {
                    throw new RuntimeException("invalid segmentType:" + segType);
                }
            }
            pi.next();
        }
        svgPolygon.setAttributeNS(null, "points", points.substring(0, points.length() - 1));
        return svgPolygon;
    }
    
    private void appendPoint(final StringBuffer points, final float x, final float y) {
        points.append(this.doubleString(x));
        points.append(" ");
        points.append(this.doubleString(y));
        points.append(" ");
    }
}
