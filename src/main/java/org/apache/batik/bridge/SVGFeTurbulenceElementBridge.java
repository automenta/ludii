// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable8Bit;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeTurbulenceElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feTurbulence";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = filterRegion;
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final float[] baseFrequency = convertBaseFrenquency(filterElement, ctx);
        final int numOctaves = AbstractSVGFilterPrimitiveElementBridge.convertInteger(filterElement, "numOctaves", 1, ctx);
        final int seed = AbstractSVGFilterPrimitiveElementBridge.convertInteger(filterElement, "seed", 0, ctx);
        final boolean stitchTiles = convertStitchTiles(filterElement, ctx);
        final boolean isFractalNoise = convertType(filterElement, ctx);
        final TurbulenceRable turbulenceRable = new TurbulenceRable8Bit(primitiveRegion);
        turbulenceRable.setBaseFrequencyX(baseFrequency[0]);
        turbulenceRable.setBaseFrequencyY(baseFrequency[1]);
        turbulenceRable.setNumOctaves(numOctaves);
        turbulenceRable.setSeed(seed);
        turbulenceRable.setStitched(stitchTiles);
        turbulenceRable.setFractalNoise(isFractalNoise);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(turbulenceRable, filterElement);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, turbulenceRable, filterMap);
        return turbulenceRable;
    }
    
    protected static float[] convertBaseFrenquency(final Element e, final BridgeContext ctx) {
        final String s = e.getAttributeNS(null, "baseFrequency");
        if (s.length() == 0) {
            return new float[] { 0.001f, 0.001f };
        }
        final float[] v = new float[2];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            v[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                v[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
            else {
                v[1] = v[0];
            }
            if (tokens.hasMoreTokens()) {
                throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "baseFrequency", s });
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[] { "baseFrequency", s });
        }
        if (v[0] < 0.0f || v[1] < 0.0f) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "baseFrequency", s });
        }
        return v;
    }
    
    protected static boolean convertStitchTiles(final Element e, final BridgeContext ctx) {
        final String s = e.getAttributeNS(null, "stitchTiles");
        if (s.length() == 0) {
            return false;
        }
        if ("stitch".equals(s)) {
            return true;
        }
        if ("noStitch".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "stitchTiles", s });
    }
    
    protected static boolean convertType(final Element e, final BridgeContext ctx) {
        final String s = e.getAttributeNS(null, "type");
        if (s.length() == 0) {
            return false;
        }
        if ("fractalNoise".equals(s)) {
            return true;
        }
        if ("turbulence".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "type", s });
    }
}
