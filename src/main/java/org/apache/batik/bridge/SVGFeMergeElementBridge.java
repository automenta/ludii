// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Node;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.CompositeRule;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeMergeElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feMerge";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final List srcs = extractFeMergeNode(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (srcs == null) {
            return null;
        }
        if (srcs.size() == 0) {
            return null;
        }
        final Iterator iter = srcs.iterator();
        final Rectangle2D defaultRegion = (Rectangle2D)iter.next().getBounds2D().clone();
        while (iter.hasNext()) {
            defaultRegion.add(iter.next().getBounds2D());
        }
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        Filter filter = new CompositeRable8Bit(srcs, CompositeRule.OVER, true);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(filter, filterElement);
        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static List extractFeMergeNode(final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Map filterMap, final BridgeContext ctx) {
        List srcs = null;
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element e = (Element)n;
                final Bridge bridge = ctx.getBridge(e);
                if (bridge != null) {
                    if (bridge instanceof SVGFeMergeNodeElementBridge) {
                        final Filter filter = ((SVGFeMergeNodeElementBridge)bridge).createFilter(ctx, e, filteredElement, filteredNode, inputFilter, filterMap);
                        if (filter != null) {
                            if (srcs == null) {
                                srcs = new LinkedList();
                            }
                            srcs.add(filter);
                        }
                    }
                }
            }
        }
        return srcs;
    }
    
    public static class SVGFeMergeNodeElementBridge extends AnimatableGenericSVGBridge
    {
        @Override
        public String getLocalName() {
            return "feMergeNode";
        }
        
        public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Map filterMap) {
            return AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        }
    }
}
