// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.SoftReferenceCache;
import org.apache.batik.util.Platform;
import java.util.HashMap;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import java.awt.image.RenderedImage;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.Element;
import java.awt.Cursor;
import java.util.Map;
import org.apache.batik.util.SVGConstants;

public class CursorManager implements SVGConstants, ErrorConstants
{
    protected static Map cursorMap;
    public static final Cursor DEFAULT_CURSOR;
    public static final Cursor ANCHOR_CURSOR;
    public static final Cursor TEXT_CURSOR;
    public static final int DEFAULT_PREFERRED_WIDTH = 32;
    public static final int DEFAULT_PREFERRED_HEIGHT = 32;
    protected BridgeContext ctx;
    protected CursorCache cursorCache;
    
    public CursorManager(final BridgeContext ctx) {
        this.cursorCache = new CursorCache();
        this.ctx = ctx;
    }
    
    public static Cursor getPredefinedCursor(final String cursorName) {
        return CursorManager.cursorMap.get(cursorName);
    }
    
    public Cursor convertCursor(final Element e) {
        Value cursorValue = CSSUtilities.getComputedStyle(e, 10);
        String cursorStr = "auto";
        if (cursorValue != null) {
            if (cursorValue.getCssValueType() == 1 && cursorValue.getPrimitiveType() == 21) {
                cursorStr = cursorValue.getStringValue();
                return this.convertBuiltInCursor(e, cursorStr);
            }
            if (cursorValue.getCssValueType() == 2) {
                final int nValues = cursorValue.getLength();
                if (nValues == 1) {
                    cursorValue = cursorValue.item(0);
                    if (cursorValue.getPrimitiveType() == 21) {
                        cursorStr = cursorValue.getStringValue();
                        return this.convertBuiltInCursor(e, cursorStr);
                    }
                }
                else if (nValues > 1) {
                    return this.convertSVGCursor(e, cursorValue);
                }
            }
        }
        return this.convertBuiltInCursor(e, cursorStr);
    }
    
    public Cursor convertBuiltInCursor(final Element e, final String cursorStr) {
        Cursor cursor = null;
        if (cursorStr.charAt(0) == 'a') {
            final String nameSpaceURI = e.getNamespaceURI();
            if ("http://www.w3.org/2000/svg".equals(nameSpaceURI)) {
                final String tag = e.getLocalName();
                if ("a".equals(tag)) {
                    cursor = CursorManager.ANCHOR_CURSOR;
                }
                else if ("text".equals(tag) || "tspan".equals(tag) || "tref".equals(tag)) {
                    cursor = CursorManager.TEXT_CURSOR;
                }
                else {
                    if ("image".equals(tag)) {
                        return null;
                    }
                    cursor = CursorManager.DEFAULT_CURSOR;
                }
            }
            else {
                cursor = CursorManager.DEFAULT_CURSOR;
            }
        }
        else {
            cursor = getPredefinedCursor(cursorStr);
        }
        return cursor;
    }
    
    public Cursor convertSVGCursor(final Element e, final Value l) {
        final int nValues = l.getLength();
        Element cursorElement = null;
        for (int i = 0; i < nValues - 1; ++i) {
            final Value cursorValue = l.item(i);
            if (cursorValue.getPrimitiveType() == 20) {
                final String uri = cursorValue.getStringValue();
                try {
                    cursorElement = this.ctx.getReferencedElement(e, uri);
                }
                catch (BridgeException be) {
                    if (!"uri.badTarget".equals(be.getCode())) {
                        throw be;
                    }
                }
                if (cursorElement != null) {
                    final String cursorNS = cursorElement.getNamespaceURI();
                    if ("http://www.w3.org/2000/svg".equals(cursorNS) && "cursor".equals(cursorElement.getLocalName())) {
                        final Cursor c = this.convertSVGCursorElement(cursorElement);
                        if (c != null) {
                            return c;
                        }
                    }
                }
            }
        }
        final Value cursorValue2 = l.item(nValues - 1);
        String cursorStr = "auto";
        if (cursorValue2.getPrimitiveType() == 21) {
            cursorStr = cursorValue2.getStringValue();
        }
        return this.convertBuiltInCursor(e, cursorStr);
    }
    
