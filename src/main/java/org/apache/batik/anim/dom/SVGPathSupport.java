// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.DOMException;
import java.awt.geom.Point2D;
import org.w3c.dom.svg.SVGPoint;
import org.apache.batik.dom.svg.SVGPathContext;

public class SVGPathSupport
{
    public static float getTotalLength(final SVGOMPathElement path) {
        final SVGPathContext pathCtx = (SVGPathContext)path.getSVGContext();
        return pathCtx.getTotalLength();
    }
    
    public static int getPathSegAtLength(final SVGOMPathElement path, final float x) {
        final SVGPathContext pathCtx = (SVGPathContext)path.getSVGContext();
        return pathCtx.getPathSegAtLength(x);
    }
    
    public static SVGPoint getPointAtLength(final SVGOMPathElement path, final float distance) {
        final SVGPathContext pathCtx = (SVGPathContext)path.getSVGContext();
        if (pathCtx == null) {
            return null;
        }
        return new SVGPoint() {
            @Override
            public float getX() {
                final Point2D pt = pathCtx.getPointAtLength(distance);
                return (float)pt.getX();
            }
            
            @Override
            public float getY() {
                final Point2D pt = pathCtx.getPointAtLength(distance);
                return (float)pt.getY();
            }
            
            @Override
            public void setX(final float x) throws DOMException {
                throw path.createDOMException((short)7, "readonly.point", null);
            }
            
            @Override
            public void setY(final float y) throws DOMException {
                throw path.createDOMException((short)7, "readonly.point", null);
            }
            
            @Override
            public SVGPoint matrixTransform(final SVGMatrix matrix) {
                throw path.createDOMException((short)7, "readonly.point", null);
            }
        };
    }
}
