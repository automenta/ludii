// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import java.util.ArrayList;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeBlendElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feBlend";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final CompositeRule rule = convertMode(filterElement, ctx);
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Filter in2 = AbstractSVGFilterPrimitiveElementBridge.getIn2(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in2 == null) {
            return null;
        }
        final Rectangle2D defaultRegion = (Rectangle2D)in.getBounds2D().clone();
        defaultRegion.add(in2.getBounds2D());
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final List srcs = new ArrayList(2);
        srcs.add(in2);
        srcs.add(in);
        Filter filter = new CompositeRable8Bit(srcs, rule, true);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(filter, filterElement);
        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static CompositeRule convertMode(final Element filterElement, final BridgeContext ctx) {
        final String rule = filterElement.getAttributeNS(null, "mode");
        if (rule.length() == 0) {
            return CompositeRule.OVER;
        }
        if ("normal".equals(rule)) {
            return CompositeRule.OVER;
        }
        if ("multiply".equals(rule)) {
            return CompositeRule.MULTIPLY;
        }
        if ("screen".equals(rule)) {
            return CompositeRule.SCREEN;
        }
        if ("darken".equals(rule)) {
            return CompositeRule.DARKEN;
        }
        if ("lighten".equals(rule)) {
            return CompositeRule.LIGHTEN;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "mode", rule });
    }
}