    public Cursor convertSVGCursorElement(final Element cursorElement) {
        final String uriStr = XLinkSupport.getXLinkHref(cursorElement);
        if (uriStr.length() == 0) {
            throw new BridgeException(this.ctx, cursorElement, "attribute.missing", new Object[] { "xlink:href" });
        }
        final String baseURI = AbstractNode.getBaseURI(cursorElement);
        ParsedURL purl;
        if (baseURI == null) {
            purl = new ParsedURL(uriStr);
        }
        else {
            purl = new ParsedURL(baseURI, uriStr);
        }
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(this.ctx, cursorElement);
        String s = cursorElement.getAttributeNS(null, "x");
        float x = 0.0f;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        s = cursorElement.getAttributeNS(null, "y");
        float y = 0.0f;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        final CursorDescriptor desc = new CursorDescriptor(purl, x, y);
        final Cursor cachedCursor = this.cursorCache.getCursor(desc);
        if (cachedCursor != null) {
            return cachedCursor;
        }
        final Point2D.Float hotSpot = new Point2D.Float(x, y);
        final Filter f = this.cursorHrefToFilter(cursorElement, purl, hotSpot);
        if (f == null) {
            this.cursorCache.clearCursor(desc);
            return null;
        }
        final Rectangle cursorSize = f.getBounds2D().getBounds();
        final RenderedImage ri = f.createScaledRendering(cursorSize.width, cursorSize.height, null);
        Image img = null;
        if (ri instanceof Image) {
            img = (Image)ri;
        }
        else {
            img = this.renderedImageToImage(ri);
        }
        hotSpot.x = ((hotSpot.x < 0.0f) ? 0.0f : hotSpot.x);
        hotSpot.y = ((hotSpot.y < 0.0f) ? 0.0f : hotSpot.y);
        hotSpot.x = ((hotSpot.x > cursorSize.width - 1) ? ((float)(cursorSize.width - 1)) : hotSpot.x);
        hotSpot.y = ((hotSpot.y > cursorSize.height - 1) ? ((float)(cursorSize.height - 1)) : hotSpot.y);
        final Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(Math.round(hotSpot.x), Math.round(hotSpot.y)), purl.toString());
        this.cursorCache.putCursor(desc, c);
        return c;
    }
    
    protected Filter cursorHrefToFilter(final Element cursorElement, final ParsedURL purl, final Point2D hotSpot) {
        AffineRable8Bit f = null;
        final String uriStr = purl.toString();
        Dimension cursorSize = null;
        final DocumentLoader loader = this.ctx.getDocumentLoader();
        final SVGDocument svgDoc = (SVGDocument)cursorElement.getOwnerDocument();
        final URIResolver resolver = this.ctx.createURIResolver(svgDoc, loader);
        try {
            Element rootElement = null;
            final Node n = resolver.getNode(uriStr, cursorElement);
            if (n.getNodeType() != 9) {
                throw new BridgeException(this.ctx, cursorElement, "uri.image.invalid", new Object[] { uriStr });
            }
            final SVGDocument doc = (SVGDocument)n;
            this.ctx.initializeDocument(doc);
            rootElement = doc.getRootElement();
            final GraphicsNode node = this.ctx.getGVTBuilder().build(this.ctx, rootElement);
            float width = 32.0f;
            float height = 32.0f;
            final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(this.ctx, rootElement);
            String s = rootElement.getAttribute("width");
            if (s.length() != 0) {
                width = UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
            }
            s = rootElement.getAttribute("height");
            if (s.length() != 0) {
                height = UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
            }
            cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(Math.round(width), Math.round(height));
            final AffineTransform at = ViewBox.getPreserveAspectRatioTransform(rootElement, (float)cursorSize.width, (float)cursorSize.height, this.ctx);
            final Filter filter = node.getGraphicsNodeRable(true);
            f = new AffineRable8Bit(filter, at);
        }
        catch (BridgeException ex) {
            throw ex;
        }
        catch (SecurityException ex2) {
            throw new BridgeException(this.ctx, cursorElement, ex2, "uri.unsecure", new Object[] { uriStr });
        }
        catch (Exception ex3) {}
        if (f == null) {
            final ImageTagRegistry reg = ImageTagRegistry.getRegistry();
            final Filter filter2 = reg.readURL(purl);
            if (filter2 == null) {
                return null;
            }
            if (BrokenLinkProvider.hasBrokenLinkProperty(filter2)) {
                return null;
            }
            final Rectangle preferredSize = filter2.getBounds2D().getBounds();
            cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(preferredSize.width, preferredSize.height);
            if (preferredSize == null || preferredSize.width <= 0 || preferredSize.height <= 0) {
                return null;
            }
            AffineTransform at2 = new AffineTransform();
            if (preferredSize.width > cursorSize.width || preferredSize.height > cursorSize.height) {
                at2 = ViewBox.getPreserveAspectRatioTransform(new float[] { 0.0f, 0.0f, (float)preferredSize.width, (float)preferredSize.height }, (short)2, true, (float)cursorSize.width, (float)cursorSize.height);
            }
            f = new AffineRable8Bit(filter2, at2);
        }
        final AffineTransform at3 = f.getAffine();
        at3.transform(hotSpot, hotSpot);
        final Rectangle cursorViewport = new Rectangle(0, 0, cursorSize.width, cursorSize.height);
        final PadRable8Bit cursorImage = new PadRable8Bit(f, cursorViewport, PadMode.ZERO_PAD);
        return cursorImage;
    }
    
    protected Image renderedImageToImage(final RenderedImage ri) {
        final int x = ri.getMinX();
        final int y = ri.getMinY();
        final SampleModel sm = ri.getSampleModel();
        final ColorModel cm = ri.getColorModel();
        final WritableRaster wr = Raster.createWritableRaster(sm, new Point(x, y));
        ri.copyData(wr);
        return new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
    }
    
    static {
        DEFAULT_CURSOR = Cursor.getPredefinedCursor(0);
        ANCHOR_CURSOR = Cursor.getPredefinedCursor(12);
        TEXT_CURSOR = Cursor.getPredefinedCursor(2);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        (CursorManager.cursorMap = new HashMap()).put("crosshair", Cursor.getPredefinedCursor(1));
        CursorManager.cursorMap.put("default", Cursor.getPredefinedCursor(0));
        CursorManager.cursorMap.put("pointer", Cursor.getPredefinedCursor(12));
        CursorManager.cursorMap.put("e-resize", Cursor.getPredefinedCursor(11));
        CursorManager.cursorMap.put("ne-resize", Cursor.getPredefinedCursor(7));
        CursorManager.cursorMap.put("nw-resize", Cursor.getPredefinedCursor(6));
        CursorManager.cursorMap.put("n-resize", Cursor.getPredefinedCursor(8));
        CursorManager.cursorMap.put("se-resize", Cursor.getPredefinedCursor(5));
        CursorManager.cursorMap.put("sw-resize", Cursor.getPredefinedCursor(4));
        CursorManager.cursorMap.put("s-resize", Cursor.getPredefinedCursor(9));
        CursorManager.cursorMap.put("w-resize", Cursor.getPredefinedCursor(10));
        CursorManager.cursorMap.put("text", Cursor.getPredefinedCursor(2));
        CursorManager.cursorMap.put("wait", Cursor.getPredefinedCursor(3));
        Cursor moveCursor = Cursor.getPredefinedCursor(13);
        if (Platform.isOSX) {
            try {
                final Image img = toolkit.createImage(CursorManager.class.getResource("resources/move.gif"));
                moveCursor = toolkit.createCustomCursor(img, new Point(11, 11), "move");
            }
            catch (Exception ex2) {}
        }
        CursorManager.cursorMap.put("move", moveCursor);
        Cursor helpCursor;
        try {
            final Image img2 = toolkit.createImage(CursorManager.class.getResource("resources/help.gif"));
            helpCursor = toolkit.createCustomCursor(img2, new Point(1, 3), "help");
        }
        catch (Exception ex) {
            helpCursor = Cursor.getPredefinedCursor(12);
        }
        CursorManager.cursorMap.put("help", helpCursor);
    }
    
    static class CursorDescriptor
    {
        ParsedURL purl;
        float x;
        float y;
        String desc;
        
        public CursorDescriptor(final ParsedURL purl, final float x, final float y) {
            if (purl == null) {
                throw new IllegalArgumentException();
            }
            this.purl = purl;
            this.x = x;
            this.y = y;
            this.desc = this.getClass().getName() + "\n\t:[" + this.purl + "]\n\t:[" + x + "]:[" + y + "]";
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof CursorDescriptor)) {
                return false;
            }
            final CursorDescriptor desc = (CursorDescriptor)obj;
            final boolean isEqual = this.purl.equals(desc.purl) && this.x == desc.x && this.y == desc.y;
            return isEqual;
        }
        
        @Override
        public String toString() {
            return this.desc;
        }
        
        @Override
        public int hashCode() {
            return this.desc.hashCode();
        }
    }
    
    static class CursorCache extends SoftReferenceCache
    {
        public CursorCache() {
        }
        
        public Cursor getCursor(final CursorDescriptor desc) {
            return (Cursor)this.requestImpl(desc);
        }
        
        public void putCursor(final CursorDescriptor desc, final Cursor cursor) {
            this.putImpl(desc, cursor);
        }
        
        public void clearCursor(final CursorDescriptor desc) {
            this.clearImpl(desc);
        }
    }
}
