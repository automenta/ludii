// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.DocumentEvent;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.w3c.dom.events.Event;
import java.io.BufferedInputStream;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import java.awt.color.ICC_Profile;
import java.awt.geom.NoninvertibleTransformException;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.events.EventTarget;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.w3c.dom.svg.SVGSVGElement;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.anim.dom.SVGOMElement;
import java.util.List;
import java.util.Iterator;
import org.apache.batik.util.MimeTypeConstants;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.w3c.dom.Document;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import java.awt.geom.Rectangle2D;
import java.io.InterruptedIOException;
import org.apache.batik.util.HaltingThread;
import java.io.InputStream;
import java.io.IOException;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import java.awt.Shape;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractNode;
import java.awt.RenderingHints;
import org.w3c.dom.svg.SVGImageElement;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGDocument;

public class SVGImageElementBridge extends AbstractGraphicsNodeBridge
{
    protected SVGDocument imgDocument;
    protected EventListener listener;
    protected BridgeContext subCtx;
    protected boolean hitCheckChildren;
    static SVGBrokenLinkProvider brokenLinkProvider;
    
    public SVGImageElementBridge() {
        this.listener = null;
        this.subCtx = null;
        this.hitCheckChildren = false;
    }
    
    @Override
    public String getLocalName() {
        return "image";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGImageElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        final ImageNode imageNode = (ImageNode)super.createGraphicsNode(ctx, e);
        if (imageNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, imageNode);
        this.hitCheckChildren = false;
        final GraphicsNode node = this.buildImageGraphicsNode(ctx, e);
        if (node == null) {
            final SVGImageElement ie = (SVGImageElement)e;
            final String uriStr = ie.getHref().getAnimVal();
            throw new BridgeException(ctx, e, "uri.image.invalid", new Object[] { uriStr });
        }
        imageNode.setImage(node);
        imageNode.setHitCheckChildren(this.hitCheckChildren);
        RenderingHints hints = null;
        hints = CSSUtilities.convertImageRendering(e, hints);
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            imageNode.setRenderingHints(hints);
        }
        return imageNode;
    }
    
    protected GraphicsNode buildImageGraphicsNode(final BridgeContext ctx, final Element e) {
        final SVGImageElement ie = (SVGImageElement)e;
        final String uriStr = ie.getHref().getAnimVal();
        if (uriStr.length() == 0) {
            throw new BridgeException(ctx, e, "attribute.missing", new Object[] { "xlink:href" });
        }
        if (uriStr.indexOf(35) != -1) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "xlink:href", uriStr });
        }
        final String baseURI = AbstractNode.getBaseURI(e);
        ParsedURL purl;
        if (baseURI == null) {
            purl = new ParsedURL(uriStr);
        }
        else {
            purl = new ParsedURL(baseURI, uriStr);
        }
        return this.createImageGraphicsNode(ctx, e, purl);
    }
    
    protected GraphicsNode createImageGraphicsNode(final BridgeContext ctx, final Element e, final ParsedURL purl) {
        final Rectangle2D bounds = getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            final ShapeNode sn = new ShapeNode();
            sn.setShape(bounds);
            return sn;
        }
        final SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
        final String docURL = svgDoc.getURL();
        ParsedURL pDocURL = null;
        if (docURL != null) {
            pDocURL = new ParsedURL(docURL);
        }
        final UserAgent userAgent = ctx.getUserAgent();
        try {
            userAgent.checkLoadExternalResource(purl, pDocURL);
        }
        catch (SecurityException secEx) {
            throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[] { purl });
        }
        final DocumentLoader loader = ctx.getDocumentLoader();
        final ImageTagRegistry reg = ImageTagRegistry.getRegistry();
        final ICCColorSpaceWithIntent colorspace = extractColorSpace(e, ctx);
        try {
            final Document doc = loader.checkCache(purl.toString());
            if (doc != null) {
                this.imgDocument = (SVGDocument)doc;
                return this.createSVGImageNode(ctx, e, this.imgDocument);
            }
        }
        catch (BridgeException ex) {
            throw ex;
        }
        catch (Exception ex3) {}
        final Filter img = reg.checkCache(purl, colorspace);
        if (img != null) {
            return this.createRasterImageNode(ctx, e, img, purl);
        }
        ProtectedStream reference = null;
        try {
            reference = this.openStream(e, purl);
        }
        catch (SecurityException secEx2) {
            throw new BridgeException(ctx, e, secEx2, "uri.unsecure", new Object[] { purl });
        }
        catch (IOException ioe) {
            return this.createBrokenImageNode(ctx, e, purl.toString(), ioe.getLocalizedMessage());
        }
        Filter img2 = reg.readURL(reference, purl, colorspace, false, false);
        if (img2 != null) {
            try {
                reference.tie();
            }
            catch (IOException ex4) {}
            return this.createRasterImageNode(ctx, e, img2, purl);
        }
        try {
            reference.retry();
        }
        catch (IOException ioe) {
            reference.release();
            reference = null;
            try {
                reference = this.openStream(e, purl);
            }
            catch (IOException ioe2) {
                return this.createBrokenImageNode(ctx, e, purl.toString(), ioe2.getLocalizedMessage());
            }
        }
        try {
            final Document doc2 = loader.loadDocument(purl.toString(), reference);
            reference.release();
            this.imgDocument = (SVGDocument)doc2;
            return this.createSVGImageNode(ctx, e, this.imgDocument);
        }
        catch (BridgeException ex2) {
            reference.release();
            throw ex2;
        }
        catch (SecurityException secEx2) {
            reference.release();
            throw new BridgeException(ctx, e, secEx2, "uri.unsecure", new Object[] { purl });
        }
        catch (InterruptedIOException iioe) {
            reference.release();
            if (HaltingThread.hasBeenHalted()) {
                throw new InterruptedBridgeException();
            }
        }
        catch (InterruptedBridgeException ibe) {
            reference.release();
            throw ibe;
        }
        catch (Exception ex5) {}
        try {
            reference.retry();
        }
        catch (IOException ioe) {
            reference.release();
            reference = null;
            try {
                reference = this.openStream(e, purl);
            }
            catch (IOException ioe2) {
                return this.createBrokenImageNode(ctx, e, purl.toString(), ioe2.getLocalizedMessage());
            }
        }
        try {
            img2 = reg.readURL(reference, purl, colorspace, true, true);
            if (img2 != null) {
                return this.createRasterImageNode(ctx, e, img2, purl);
            }
        }
        finally {
            reference.release();
        }
        return null;
    }
    
    protected ProtectedStream openStream(final Element e, final ParsedURL purl) throws IOException {
        final List mimeTypes = new ArrayList(ImageTagRegistry.getRegistry().getRegisteredMimeTypes());
        mimeTypes.addAll(MimeTypeConstants.MIME_TYPES_SVG_LIST);
        final InputStream reference = purl.openStream(mimeTypes.iterator());
        return new ProtectedStream(reference);
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new ImageNode();
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    protected void initializeDynamicSupport(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        if (!ctx.isInteractive()) {
            return;
        }
        ctx.bind(e, node);
        if (ctx.isDynamic()) {
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext(this);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        try {
            final String ns = alav.getNamespaceURI();
            final String ln = alav.getLocalName();
            if (ns == null) {
                if (ln.equals("x") || ln.equals("y")) {
                    this.updateImageBounds();
                    return;
                }
                if (ln.equals("width") || ln.equals("height")) {
                    final SVGImageElement ie = (SVGImageElement)this.e;
                    final ImageNode imageNode = (ImageNode)this.node;
                    AbstractSVGAnimatedLength _attr;
                    if (ln.charAt(0) == 'w') {
                        _attr = (AbstractSVGAnimatedLength)ie.getWidth();
                    }
                    else {
                        _attr = (AbstractSVGAnimatedLength)ie.getHeight();
                    }
                    final float val = _attr.getCheckedValue();
                    if (val == 0.0f || imageNode.getImage() instanceof ShapeNode) {
                        this.rebuildImageNode();
                    }
                    else {
                        this.updateImageBounds();
                    }
                    return;
                }
                if (ln.equals("preserveAspectRatio")) {
                    this.updateImageBounds();
                    return;
                }
            }
            else if (ns.equals("http://www.w3.org/1999/xlink") && ln.equals("href")) {
                this.rebuildImageNode();
                return;
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(this.ctx, ex);
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    protected void updateImageBounds() {
        final Rectangle2D bounds = getImageBounds(this.ctx, this.e);
        final GraphicsNode imageNode = ((ImageNode)this.node).getImage();
        float[] vb = null;
        if (imageNode instanceof RasterImageNode) {
            final Rectangle2D imgBounds = ((RasterImageNode)imageNode).getImageBounds();
            vb = new float[] { 0.0f, 0.0f, (float)imgBounds.getWidth(), (float)imgBounds.getHeight() };
        }
        else if (this.imgDocument != null) {
            final Element svgElement = this.imgDocument.getRootElement();
            final String viewBox = svgElement.getAttributeNS(null, "viewBox");
            vb = ViewBox.parseViewBoxAttribute(this.e, viewBox, this.ctx);
        }
        if (imageNode != null) {
            initializeViewport(this.ctx, this.e, imageNode, vb, bounds);
        }
    }
    
    protected void rebuildImageNode() {
        if (this.imgDocument != null && this.listener != null) {
            final NodeEventTarget tgt = (NodeEventTarget)this.imgDocument.getRootElement();
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
            this.listener = null;
        }
        if (this.imgDocument != null) {
            final SVGSVGElement svgElement = this.imgDocument.getRootElement();
            AbstractGraphicsNodeBridge.disposeTree(svgElement);
        }
        this.imgDocument = null;
        this.subCtx = null;
        final GraphicsNode inode = this.buildImageGraphicsNode(this.ctx, this.e);
        final ImageNode imgNode = (ImageNode)this.node;
        imgNode.setImage(inode);
        if (inode == null) {
            final SVGImageElement ie = (SVGImageElement)this.e;
            final String uriStr = ie.getHref().getAnimVal();
            throw new BridgeException(this.ctx, this.e, "uri.image.invalid", new Object[] { uriStr });
        }
    }
    
    @Override
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 6:
            case 30: {
                RenderingHints hints = CSSUtilities.convertImageRendering(this.e, null);
                hints = CSSUtilities.convertColorRendering(this.e, hints);
                if (hints != null) {
                    this.node.setRenderingHints(hints);
                    break;
                }
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
                break;
            }
        }
    }
    
    protected GraphicsNode createRasterImageNode(final BridgeContext ctx, final Element e, final Filter img, final ParsedURL purl) {
        final Rectangle2D bounds = getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            final ShapeNode sn = new ShapeNode();
            sn.setShape(bounds);
            return sn;
        }
        if (BrokenLinkProvider.hasBrokenLinkProperty(img)) {
            final Object o = img.getProperty("org.apache.batik.BrokenLinkImage");
            String msg = "unknown";
            if (o instanceof String) {
                msg = (String)o;
            }
            final SVGDocument doc = ctx.getUserAgent().getBrokenLinkDocument(e, purl.toString(), msg);
            return this.createSVGImageNode(ctx, e, doc);
        }
        final RasterImageNode node = new RasterImageNode();
        node.setImage(img);
        final Rectangle2D imgBounds = img.getBounds2D();
        final float[] vb = { 0.0f, 0.0f, (float)imgBounds.getWidth(), (float)imgBounds.getHeight() };
        initializeViewport(ctx, e, node, vb, bounds);
        return node;
    }
    
    protected GraphicsNode createSVGImageNode(final BridgeContext ctx, final Element e, final SVGDocument imgDocument) {
        final CSSEngine eng = ((SVGOMDocument)imgDocument).getCSSEngine();
        this.subCtx = ctx.createSubBridgeContext((SVGOMDocument)imgDocument);
        final CompositeGraphicsNode result = new CompositeGraphicsNode();
        final Rectangle2D bounds = getImageBounds(ctx, e);
        if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
            final ShapeNode sn = new ShapeNode();
            sn.setShape(bounds);
            result.getChildren().add(sn);
            return result;
        }
        final Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            result.setBackgroundEnable(r);
        }
        final SVGSVGElement svgElement = imgDocument.getRootElement();
        final CanvasGraphicsNode node = (CanvasGraphicsNode)this.subCtx.getGVTBuilder().build(this.subCtx, svgElement);
        if (eng == null && ctx.isInteractive()) {
            this.subCtx.addUIEventListeners(imgDocument);
        }
        node.setClip(null);
        node.setViewingTransform(new AffineTransform());
        result.getChildren().add(node);
        final String viewBox = svgElement.getAttributeNS(null, "viewBox");
        final float[] vb = ViewBox.parseViewBoxAttribute(e, viewBox, ctx);
        initializeViewport(ctx, e, result, vb, bounds);
        if (ctx.isInteractive()) {
            this.listener = new ForwardEventListener(svgElement, e);
            final NodeEventTarget tgt = (NodeEventTarget)svgElement;
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false, null);
            this.subCtx.storeEventListenerNS(tgt, "http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
        }
        return result;
    }
    
    @Override
    public void dispose() {
        if (this.imgDocument != null && this.listener != null) {
            final NodeEventTarget tgt = (NodeEventTarget)this.imgDocument.getRootElement();
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.listener, false);
            tgt.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.listener, false);
            this.listener = null;
        }
        if (this.imgDocument != null) {
            final SVGSVGElement svgElement = this.imgDocument.getRootElement();
            AbstractGraphicsNodeBridge.disposeTree(svgElement);
            this.imgDocument = null;
            this.subCtx = null;
        }
        super.dispose();
    }
    
    protected static void initializeViewport(final BridgeContext ctx, final Element e, final GraphicsNode node, final float[] vb, final Rectangle2D bounds) {
        final float x = (float)bounds.getX();
        final float y = (float)bounds.getY();
        final float w = (float)bounds.getWidth();
        final float h = (float)bounds.getHeight();
        try {
            final SVGImageElement ie = (SVGImageElement)e;
            final SVGOMAnimatedPreserveAspectRatio _par = (SVGOMAnimatedPreserveAspectRatio)ie.getPreserveAspectRatio();
            _par.check();
            AffineTransform at = ViewBox.getPreserveAspectRatioTransform(e, vb, w, h, _par, ctx);
            at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
            node.setTransform(at);
            Shape clip = null;
            if (CSSUtilities.convertOverflow(e)) {
                final float[] offsets = CSSUtilities.convertClip(e);
                if (offsets == null) {
                    clip = new Rectangle2D.Float(x, y, w, h);
                }
                else {
                    clip = new Rectangle2D.Float(x + offsets[3], y + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
                }
            }
            if (clip != null) {
                try {
                    at = at.createInverse();
                    final Filter filter = node.getGraphicsNodeRable(true);
                    clip = at.createTransformedShape(clip);
                    node.setClip(new ClipRable8Bit(filter, clip));
                }
                catch (NoninvertibleTransformException ex2) {}
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    protected static ICCColorSpaceWithIntent extractColorSpace(final Element element, final BridgeContext ctx) {
        final String colorProfileProperty = CSSUtilities.getComputedStyle(element, 8).getStringValue();
        ICCColorSpaceWithIntent colorSpace = null;
        if ("srgb".equalsIgnoreCase(colorProfileProperty)) {
            colorSpace = new ICCColorSpaceWithIntent(ICC_Profile.getInstance(1000), RenderingIntent.AUTO, "sRGB", null);
        }
        else if (!"auto".equalsIgnoreCase(colorProfileProperty) && !"".equalsIgnoreCase(colorProfileProperty)) {
            final SVGColorProfileElementBridge profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile");
            if (profileBridge != null) {
                colorSpace = profileBridge.createICCColorSpaceWithIntent(ctx, element, colorProfileProperty);
            }
        }
        return colorSpace;
    }
    
    protected static Rectangle2D getImageBounds(final BridgeContext ctx, final Element element) {
        try {
            final SVGImageElement ie = (SVGImageElement)element;
            final AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)ie.getX();
            final float x = _x.getCheckedValue();
            final AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)ie.getY();
            final float y = _y.getCheckedValue();
            final AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)ie.getWidth();
            final float w = _width.getCheckedValue();
            final AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)ie.getHeight();
            final float h = _height.getCheckedValue();
            return new Rectangle2D.Float(x, y, w, h);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    GraphicsNode createBrokenImageNode(final BridgeContext ctx, final Element e, final String uri, final String message) {
        final SVGDocument doc = ctx.getUserAgent().getBrokenLinkDocument(e, uri, Messages.formatMessage("uri.image.error", new Object[] { message }));
        return this.createSVGImageNode(ctx, e, doc);
    }
    
    static {
        ImageTagRegistry.setBrokenLinkProvider(SVGImageElementBridge.brokenLinkProvider = new SVGBrokenLinkProvider());
    }
    
    public static class ProtectedStream extends BufferedInputStream
    {
        static final int BUFFER_SIZE = 8192;
        boolean wasClosed;
        boolean isTied;
        
        ProtectedStream(final InputStream is) {
            super(is, 8192);
            this.wasClosed = false;
            this.isTied = false;
            super.mark(8192);
        }
        
        ProtectedStream(final InputStream is, final int size) {
            super(is, size);
            this.wasClosed = false;
            this.isTied = false;
            super.mark(size);
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public void mark(final int sz) {
        }
        
        @Override
        public void reset() throws IOException {
            throw new IOException("Reset unsupported");
        }
        
        public synchronized void retry() throws IOException {
            super.reset();
            this.wasClosed = false;
            this.isTied = false;
        }
        
        @Override
        public synchronized void close() throws IOException {
            this.wasClosed = true;
            if (this.isTied) {
                super.close();
            }
        }
        
        public synchronized void tie() throws IOException {
            this.isTied = true;
            if (this.wasClosed) {
                super.close();
            }
        }
        
        public void release() {
            try {
                super.close();
            }
            catch (IOException ex) {}
        }
    }
    
    protected static class ForwardEventListener implements EventListener
    {
        protected Element svgElement;
        protected Element imgElement;
        
        public ForwardEventListener(final Element svgElement, final Element imgElement) {
            this.svgElement = svgElement;
            this.imgElement = imgElement;
        }
        
        @Override
        public void handleEvent(final Event e) {
            final DOMMouseEvent evt = (DOMMouseEvent)e;
            final DOMMouseEvent newMouseEvent = (DOMMouseEvent)((DocumentEvent)this.imgElement.getOwnerDocument()).createEvent("MouseEvents");
            newMouseEvent.initMouseEventNS("http://www.w3.org/2001/xml-events", evt.getType(), evt.getBubbles(), evt.getCancelable(), evt.getView(), evt.getDetail(), evt.getScreenX(), evt.getScreenY(), evt.getClientX(), evt.getClientY(), evt.getButton(), (EventTarget)this.imgElement, evt.getModifiersString());
            ((EventTarget)this.imgElement).dispatchEvent(newMouseEvent);
        }
    }
}
