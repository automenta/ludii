// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.w3c.dom.Node;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGRadialGradientElementBridge extends AbstractSVGGradientElementBridge
{
    @Override
    public String getLocalName() {
        return "radialGradient";
    }
    
    @Override
    protected Paint buildGradient(final Element paintElement, final Element paintedElement, final GraphicsNode paintedNode, final MultipleGradientPaint.CycleMethodEnum spreadMethod, final MultipleGradientPaint.ColorSpaceEnum colorSpace, AffineTransform transform, final Color[] colors, final float[] offsets, final BridgeContext ctx) {
        String cxStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "cx", ctx);
        if (cxStr.length() == 0) {
            cxStr = "50%";
        }
        String cyStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "cy", ctx);
        if (cyStr.length() == 0) {
            cyStr = "50%";
        }
        String rStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "r", ctx);
        if (rStr.length() == 0) {
            rStr = "50%";
        }
        String fxStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "fx", ctx);
        if (fxStr.length() == 0) {
            fxStr = cxStr;
        }
        String fyStr = SVGUtilities.getChainableAttributeNS(paintElement, null, "fy", ctx);
        if (fyStr.length() == 0) {
            fyStr = cyStr;
        }
        final String s = SVGUtilities.getChainableAttributeNS(paintElement, null, "gradientUnits", ctx);
        short coordSystemType;
        if (s.length() == 0) {
            coordSystemType = 2;
        }
        else {
            coordSystemType = SVGUtilities.parseCoordinateSystem(paintElement, "gradientUnits", s, ctx);
        }
        final SVGContext bridge = BridgeContext.getSVGContext(paintedElement);
        if (coordSystemType == 2 && bridge instanceof AbstractGraphicsNodeBridge) {
            final Rectangle2D bbox = bridge.getBBox();
            if (bbox != null && (bbox.getWidth() == 0.0 || bbox.getHeight() == 0.0)) {
                return null;
            }
        }
        if (coordSystemType == 2) {
            transform = SVGUtilities.toObjectBBox(transform, paintedNode);
        }
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintElement);
        final float r = SVGUtilities.convertLength(rStr, "r", coordSystemType, uctx);
        if (r == 0.0f) {
            return colors[colors.length - 1];
        }
        final Point2D c = SVGUtilities.convertPoint(cxStr, "cx", cyStr, "cy", coordSystemType, uctx);
        final Point2D f = SVGUtilities.convertPoint(fxStr, "fx", fyStr, "fy", coordSystemType, uctx);
        return new RadialGradientPaint(c, r, f, offsets, colors, spreadMethod, RadialGradientPaint.SRGB, transform);
    }
}
