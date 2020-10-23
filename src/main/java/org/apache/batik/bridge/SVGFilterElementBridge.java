// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Node;
import java.util.List;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.LinkedList;
import java.awt.Paint;
import org.apache.batik.ext.awt.image.renderable.FloodRable8Bit;
import java.util.Map;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import java.awt.Color;

public class SVGFilterElementBridge extends AnimatableGenericSVGBridge implements FilterBridge, ErrorConstants
{
    protected static final Color TRANSPARENT_BLACK;
    
    @Override
    public String getLocalName() {
        return "filter";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode) {
        final Rectangle2D filterRegion = SVGUtilities.convertFilterChainRegion(filterElement, filteredElement, filteredNode, ctx);
        if (filterRegion == null) {
            return null;
        }
        Filter sourceGraphic = filteredNode.getGraphicsNodeRable(true);
        sourceGraphic = new PadRable8Bit(sourceGraphic, filterRegion, PadMode.ZERO_PAD);
        final FilterChainRable filterChain = new FilterChainRable8Bit(sourceGraphic, filterRegion);
        final float[] filterRes = SVGUtilities.convertFilterRes(filterElement, ctx);
        filterChain.setFilterResolutionX((int)filterRes[0]);
        filterChain.setFilterResolutionY((int)filterRes[1]);
        final Map filterNodeMap = new HashMap(11);
        filterNodeMap.put("SourceGraphic", sourceGraphic);
        Filter in = buildFilterPrimitives(filterElement, filterRegion, filteredElement, filteredNode, sourceGraphic, filterNodeMap, ctx);
        if (in == null) {
            return null;
        }
        if (in == sourceGraphic) {
            in = createEmptyFilter(filterElement, filterRegion, filteredElement, filteredNode, ctx);
        }
        filterChain.setSource(in);
        return filterChain;
    }
    
    protected static Filter createEmptyFilter(final Element filterElement, final Rectangle2D filterRegion, final Element filteredElement, final GraphicsNode filteredNode, final BridgeContext ctx) {
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(null, filterElement, filteredElement, filteredNode, filterRegion, filterRegion, ctx);
        return new FloodRable8Bit(primitiveRegion, SVGFilterElementBridge.TRANSPARENT_BLACK);
    }
    
    protected static Filter buildFilterPrimitives(Element filterElement, final Rectangle2D filterRegion, final Element filteredElement, final GraphicsNode filteredNode, final Filter in, final Map filterNodeMap, final BridgeContext ctx) {
        final List refs = new LinkedList();
        while (true) {
            final Filter newIn = buildLocalFilterPrimitives(filterElement, filterRegion, filteredElement, filteredNode, in, filterNodeMap, ctx);
            if (newIn != in) {
                return newIn;
            }
            final String uri = XLinkSupport.getXLinkHref(filterElement);
            if (uri.length() == 0) {
                return in;
            }
            final SVGOMDocument doc = (SVGOMDocument)filterElement.getOwnerDocument();
            final ParsedURL url = new ParsedURL(doc.getURLObject(), uri);
            if (refs.contains(url)) {
                throw new BridgeException(ctx, filterElement, "xlink.href.circularDependencies", new Object[] { uri });
            }
            refs.add(url);
            filterElement = ctx.getReferencedElement(filterElement, uri);
        }
    }
    
    protected static Filter buildLocalFilterPrimitives(final Element filterElement, final Rectangle2D filterRegion, final Element filteredElement, final GraphicsNode filteredNode, Filter in, final Map filterNodeMap, final BridgeContext ctx) {
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element e = (Element)n;
                final Bridge bridge = ctx.getBridge(e);
                if (bridge != null) {
                    if (bridge instanceof FilterPrimitiveBridge) {
                        final FilterPrimitiveBridge filterBridge = (FilterPrimitiveBridge)bridge;
                        final Filter filterNode = filterBridge.createFilter(ctx, e, filteredElement, filteredNode, in, filterRegion, filterNodeMap);
                        if (filterNode == null) {
                            return null;
                        }
                        in = filterNode;
                    }
                }
            }
        }
        return in;
    }
    
    static {
        TRANSPARENT_BLACK = new Color(0, true);
    }
}
