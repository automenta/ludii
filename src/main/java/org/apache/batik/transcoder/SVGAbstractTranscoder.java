// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import java.util.StringTokenizer;
import java.util.LinkedList;
import org.apache.batik.bridge.RelaxedScriptSecurity;
import org.apache.batik.bridge.DefaultScriptSecurity;
import org.apache.batik.bridge.NoLoadScriptSecurity;
import org.apache.batik.bridge.ScriptSecurity;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.transcoder.keys.Rectangle2DKey;
import org.apache.batik.transcoder.keys.LengthKey;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import java.util.List;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.w3c.dom.svg.SVGSVGElement;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.bridge.BridgeException;
import org.w3c.dom.Element;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Document;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.DocumentFactory;
import org.w3c.dom.DOMImplementation;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class SVGAbstractTranscoder extends XMLAbstractTranscoder
{
    public static final String DEFAULT_DEFAULT_FONT_FAMILY = "Arial, Helvetica, sans-serif";
    protected Rectangle2D curAOI;
    protected AffineTransform curTxf;
    protected GraphicsNode root;
    protected BridgeContext ctx;
    protected GVTBuilder builder;
    protected float width;
    protected float height;
    protected UserAgent userAgent;
    public static final TranscodingHints.Key KEY_WIDTH;
    public static final TranscodingHints.Key KEY_HEIGHT;
    public static final TranscodingHints.Key KEY_MAX_WIDTH;
    public static final TranscodingHints.Key KEY_MAX_HEIGHT;
    public static final TranscodingHints.Key KEY_AOI;
    public static final TranscodingHints.Key KEY_LANGUAGE;
    public static final TranscodingHints.Key KEY_MEDIA;
    public static final TranscodingHints.Key KEY_DEFAULT_FONT_FAMILY;
    public static final TranscodingHints.Key KEY_ALTERNATE_STYLESHEET;
    public static final TranscodingHints.Key KEY_USER_STYLESHEET_URI;
    public static final TranscodingHints.Key KEY_PIXEL_UNIT_TO_MILLIMETER;
    @Deprecated
    public static final TranscodingHints.Key KEY_PIXEL_TO_MM;
    public static final TranscodingHints.Key KEY_EXECUTE_ONLOAD;
    public static final TranscodingHints.Key KEY_SNAPSHOT_TIME;
    public static final TranscodingHints.Key KEY_ALLOWED_SCRIPT_TYPES;
    public static final String DEFAULT_ALLOWED_SCRIPT_TYPES = "text/ecmascript, application/ecmascript, text/javascript, application/javascript, application/java-archive";
    public static final TranscodingHints.Key KEY_CONSTRAIN_SCRIPT_ORIGIN;
    
    protected SVGAbstractTranscoder() {
        this.width = 400.0f;
        this.height = 400.0f;
        this.userAgent = this.createUserAgent();
        this.hints.put(SVGAbstractTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, "http://www.w3.org/2000/svg");
        this.hints.put(SVGAbstractTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        this.hints.put(SVGAbstractTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
        this.hints.put(SVGAbstractTranscoder.KEY_MEDIA, "screen");
        this.hints.put(SVGAbstractTranscoder.KEY_DEFAULT_FONT_FAMILY, "Arial, Helvetica, sans-serif");
        this.hints.put(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.FALSE);
        this.hints.put(SVGAbstractTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "text/ecmascript, application/ecmascript, text/javascript, application/javascript, application/java-archive");
    }
    
    protected UserAgent createUserAgent() {
        return new SVGAbstractTranscoderUserAgent();
    }
    
    @Override
    protected DocumentFactory createDocumentFactory(final DOMImplementation domImpl, final String parserClassname) {
        return new SAXSVGDocumentFactory(parserClassname);
    }
    
    @Override
    public void transcode(final TranscoderInput input, final TranscoderOutput output) throws TranscoderException {
        super.transcode(input, output);
        if (this.ctx != null) {
            this.ctx.dispose();
        }
    }
    
    @Override
    protected void transcode(Document document, final String uri, final TranscoderOutput output) throws TranscoderException {
        if (document != null && !(document.getImplementation() instanceof SVGDOMImplementation)) {
            final DOMImplementation impl = (DOMImplementation)this.hints.get(SVGAbstractTranscoder.KEY_DOM_IMPLEMENTATION);
            document = DOMUtilities.deepCloneDocument(document, impl);
            if (uri != null) {
                final ParsedURL url = new ParsedURL(uri);
                ((SVGOMDocument)document).setParsedURL(url);
            }
        }
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_WIDTH)) {
            this.width = (float)this.hints.get(SVGAbstractTranscoder.KEY_WIDTH);
        }
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_HEIGHT)) {
            this.height = (float)this.hints.get(SVGAbstractTranscoder.KEY_HEIGHT);
        }
        final SVGOMDocument svgDoc = (SVGOMDocument)document;
        final SVGSVGElement root = svgDoc.getRootElement();
        this.ctx = this.createBridgeContext(svgDoc);
        this.builder = new GVTBuilder();
        final boolean isDynamic = this.hints.containsKey(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD) && (boolean)this.hints.get(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD);
        GraphicsNode gvtRoot;
        try {
            if (isDynamic) {
                this.ctx.setDynamicState(2);
            }
            gvtRoot = this.builder.build(this.ctx, svgDoc);
            if (this.ctx.isDynamic()) {
                final BaseScriptingEnvironment se = new BaseScriptingEnvironment(this.ctx);
                se.loadScripts();
                se.dispatchSVGLoadEvent();
                if (this.hints.containsKey(SVGAbstractTranscoder.KEY_SNAPSHOT_TIME)) {
                    final float t = (float)this.hints.get(SVGAbstractTranscoder.KEY_SNAPSHOT_TIME);
                    this.ctx.getAnimationEngine().setCurrentTime(t);
                }
                else if (this.ctx.isSVG12()) {
                    final float t = SVGUtilities.convertSnapshotTime(root, null);
                    this.ctx.getAnimationEngine().setCurrentTime(t);
                }
            }
        }
        catch (BridgeException ex) {
            throw new TranscoderException(ex);
        }
        final float docWidth = (float)this.ctx.getDocumentSize().getWidth();
        final float docHeight = (float)this.ctx.getDocumentSize().getHeight();
        this.setImageSize(docWidth, docHeight);
        AffineTransform Px;
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_AOI)) {
            final Rectangle2D aoi = (Rectangle2D)this.hints.get(SVGAbstractTranscoder.KEY_AOI);
            Px = new AffineTransform();
            final double sx = this.width / aoi.getWidth();
            final double sy = this.height / aoi.getHeight();
            final double scale = Math.min(sx, sy);
            Px.scale(scale, scale);
            final double tx = -aoi.getX() + (this.width / scale - aoi.getWidth()) / 2.0;
            final double ty = -aoi.getY() + (this.height / scale - aoi.getHeight()) / 2.0;
            Px.translate(tx, ty);
            this.curAOI = aoi;
        }
        else {
            final String ref = new ParsedURL(uri).getRef();
            final String viewBox = root.getAttributeNS(null, "viewBox");
            if (ref != null && ref.length() != 0) {
                Px = ViewBox.getViewTransform(ref, root, this.width, this.height, this.ctx);
            }
            else if (viewBox != null && viewBox.length() != 0) {
                final String aspectRatio = root.getAttributeNS(null, "preserveAspectRatio");
                Px = ViewBox.getPreserveAspectRatioTransform(root, viewBox, aspectRatio, this.width, this.height, this.ctx);
            }
            else {
                final float xscale = this.width / docWidth;
                final float yscale = this.height / docHeight;
                final float scale2 = Math.min(xscale, yscale);
                Px = AffineTransform.getScaleInstance(scale2, scale2);
            }
            this.curAOI = new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height);
        }
        final CanvasGraphicsNode cgn = this.getCanvasGraphicsNode(gvtRoot);
        if (cgn != null) {
            cgn.setViewingTransform(Px);
            this.curTxf = new AffineTransform();
        }
        else {
            this.curTxf = Px;
        }
        this.root = gvtRoot;
    }
    
    protected CanvasGraphicsNode getCanvasGraphicsNode(GraphicsNode gn) {
        if (!(gn instanceof CompositeGraphicsNode)) {
            return null;
        }
        final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
        final List children = cgn.getChildren();
        if (children.size() == 0) {
            return null;
        }
        gn = children.get(0);
        if (!(gn instanceof CanvasGraphicsNode)) {
            return null;
        }
        return (CanvasGraphicsNode)gn;
    }
    
    protected BridgeContext createBridgeContext(final SVGOMDocument doc) {
        return this.createBridgeContext(doc.isSVG12() ? "1.2" : "1.x");
    }
    
    protected BridgeContext createBridgeContext() {
        return this.createBridgeContext("1.x");
    }
    
    protected BridgeContext createBridgeContext(final String svgVersion) {
        if ("1.2".equals(svgVersion)) {
            return new SVG12BridgeContext(this.userAgent);
        }
        return new BridgeContext(this.userAgent);
    }
    
    protected void setImageSize(final float docWidth, final float docHeight) {
        float imgWidth = -1.0f;
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_WIDTH)) {
            imgWidth = (float)this.hints.get(SVGAbstractTranscoder.KEY_WIDTH);
        }
        float imgHeight = -1.0f;
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_HEIGHT)) {
            imgHeight = (float)this.hints.get(SVGAbstractTranscoder.KEY_HEIGHT);
        }
        if (imgWidth > 0.0f && imgHeight > 0.0f) {
            this.width = imgWidth;
            this.height = imgHeight;
        }
        else if (imgHeight > 0.0f) {
            this.width = docWidth * imgHeight / docHeight;
            this.height = imgHeight;
        }
        else if (imgWidth > 0.0f) {
            this.width = imgWidth;
            this.height = docHeight * imgWidth / docWidth;
        }
        else {
            this.width = docWidth;
            this.height = docHeight;
        }
        float imgMaxWidth = -1.0f;
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_MAX_WIDTH)) {
            imgMaxWidth = (float)this.hints.get(SVGAbstractTranscoder.KEY_MAX_WIDTH);
        }
        float imgMaxHeight = -1.0f;
        if (this.hints.containsKey(SVGAbstractTranscoder.KEY_MAX_HEIGHT)) {
            imgMaxHeight = (float)this.hints.get(SVGAbstractTranscoder.KEY_MAX_HEIGHT);
        }
        if (imgMaxHeight > 0.0f && this.height > imgMaxHeight) {
            this.width = docWidth * imgMaxHeight / docHeight;
            this.height = imgMaxHeight;
        }
        if (imgMaxWidth > 0.0f && this.width > imgMaxWidth) {
            this.width = imgMaxWidth;
            this.height = docHeight * imgMaxWidth / docWidth;
        }
    }
    
    static {
        KEY_WIDTH = new LengthKey();
        KEY_HEIGHT = new LengthKey();
        KEY_MAX_WIDTH = new LengthKey();
        KEY_MAX_HEIGHT = new LengthKey();
        KEY_AOI = new Rectangle2DKey();
        KEY_LANGUAGE = new StringKey();
        KEY_MEDIA = new StringKey();
        KEY_DEFAULT_FONT_FAMILY = new StringKey();
        KEY_ALTERNATE_STYLESHEET = new StringKey();
        KEY_USER_STYLESHEET_URI = new StringKey();
        KEY_PIXEL_UNIT_TO_MILLIMETER = new FloatKey();
        KEY_PIXEL_TO_MM = SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER;
        KEY_EXECUTE_ONLOAD = new BooleanKey();
        KEY_SNAPSHOT_TIME = new FloatKey();
        KEY_ALLOWED_SCRIPT_TYPES = new StringKey();
        KEY_CONSTRAIN_SCRIPT_ORIGIN = new BooleanKey();
    }
    
    protected class SVGAbstractTranscoderUserAgent extends UserAgentAdapter
    {
        protected List scripts;
        
        public SVGAbstractTranscoderUserAgent() {
            this.addStdFeatures();
        }
        
        @Override
        public AffineTransform getTransform() {
            return SVGAbstractTranscoder.this.curTxf;
        }
        
        @Override
        public void setTransform(final AffineTransform at) {
            SVGAbstractTranscoder.this.curTxf = at;
        }
        
        @Override
        public Dimension2D getViewportSize() {
            return new Dimension((int)SVGAbstractTranscoder.this.width, (int)SVGAbstractTranscoder.this.height);
        }
        
        @Override
        public void displayError(final String message) {
            try {
                SVGAbstractTranscoder.this.handler.error(new TranscoderException(message));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        
        @Override
        public void displayError(final Exception e) {
            try {
                e.printStackTrace();
                SVGAbstractTranscoder.this.handler.error(new TranscoderException(e));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        
        @Override
        public void displayMessage(final String message) {
            try {
                SVGAbstractTranscoder.this.handler.warning(new TranscoderException(message));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        
        @Override
        public float getPixelUnitToMillimeter() {
            final Object obj = SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER);
            if (obj != null) {
                return (float)obj;
            }
            return super.getPixelUnitToMillimeter();
        }
        
        @Override
        public String getLanguages() {
            if (SVGAbstractTranscoder.this.hints.containsKey(SVGAbstractTranscoder.KEY_LANGUAGE)) {
                return (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_LANGUAGE);
            }
            return super.getLanguages();
        }
        
        @Override
        public String getMedia() {
            final String s = (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_MEDIA);
            if (s != null) {
                return s;
            }
            return super.getMedia();
        }
        
        @Override
        public String getDefaultFontFamily() {
            final String s = (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_DEFAULT_FONT_FAMILY);
            if (s != null) {
                return s;
            }
            return super.getDefaultFontFamily();
        }
        
        @Override
        public String getAlternateStyleSheet() {
            final String s = (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_ALTERNATE_STYLESHEET);
            if (s != null) {
                return s;
            }
            return super.getAlternateStyleSheet();
        }
        
        @Override
        public String getUserStyleSheetURI() {
            final String s = (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_USER_STYLESHEET_URI);
            if (s != null) {
                return s;
            }
            return super.getUserStyleSheetURI();
        }
        
        @Override
        public String getXMLParserClassName() {
            final String s = (String)SVGAbstractTranscoder.this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_CLASSNAME);
            if (s != null) {
                return s;
            }
            return super.getXMLParserClassName();
        }
        
        @Override
        public boolean isXMLParserValidating() {
            final Boolean b = (Boolean)SVGAbstractTranscoder.this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING);
            if (b != null) {
                return b;
            }
            return super.isXMLParserValidating();
        }
        
        @Override
        public ScriptSecurity getScriptSecurity(final String scriptType, final ParsedURL scriptPURL, final ParsedURL docPURL) {
            if (this.scripts == null) {
                this.computeAllowedScripts();
            }
            if (!this.scripts.contains(scriptType)) {
                return new NoLoadScriptSecurity(scriptType);
            }
            boolean constrainOrigin = true;
            if (SVGAbstractTranscoder.this.hints.containsKey(SVGAbstractTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN)) {
                constrainOrigin = (boolean)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN);
            }
            if (constrainOrigin) {
                return new DefaultScriptSecurity(scriptType, scriptPURL, docPURL);
            }
            return new RelaxedScriptSecurity(scriptType, scriptPURL, docPURL);
        }
        
        protected void computeAllowedScripts() {
            this.scripts = new LinkedList();
            if (!SVGAbstractTranscoder.this.hints.containsKey(SVGAbstractTranscoder.KEY_ALLOWED_SCRIPT_TYPES)) {
                return;
            }
            final String allowedScripts = (String)SVGAbstractTranscoder.this.hints.get(SVGAbstractTranscoder.KEY_ALLOWED_SCRIPT_TYPES);
            final StringTokenizer st = new StringTokenizer(allowedScripts, ",");
            while (st.hasMoreTokens()) {
                this.scripts.add(st.nextToken());
            }
        }
    }
}
