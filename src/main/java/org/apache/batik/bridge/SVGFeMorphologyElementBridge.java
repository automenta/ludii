// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.MorphologyRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeMorphologyElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feMorphology";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final float[] radii = convertRadius(filterElement, ctx);
        if (radii[0] == 0.0f || radii[1] == 0.0f) {
            return null;
        }
        final boolean isDilate = convertOperator(filterElement, ctx);
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        final Filter morphology = new MorphologyRable8Bit(pad, radii[0], radii[1], isDilate);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(morphology, filterElement);
        final PadRable filter = new PadRable8Bit(morphology, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static float[] convertRadius(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "radius");
        if (s.length() == 0) {
            return new float[] { 0.0f, 0.0f };
        }
        final float[] radii = new float[2];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            radii[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                radii[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
            else {
                radii[1] = radii[0];
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "radius", s, nfEx });
        }
        if (tokens.hasMoreTokens() || radii[0] < 0.0f || radii[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "radius", s });
        }
        return radii;
    }
    
    protected static boolean convertOperator(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "operator");
        if (s.length() == 0) {
            return false;
        }
        if ("erode".equals(s)) {
            return false;
        }
        if ("dilate".equals(s)) {
            return true;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "operator", s });
    }
}
