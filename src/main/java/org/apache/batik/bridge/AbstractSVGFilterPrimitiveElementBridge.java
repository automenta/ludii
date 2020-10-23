// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Paint;
import java.awt.Color;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.gvt.filter.BackgroundRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FloodRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FilterAlphaRable;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;
import java.util.Map;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import java.awt.geom.Rectangle2D;

public abstract class AbstractSVGFilterPrimitiveElementBridge extends AnimatableGenericSVGBridge implements FilterPrimitiveBridge, ErrorConstants
{
    static final Rectangle2D INFINITE_FILTER_REGION;
    
    protected AbstractSVGFilterPrimitiveElementBridge() {
    }
    
    protected static Filter getIn(final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Map filterMap, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "in");
        if (s.length() == 0) {
            return inputFilter;
        }
        return getFilterSource(filterElement, s, filteredElement, filteredNode, filterMap, ctx);
    }
    
    protected static Filter getIn2(final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Map filterMap, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "in2");
        if (s.length() == 0) {
            throw new BridgeException(ctx, filterElement, "attribute.missing", new Object[] { "in2" });
        }
        return getFilterSource(filterElement, s, filteredElement, filteredNode, filterMap, ctx);
    }
    
    protected static void updateFilterMap(final Element filterElement, final Filter filter, final Map filterMap) {
        final String s = filterElement.getAttributeNS(null, "result");
        if (s.length() != 0 && s.trim().length() != 0) {
            filterMap.put(s, filter);
        }
    }
    
    protected static void handleColorInterpolationFilters(final Filter filter, final Element filterElement) {
        if (filter instanceof FilterColorInterpolation) {
            final boolean isLinear = CSSUtilities.convertColorInterpolationFilters(filterElement);
            ((FilterColorInterpolation)filter).setColorSpaceLinear(isLinear);
        }
    }
    
    static Filter getFilterSource(final Element filterElement, final String s, final Element filteredElement, final GraphicsNode filteredNode, final Map filterMap, final BridgeContext ctx) {
        final Filter srcG = filterMap.get("SourceGraphic");
        final Rectangle2D filterRegion = srcG.getBounds2D();
        final int length = s.length();
        Filter source = null;
        switch (length) {
            case 13: {
                if ("SourceGraphic".equals(s)) {
                    source = srcG;
                    break;
                }
                break;
            }
            case 11: {
                if (s.charAt(1) == "SourceAlpha".charAt(1)) {
                    if ("SourceAlpha".equals(s)) {
                        source = srcG;
                        source = new FilterAlphaRable(source);
                        break;
                    }
                    break;
                }
                else {
                    if ("StrokePaint".equals(s)) {
                        final Paint paint = PaintServer.convertStrokePaint(filteredElement, filteredNode, ctx);
                        source = new FloodRable8Bit(filterRegion, paint);
                        break;
                    }
                    break;
                }
                break;
            }
            case 15: {
                if (s.charAt(10) == "BackgroundImage".charAt(10)) {
                    if ("BackgroundImage".equals(s)) {
                        source = new BackgroundRable8Bit(filteredNode);
                        source = new PadRable8Bit(source, filterRegion, PadMode.ZERO_PAD);
                        break;
                    }
                    break;
                }
                else {
                    if ("BackgroundAlpha".equals(s)) {
                        source = new BackgroundRable8Bit(filteredNode);
                        source = new FilterAlphaRable(source);
                        source = new PadRable8Bit(source, filterRegion, PadMode.ZERO_PAD);
                        break;
                    }
                    break;
                }
                break;
            }
            case 9: {
                if ("FillPaint".equals(s)) {
                    Paint paint = PaintServer.convertFillPaint(filteredElement, filteredNode, ctx);
                    if (paint == null) {
                        paint = new Color(0, 0, 0, 0);
                    }
                    source = new FloodRable8Bit(filterRegion, paint);
                    break;
                }
                break;
            }
        }
        if (source == null) {
            source = filterMap.get(s);
        }
        return source;
    }
    
    protected static int convertInteger(final Element filterElement, final String attrName, final int defaultValue, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultValue;
        }
        try {
            return SVGUtilities.convertSVGInteger(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { attrName, s });
        }
    }
    
    protected static float convertNumber(final Element filterElement, final String attrName, final float defaultValue, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultValue;
        }
        try {
            return SVGUtilities.convertSVGNumber(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { attrName, s, nfEx });
        }
    }
    
    static {
        INFINITE_FILTER_REGION = new Rectangle2D.Float(-1.7014117E38f, -1.7014117E38f, Float.MAX_VALUE, Float.MAX_VALUE);
    }
}
