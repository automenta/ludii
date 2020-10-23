// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Shape;
import org.apache.batik.gvt.AbstractGraphicsNode;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.LinkedList;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit;
import org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Node;
import org.apache.batik.gvt.RootGraphicsNode;
import java.awt.Paint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGPatternElementBridge extends AnimatableGenericSVGBridge implements PaintBridge, ErrorConstants
{
    @Override
    public String getLocalName() {
        return "pattern";
    }
    
    @Override
    public Paint createPaint(final BridgeContext ctx, final Element patternElement, final Element paintedElement, final GraphicsNode paintedNode, final float opacity) {
        RootGraphicsNode patternContentNode = (RootGraphicsNode)ctx.getElementData(patternElement);
        if (patternContentNode == null) {
            patternContentNode = extractPatternContent(patternElement, ctx);
            ctx.setElementData(patternElement, patternContentNode);
        }
        if (patternContentNode == null) {
            return null;
        }
        final Rectangle2D patternRegion = SVGUtilities.convertPatternRegion(patternElement, paintedElement, paintedNode, ctx);
        String s = SVGUtilities.getChainableAttributeNS(patternElement, null, "patternTransform", ctx);
        AffineTransform patternTransform;
        if (s.length() != 0) {
            patternTransform = SVGUtilities.convertTransform(patternElement, "patternTransform", s, ctx);
        }
        else {
            patternTransform = new AffineTransform();
        }
        final boolean overflowIsHidden = CSSUtilities.convertOverflow(patternElement);
        s = SVGUtilities.getChainableAttributeNS(patternElement, null, "patternContentUnits", ctx);
        short contentCoordSystem;
        if (s.length() == 0) {
            contentCoordSystem = 1;
        }
        else {
            contentCoordSystem = SVGUtilities.parseCoordinateSystem(patternElement, "patternContentUnits", s, ctx);
        }
        final AffineTransform patternContentTransform = new AffineTransform();
        patternContentTransform.translate(patternRegion.getX(), patternRegion.getY());
        final String viewBoxStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "viewBox", ctx);
        if (viewBoxStr.length() > 0) {
            final String aspectRatioStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "preserveAspectRatio", ctx);
            final float w = (float)patternRegion.getWidth();
            final float h = (float)patternRegion.getHeight();
            final AffineTransform preserveAspectRatioTransform = ViewBox.getPreserveAspectRatioTransform(patternElement, viewBoxStr, aspectRatioStr, w, h, ctx);
            patternContentTransform.concatenate(preserveAspectRatioTransform);
        }
        else if (contentCoordSystem == 2) {
            final AffineTransform patternContentUnitsTransform = new AffineTransform();
            final Rectangle2D objectBoundingBox = paintedNode.getGeometryBounds();
            patternContentUnitsTransform.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
            patternContentUnitsTransform.scale(objectBoundingBox.getWidth(), objectBoundingBox.getHeight());
            patternContentTransform.concatenate(patternContentUnitsTransform);
        }
        final GraphicsNode gn = new PatternGraphicsNode(patternContentNode);
        gn.setTransform(patternContentTransform);
        if (opacity != 1.0f) {
            Filter filter = gn.getGraphicsNodeRable(true);
            filter = new ComponentTransferRable8Bit(filter, ConcreteComponentTransferFunction.getLinearTransfer(opacity, 0.0f), ConcreteComponentTransferFunction.getIdentityTransfer(), ConcreteComponentTransferFunction.getIdentityTransfer(), ConcreteComponentTransferFunction.getIdentityTransfer());
            gn.setFilter(filter);
        }
        return new PatternPaint(gn, patternRegion, !overflowIsHidden, patternTransform);
    }
    
    protected static RootGraphicsNode extractPatternContent(Element patternElement, final BridgeContext ctx) {
        final List refs = new LinkedList();
        while (true) {
            final RootGraphicsNode content = extractLocalPatternContent(patternElement, ctx);
            if (content != null) {
                return content;
            }
            final String uri = XLinkSupport.getXLinkHref(patternElement);
            if (uri.length() == 0) {
                return null;
            }
            final SVGOMDocument doc = (SVGOMDocument)patternElement.getOwnerDocument();
            final ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (!purl.complete()) {
                throw new BridgeException(ctx, patternElement, "uri.malformed", new Object[] { uri });
            }
            if (contains(refs, purl)) {
                throw new BridgeException(ctx, patternElement, "xlink.href.circularDependencies", new Object[] { uri });
            }
            refs.add(purl);
            patternElement = ctx.getReferencedElement(patternElement, uri);
        }
    }
    
    protected static RootGraphicsNode extractLocalPatternContent(final Element e, final BridgeContext ctx) {
        final GVTBuilder builder = ctx.getGVTBuilder();
        RootGraphicsNode content = null;
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final GraphicsNode gn = builder.build(ctx, (Element)n);
                if (gn != null) {
                    if (content == null) {
                        content = new RootGraphicsNode();
                    }
                    content.getChildren().add(gn);
                }
            }
        }
        return content;
    }
    
    private static boolean contains(final List urls, final ParsedURL key) {
        for (final Object url : urls) {
            if (key.equals(url)) {
                return true;
            }
        }
        return false;
    }
    
    public static class PatternGraphicsNode extends AbstractGraphicsNode
    {
        GraphicsNode pcn;
        Rectangle2D pBounds;
        Rectangle2D gBounds;
        Rectangle2D sBounds;
        Shape oShape;
        
        public PatternGraphicsNode(final GraphicsNode gn) {
            this.pcn = gn;
        }
        
        @Override
        public void primitivePaint(final Graphics2D g2d) {
            this.pcn.paint(g2d);
        }
        
        @Override
        public Rectangle2D getPrimitiveBounds() {
            if (this.pBounds != null) {
                return this.pBounds;
            }
            return this.pBounds = this.pcn.getTransformedBounds(PatternGraphicsNode.IDENTITY);
        }
        
        @Override
        public Rectangle2D getGeometryBounds() {
            if (this.gBounds != null) {
                return this.gBounds;
            }
            return this.gBounds = this.pcn.getTransformedGeometryBounds(PatternGraphicsNode.IDENTITY);
        }
        
        @Override
        public Rectangle2D getSensitiveBounds() {
            if (this.sBounds != null) {
                return this.sBounds;
            }
            return this.sBounds = this.pcn.getTransformedSensitiveBounds(PatternGraphicsNode.IDENTITY);
        }
        
        @Override
        public Shape getOutline() {
            if (this.oShape != null) {
                return this.oShape;
            }
            this.oShape = this.pcn.getOutline();
            final AffineTransform tr = this.pcn.getTransform();
            if (tr != null) {
                this.oShape = tr.createTransformedShape(this.oShape);
            }
            return this.oShape;
        }
        
        @Override
        protected void invalidateGeometryCache() {
            this.pBounds = null;
            this.gBounds = null;
            this.sBounds = null;
            this.oShape = null;
            super.invalidateGeometryCache();
        }
    }
}
