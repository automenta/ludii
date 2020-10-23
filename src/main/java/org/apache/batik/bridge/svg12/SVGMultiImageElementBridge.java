// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.UnitProcessor;
import java.util.Iterator;
import org.w3c.dom.Node;
import java.util.List;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.bridge.MultiResGraphicsNode;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.CSSUtilities;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGImageElementBridge;

public class SVGMultiImageElementBridge extends SVGImageElementBridge
{
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }
    
    @Override
    public String getLocalName() {
        return "multiImage";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGMultiImageElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final ImageNode imgNode = (ImageNode)this.instantiateGraphicsNode();
        if (imgNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, imgNode);
        final Rectangle2D b = getImageBounds(ctx, e);
        AffineTransform at = null;
        final String s = e.getAttribute("transform");
        if (s.length() != 0) {
            at = SVGUtilities.convertTransform(e, "transform", s, ctx);
        }
        else {
            at = new AffineTransform();
        }
        at.translate(b.getX(), b.getY());
        imgNode.setTransform(at);
        imgNode.setVisible(CSSUtilities.convertVisibility(e));
        final Rectangle2D clip = new Rectangle2D.Double(0.0, 0.0, b.getWidth(), b.getHeight());
        final Filter filter = imgNode.getGraphicsNodeRable(true);
        imgNode.setClip(new ClipRable8Bit(filter, clip));
        final Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            imgNode.setBackgroundEnable(r);
        }
        ctx.openViewport(e, new MultiImageElementViewport((float)b.getWidth(), (float)b.getHeight()));
        final List elems = new LinkedList();
        final List minDim = new LinkedList();
        final List maxDim = new LinkedList();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element se = (Element)n;
                if (this.getNamespaceURI().equals(se.getNamespaceURI())) {
                    if (se.getLocalName().equals("subImage")) {
                        this.addInfo(se, elems, minDim, maxDim, b);
                    }
                    if (se.getLocalName().equals("subImageRef")) {
                        this.addRefInfo(se, elems, minDim, maxDim, b);
                    }
                }
            }
        }
        final Dimension[] mindary = new Dimension[elems.size()];
        final Dimension[] maxdary = new Dimension[elems.size()];
        final Element[] elemary = new Element[elems.size()];
        final Iterator mindi = minDim.iterator();
        final Iterator maxdi = maxDim.iterator();
        final Iterator ei = elems.iterator();
        int n2 = 0;
        while (mindi.hasNext()) {
            final Dimension minD = mindi.next();
            final Dimension maxD = maxdi.next();
            int i = 0;
            if (minD != null) {
                while (i < n2) {
                    if (mindary[i] != null && minD.width < mindary[i].width) {
                        break;
                    }
                    ++i;
                }
            }
            for (int j = n2; j > i; --j) {
                elemary[j] = elemary[j - 1];
                mindary[j] = mindary[j - 1];
                maxdary[j] = maxdary[j - 1];
            }
            elemary[i] = ei.next();
            mindary[i] = minD;
            maxdary[i] = maxD;
            ++n2;
        }
        final GraphicsNode node = new MultiResGraphicsNode(e, clip, elemary, mindary, maxdary, ctx);
        imgNode.setImage(node);
        return imgNode;
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
        ctx.closeViewport(e);
    }
    
    @Override
    protected void initializeDynamicSupport(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        if (ctx.isInteractive()) {
            final ImageNode imgNode = (ImageNode)node;
            ctx.bind(e, imgNode.getImage());
        }
    }
    
    @Override
    public void dispose() {
        this.ctx.removeViewport(this.e);
        super.dispose();
    }
    
    protected static Rectangle2D getImageBounds(final BridgeContext ctx, final Element element) {
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        String s = element.getAttributeNS(null, "x");
        float x = 0.0f;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        s = element.getAttributeNS(null, "y");
        float y = 0.0f;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        s = element.getAttributeNS(null, "width");
        if (s.length() == 0) {
            throw new BridgeException(ctx, element, "attribute.missing", new Object[] { "width" });
        }
        final float w = UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
        s = element.getAttributeNS(null, "height");
        if (s.length() == 0) {
            throw new BridgeException(ctx, element, "attribute.missing", new Object[] { "height" });
        }
        final float h = UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
        return new Rectangle2D.Float(x, y, w, h);
    }
    
    protected void addInfo(final Element e, final Collection elems, final Collection minDim, final Collection maxDim, final Rectangle2D bounds) {
        final Document doc = e.getOwnerDocument();
        final Element gElem = doc.createElementNS("http://www.w3.org/2000/svg", "g");
        final NamedNodeMap attrs = e.getAttributes();
        for (int len = attrs.getLength(), i = 0; i < len; ++i) {
            final Attr attr = (Attr)attrs.item(i);
            gElem.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
        }
        for (Node n = e.getFirstChild(); n != null; n = e.getFirstChild()) {
            gElem.appendChild(n);
        }
        e.appendChild(gElem);
        elems.add(gElem);
        minDim.add(this.getElementMinPixel(e, bounds));
        maxDim.add(this.getElementMaxPixel(e, bounds));
    }
    
    protected void addRefInfo(final Element e, final Collection elems, final Collection minDim, final Collection maxDim, final Rectangle2D bounds) {
        final String uriStr = XLinkSupport.getXLinkHref(e);
        if (uriStr.length() == 0) {
            throw new BridgeException(this.ctx, e, "attribute.missing", new Object[] { "xlink:href" });
        }
        final String baseURI = AbstractNode.getBaseURI(e);
        ParsedURL purl;
        if (baseURI == null) {
            purl = new ParsedURL(uriStr);
        }
        else {
            purl = new ParsedURL(baseURI, uriStr);
        }
        final Document doc = e.getOwnerDocument();
        final Element imgElem = doc.createElementNS("http://www.w3.org/2000/svg", "image");
        imgElem.setAttributeNS("http://www.w3.org/1999/xlink", "href", purl.toString());
        final NamedNodeMap attrs = e.getAttributes();
        for (int len = attrs.getLength(), i = 0; i < len; ++i) {
            final Attr attr = (Attr)attrs.item(i);
            imgElem.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
        }
        String s = e.getAttribute("x");
        if (s.length() == 0) {
            imgElem.setAttribute("x", "0");
        }
        s = e.getAttribute("y");
        if (s.length() == 0) {
            imgElem.setAttribute("y", "0");
        }
        s = e.getAttribute("width");
        if (s.length() == 0) {
            imgElem.setAttribute("width", "100%");
        }
        s = e.getAttribute("height");
        if (s.length() == 0) {
            imgElem.setAttribute("height", "100%");
        }
        e.appendChild(imgElem);
        elems.add(imgElem);
        minDim.add(this.getElementMinPixel(e, bounds));
        maxDim.add(this.getElementMaxPixel(e, bounds));
    }
    
    protected Dimension getElementMinPixel(final Element e, final Rectangle2D bounds) {
        return this.getElementPixelSize(e, "max-pixel-size", bounds);
    }
    
    protected Dimension getElementMaxPixel(final Element e, final Rectangle2D bounds) {
        return this.getElementPixelSize(e, "min-pixel-size", bounds);
    }
    
    protected Dimension getElementPixelSize(final Element e, final String attr, final Rectangle2D bounds) {
        final String s = e.getAttribute(attr);
        if (s.length() == 0) {
            return null;
        }
        final Float[] vals = SVGUtilities.convertSVGNumberOptionalNumber(e, attr, s, this.ctx);
        if (vals[0] == null) {
            return null;
        }
        float yPixSz;
        final float xPixSz = yPixSz = vals[0];
        if (vals[1] != null) {
            yPixSz = vals[1];
        }
        return new Dimension((int)(bounds.getWidth() / xPixSz + 0.5), (int)(bounds.getHeight() / yPixSz + 0.5));
    }
    
    public static class MultiImageElementViewport implements Viewport
    {
        private float width;
        private float height;
        
        public MultiImageElementViewport(final float w, final float h) {
            this.width = w;
            this.height = h;
        }
        
        @Override
        public float getWidth() {
            return this.width;
        }
        
        @Override
        public float getHeight() {
            return this.height;
        }
    }
}
