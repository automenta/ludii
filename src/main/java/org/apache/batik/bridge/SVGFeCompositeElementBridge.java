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

public class SVGFeCompositeElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feComposite";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final CompositeRule rule = convertOperator(filterElement, ctx);
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
    
    protected static CompositeRule convertOperator(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "operator");
        if (s.length() == 0) {
            return CompositeRule.OVER;
        }
        if ("atop".equals(s)) {
            return CompositeRule.ATOP;
        }
        if ("in".equals(s)) {
            return CompositeRule.IN;
        }
        if ("over".equals(s)) {
            return CompositeRule.OVER;
        }
        if ("out".equals(s)) {
            return CompositeRule.OUT;
        }
        if ("xor".equals(s)) {
            return CompositeRule.XOR;
        }
        if ("arithmetic".equals(s)) {
            final float k1 = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "k1", 0.0f, ctx);
            final float k2 = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "k2", 0.0f, ctx);
            final float k3 = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "k3", 0.0f, ctx);
            final float k4 = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "k4", 0.0f, ctx);
            return CompositeRule.ARITHMETIC(k1, k2, k3, k4);
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "operator", s });
    }
}
