// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Element;
import java.awt.Shape;

public class SVGPath extends SVGGraphicObjectConverter
{
    public SVGPath(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    public Element toSVG(final Shape path) {
        final String dAttr = toSVGPathData(path, this.generatorContext);
        if (dAttr == null || dAttr.length() == 0) {
            return null;
        }
        final Element svgPath = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "path");
        svgPath.setAttributeNS(null, "d", dAttr);
        if (path.getPathIterator(null).getWindingRule() == 0) {
            svgPath.setAttributeNS(null, "fill-rule", "evenodd");
        }
        return svgPath;
    }
    
    public static String toSVGPathData(final Shape path, final SVGGeneratorContext gc) {
        final StringBuffer d = new StringBuffer(40);
        final PathIterator pi = path.getPathIterator(null);
        final float[] seg = new float[6];
        int segType = 0;
        while (!pi.isDone()) {
            segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    d.append("M");
                    appendPoint(d, seg[0], seg[1], gc);
                    break;
                }
                case 1: {
                    d.append("L");
                    appendPoint(d, seg[0], seg[1], gc);
                    break;
                }
                case 4: {
                    d.append("Z");
                    break;
                }
                case 2: {
                    d.append("Q");
                    appendPoint(d, seg[0], seg[1], gc);
                    appendPoint(d, seg[2], seg[3], gc);
                    break;
                }
                case 3: {
                    d.append("C");
                    appendPoint(d, seg[0], seg[1], gc);
                    appendPoint(d, seg[2], seg[3], gc);
                    appendPoint(d, seg[4], seg[5], gc);
                    break;
                }
                default: {
                    throw new RuntimeException("invalid segmentType:" + segType);
                }
            }
            pi.next();
        }
        if (d.length() > 0) {
            return d.toString().trim();
        }
        return "";
    }
    
    private static void appendPoint(final StringBuffer d, final float x, final float y, final SVGGeneratorContext gc) {
        d.append(gc.doubleString(x));
        d.append(" ");
        d.append(gc.doubleString(y));
        d.append(" ");
    }
}
