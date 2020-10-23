// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Node;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeImageElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feImage";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final String uriStr = XLinkSupport.getXLinkHref(filterElement);
        if (uriStr.length() == 0) {
            throw new BridgeException(ctx, filterElement, "attribute.missing", new Object[] { "xlink:href" });
        }
        final Document document = filterElement.getOwnerDocument();
        final boolean isUse = uriStr.indexOf(35) != -1;
        Element contentElement = null;
        if (isUse) {
            contentElement = document.createElementNS("http://www.w3.org/2000/svg", "use");
        }
        else {
            contentElement = document.createElementNS("http://www.w3.org/2000/svg", "image");
        }
        contentElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", uriStr);
        final Element proxyElement = document.createElementNS("http://www.w3.org/2000/svg", "g");
        proxyElement.appendChild(contentElement);
        final Rectangle2D defaultRegion = filterRegion;
        final Element filterDefElement = (Element)filterElement.getParentNode();
        final Rectangle2D primitiveRegion = SVGUtilities.getBaseFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, ctx);
        contentElement.setAttributeNS(null, "x", String.valueOf(primitiveRegion.getX()));
        contentElement.setAttributeNS(null, "y", String.valueOf(primitiveRegion.getY()));
        contentElement.setAttributeNS(null, "width", String.valueOf(primitiveRegion.getWidth()));
        contentElement.setAttributeNS(null, "height", String.valueOf(primitiveRegion.getHeight()));
        final GraphicsNode node = ctx.getGVTBuilder().build(ctx, proxyElement);
        Filter filter = node.getGraphicsNodeRable(true);
        final String s = SVGUtilities.getChainableAttributeNS(filterDefElement, null, "primitiveUnits", ctx);
        short coordSystemType;
        if (s.length() == 0) {
            coordSystemType = 1;
        }
        else {
            coordSystemType = SVGUtilities.parseCoordinateSystem(filterDefElement, "primitiveUnits", s, ctx);
        }
        AffineTransform at = new AffineTransform();
        if (coordSystemType == 2) {
            at = SVGUtilities.toObjectBBox(at, filteredNode);
        }
        filter = new AffineRable8Bit(filter, at);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(filter, filterElement);
        final Rectangle2D primitiveRegionUserSpace = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        filter = new PadRable8Bit(filter, primitiveRegionUserSpace, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static Filter createSVGFeImage(final BridgeContext ctx, final Rectangle2D primitiveRegion, final Element refElement, final boolean toBBoxNeeded, final Element filterElement, final GraphicsNode filteredNode) {
        final GraphicsNode node = ctx.getGVTBuilder().build(ctx, refElement);
        final Filter filter = node.getGraphicsNodeRable(true);
        AffineTransform at = new AffineTransform();
        if (toBBoxNeeded) {
            final Element filterDefElement = (Element)filterElement.getParentNode();
            final String s = SVGUtilities.getChainableAttributeNS(filterDefElement, null, "primitiveUnits", ctx);
            short coordSystemType;
            if (s.length() == 0) {
                coordSystemType = 1;
            }
            else {
                coordSystemType = SVGUtilities.parseCoordinateSystem(filterDefElement, "primitiveUnits", s, ctx);
            }
            if (coordSystemType == 2) {
                at = SVGUtilities.toObjectBBox(at, filteredNode);
            }
            final Rectangle2D bounds = filteredNode.getGeometryBounds();
            at.preConcatenate(AffineTransform.getTranslateInstance(primitiveRegion.getX() - bounds.getX(), primitiveRegion.getY() - bounds.getY()));
        }
        else {
            at.translate(primitiveRegion.getX(), primitiveRegion.getY());
        }
        return new AffineRable8Bit(filter, at);
    }
    
    protected static Filter createRasterFeImage(final BridgeContext ctx, final Rectangle2D primitiveRegion, final ParsedURL purl) {
        final Filter filter = ImageTagRegistry.getRegistry().readURL(purl);
        final Rectangle2D bounds = filter.getBounds2D();
        final AffineTransform scale = new AffineTransform();
        scale.translate(primitiveRegion.getX(), primitiveRegion.getY());
        scale.scale(primitiveRegion.getWidth() / (bounds.getWidth() - 1.0), primitiveRegion.getHeight() / (bounds.getHeight() - 1.0));
        scale.translate(-bounds.getX(), -bounds.getY());
        return new AffineRable8Bit(filter, scale);
    }
}
