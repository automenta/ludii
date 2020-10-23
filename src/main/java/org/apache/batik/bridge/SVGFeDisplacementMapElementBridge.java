// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable8Bit;
import java.util.ArrayList;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.ARGBChannel;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeDisplacementMapElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feDisplacementMap";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final float scale = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "scale", 0.0f, ctx);
        final ARGBChannel xChannelSelector = convertChannelSelector(filterElement, "xChannelSelector", ARGBChannel.A, ctx);
        final ARGBChannel yChannelSelector = convertChannelSelector(filterElement, "yChannelSelector", ARGBChannel.A, ctx);
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
        final PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        final List srcs = new ArrayList(2);
        srcs.add(pad);
        srcs.add(in2);
        final Filter displacementMap = new DisplacementMapRable8Bit(srcs, scale, xChannelSelector, yChannelSelector);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(displacementMap, filterElement);
        final PadRable filter = new PadRable8Bit(displacementMap, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static ARGBChannel convertChannelSelector(final Element filterElement, final String attrName, final ARGBChannel defaultChannel, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultChannel;
        }
        if ("A".equals(s)) {
            return ARGBChannel.A;
        }
        if ("R".equals(s)) {
            return ARGBChannel.R;
        }
        if ("G".equals(s)) {
            return ARGBChannel.G;
        }
        if ("B".equals(s)) {
            return ARGBChannel.B;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { attrName, s });
    }
}
