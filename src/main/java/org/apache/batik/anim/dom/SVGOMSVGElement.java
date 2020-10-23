// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGTestsSupport;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.Node;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.svg.SVGTransform;
import org.apache.batik.dom.svg.AbstractSVGMatrix;
import org.apache.batik.dom.svg.SVGOMAngle;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGNumber;
import java.util.List;
import org.apache.batik.dom.util.ListNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGElement;
import org.apache.batik.dom.svg.SVGSVGContext;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGViewSpec;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMRect;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGOMSVGElement extends SVGStylableElement implements SVGSVGElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected SVGOMAnimatedRect viewBox;
    
    protected SVGOMSVGElement() {
    }
    
    public SVGOMSVGElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "100%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "100%", (short)1, true);
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
        this.viewBox = this.createLiveAnimatedRect(null, "viewBox", null);
    }
    
    @Override
    public String getLocalName() {
        return "svg";
    }
    
    @Override
    public SVGAnimatedLength getX() {
        return this.x;
    }
    
    @Override
    public SVGAnimatedLength getY() {
        return this.y;
    }
    
    @Override
    public SVGAnimatedLength getWidth() {
        return this.width;
    }
    
    @Override
    public SVGAnimatedLength getHeight() {
        return this.height;
    }
    
    @Override
    public String getContentScriptType() {
        return this.getAttributeNS(null, "contentScriptType");
    }
    
    @Override
    public void setContentScriptType(final String type) {
        this.setAttributeNS(null, "contentScriptType", type);
    }
    
    @Override
    public String getContentStyleType() {
        return this.getAttributeNS(null, "contentStyleType");
    }
    
    @Override
    public void setContentStyleType(final String type) {
        this.setAttributeNS(null, "contentStyleType", type);
    }
    
    @Override
    public SVGRect getViewport() {
        final SVGContext ctx = this.getSVGContext();
        return new SVGOMRect(0.0f, 0.0f, ctx.getViewportWidth(), ctx.getViewportHeight());
    }
    
    @Override
    public float getPixelUnitToMillimeterX() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }
    
    @Override
    public float getPixelUnitToMillimeterY() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }
    
    @Override
    public float getScreenPixelToMillimeterX() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }
    
    @Override
    public float getScreenPixelToMillimeterY() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }
    
    @Override
    public boolean getUseCurrentView() {
        throw new UnsupportedOperationException("SVGSVGElement.getUseCurrentView is not implemented");
    }
    
    @Override
    public void setUseCurrentView(final boolean useCurrentView) throws DOMException {
        throw new UnsupportedOperationException("SVGSVGElement.setUseCurrentView is not implemented");
    }
    
    @Override
    public SVGViewSpec getCurrentView() {
        throw new UnsupportedOperationException("SVGSVGElement.getCurrentView is not implemented");
    }
    
    @Override
    public float getCurrentScale() {
        final AffineTransform scrnTrans = this.getSVGContext().getScreenTransform();
        if (scrnTrans != null) {
            return (float)Math.sqrt(scrnTrans.getDeterminant());
        }
        return 1.0f;
    }
    
    @Override
    public void setCurrentScale(final float currentScale) throws DOMException {
        final SVGContext context = this.getSVGContext();
        AffineTransform scrnTrans = context.getScreenTransform();
        float scale = 1.0f;
        if (scrnTrans != null) {
            scale = (float)Math.sqrt(scrnTrans.getDeterminant());
        }
        final float delta = currentScale / scale;
        scrnTrans = new AffineTransform(scrnTrans.getScaleX() * delta, scrnTrans.getShearY() * delta, scrnTrans.getShearX() * delta, scrnTrans.getScaleY() * delta, scrnTrans.getTranslateX(), scrnTrans.getTranslateY());
        context.setScreenTransform(scrnTrans);
    }
    
    @Override
    public SVGPoint getCurrentTranslate() {
        return new SVGPoint() {
            protected AffineTransform getScreenTransform() {
                final SVGContext context = SVGOMSVGElement.this.getSVGContext();
                return context.getScreenTransform();
            }
            
            @Override
            public float getX() {
                final AffineTransform scrnTrans = this.getScreenTransform();
                return (float)scrnTrans.getTranslateX();
            }
            
            @Override
            public float getY() {
                final AffineTransform scrnTrans = this.getScreenTransform();
                return (float)scrnTrans.getTranslateY();
            }
            
            @Override
            public void setX(final float newX) {
                final SVGContext context = SVGOMSVGElement.this.getSVGContext();
                AffineTransform scrnTrans = context.getScreenTransform();
                scrnTrans = new AffineTransform(scrnTrans.getScaleX(), scrnTrans.getShearY(), scrnTrans.getShearX(), scrnTrans.getScaleY(), newX, scrnTrans.getTranslateY());
                context.setScreenTransform(scrnTrans);
            }
            
            @Override
            public void setY(final float newY) {
                final SVGContext context = SVGOMSVGElement.this.getSVGContext();
                AffineTransform scrnTrans = context.getScreenTransform();
                scrnTrans = new AffineTransform(scrnTrans.getScaleX(), scrnTrans.getShearY(), scrnTrans.getShearX(), scrnTrans.getScaleY(), scrnTrans.getTranslateX(), newY);
                context.setScreenTransform(scrnTrans);
            }
            
            @Override
            public SVGPoint matrixTransform(final SVGMatrix mat) {
                final AffineTransform scrnTrans = this.getScreenTransform();
                final float x = (float)scrnTrans.getTranslateX();
                final float y = (float)scrnTrans.getTranslateY();
                final float newX = mat.getA() * x + mat.getC() * y + mat.getE();
                final float newY = mat.getB() * x + mat.getD() * y + mat.getF();
                return new SVGOMPoint(newX, newY);
            }
        };
    }
    
    @Override
    public int suspendRedraw(int max_wait_milliseconds) {
        if (max_wait_milliseconds > 60000) {
            max_wait_milliseconds = 60000;
        }
        else if (max_wait_milliseconds < 0) {
            max_wait_milliseconds = 0;
        }
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.suspendRedraw(max_wait_milliseconds);
    }
    
    @Override
    public void unsuspendRedraw(final int suspend_handle_id) throws DOMException {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        if (!ctx.unsuspendRedraw(suspend_handle_id)) {
            throw this.createDOMException((short)8, "invalid.suspend.handle", new Object[] { suspend_handle_id });
        }
    }
    
    @Override
    public void unsuspendRedrawAll() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.unsuspendRedrawAll();
    }
    
    @Override
    public void forceRedraw() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.forceRedraw();
    }
    
    @Override
    public void pauseAnimations() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.pauseAnimations();
    }
    
    @Override
    public void unpauseAnimations() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.unpauseAnimations();
    }
    
    @Override
    public boolean animationsPaused() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.animationsPaused();
    }
    
    @Override
    public float getCurrentTime() {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.getCurrentTime();
    }
    
    @Override
    public void setCurrentTime(final float seconds) {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.setCurrentTime(seconds);
    }
    
    @Override
    public NodeList getIntersectionList(final SVGRect rect, final SVGElement referenceElement) {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        final List list = ctx.getIntersectionList(rect, referenceElement);
        return new ListNodeList(list);
    }
    
    @Override
    public NodeList getEnclosureList(final SVGRect rect, final SVGElement referenceElement) {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        final List list = ctx.getEnclosureList(rect, referenceElement);
        return new ListNodeList(list);
    }
    
    @Override
    public boolean checkIntersection(final SVGElement element, final SVGRect rect) {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.checkIntersection(element, rect);
    }
    
    @Override
    public boolean checkEnclosure(final SVGElement element, final SVGRect rect) {
        final SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.checkEnclosure(element, rect);
    }
    
    @Override
    public void deselectAll() {
        ((SVGSVGContext)this.getSVGContext()).deselectAll();
    }
    
    @Override
    public SVGNumber createSVGNumber() {
        return new SVGNumber() {
            protected float value;
            
            @Override
            public float getValue() {
                return this.value;
            }
            
            @Override
            public void setValue(final float f) {
                this.value = f;
            }
        };
    }
    
    @Override
    public SVGLength createSVGLength() {
        return new SVGOMLength(this);
    }
    
    @Override
    public SVGAngle createSVGAngle() {
        return new SVGOMAngle();
    }
    
    @Override
    public SVGPoint createSVGPoint() {
        return new SVGOMPoint(0.0f, 0.0f);
    }
    
    @Override
    public SVGMatrix createSVGMatrix() {
        return new AbstractSVGMatrix() {
            protected AffineTransform at = new AffineTransform();
            
            @Override
            protected AffineTransform getAffineTransform() {
                return this.at;
            }
        };
    }
    
    @Override
    public SVGRect createSVGRect() {
        return new SVGOMRect(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public SVGTransform createSVGTransform() {
        final SVGOMTransform ret = new SVGOMTransform();
        ret.setType((short)1);
        return ret;
    }
    
    @Override
    public SVGTransform createSVGTransformFromMatrix(final SVGMatrix matrix) {
        final SVGOMTransform tr = new SVGOMTransform();
        tr.setMatrix(matrix);
        return tr;
    }
    
    @Override
    public Element getElementById(final String elementId) {
        return this.ownerDocument.getChildElementById(this, elementId);
    }
    
    @Override
    public SVGElement getNearestViewportElement() {
        return SVGLocatableSupport.getNearestViewportElement(this);
    }
    
    @Override
    public SVGElement getFarthestViewportElement() {
        return SVGLocatableSupport.getFarthestViewportElement(this);
    }
    
    @Override
    public SVGRect getBBox() {
        return SVGLocatableSupport.getBBox(this);
    }
    
    @Override
    public SVGMatrix getCTM() {
        return SVGLocatableSupport.getCTM(this);
    }
    
    @Override
    public SVGMatrix getScreenCTM() {
        return SVGLocatableSupport.getScreenCTM(this);
    }
    
    @Override
    public SVGMatrix getTransformToElement(final SVGElement element) throws SVGException {
        return SVGLocatableSupport.getTransformToElement(this, element);
    }
    
    @Override
    public DocumentView getDocument() {
        return (DocumentView)this.getOwnerDocument();
    }
    
    @Override
    public CSSStyleDeclaration getComputedStyle(final Element elt, final String pseudoElt) {
        final AbstractView av = ((DocumentView)this.getOwnerDocument()).getDefaultView();
        return ((ViewCSS)av).getComputedStyle(elt, pseudoElt);
    }
    
    @Override
    public Event createEvent(final String eventType) throws DOMException {
        return ((DocumentEvent)this.getOwnerDocument()).createEvent(eventType);
    }
    
    public boolean canDispatch(final String namespaceURI, final String type) throws DOMException {
        final AbstractDocument doc = (AbstractDocument)this.getOwnerDocument();
        return doc.canDispatch(namespaceURI, type);
    }
    
    @Override
    public StyleSheetList getStyleSheets() {
        return ((DocumentStyle)this.getOwnerDocument()).getStyleSheets();
    }
    
    @Override
    public CSSStyleDeclaration getOverrideStyle(final Element elt, final String pseudoElt) {
        return ((DocumentCSS)this.getOwnerDocument()).getOverrideStyle(elt, pseudoElt);
    }
    
    @Override
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }
    
    @Override
    public void setXMLlang(final String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }
    
    @Override
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }
    
    @Override
    public void setXMLspace(final String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }
    
    @Override
    public short getZoomAndPan() {
        return SVGZoomAndPanSupport.getZoomAndPan(this);
    }
    
    @Override
    public void setZoomAndPan(final short val) {
        SVGZoomAndPanSupport.setZoomAndPan(this, val);
    }
    
    @Override
    public SVGAnimatedRect getViewBox() {
        return this.viewBox;
    }
    
    @Override
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }
    
    @Override
    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }
    
    @Override
    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures(this);
    }
    
    @Override
    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions(this);
    }
    
    @Override
    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage(this);
    }
    
    @Override
    public boolean hasExtension(final String extension) {
        return SVGTestsSupport.hasExtension(this, extension);
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMSVGElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMSVGElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMSVGElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, "x", new TraitInformation(true, 3, (short)1));
        t.put(null, "y", new TraitInformation(true, 3, (short)2));
        t.put(null, "width", new TraitInformation(true, 3, (short)1));
        t.put(null, "height", new TraitInformation(true, 3, (short)2));
        t.put(null, "preserveAspectRatio", new TraitInformation(true, 32));
        t.put(null, "viewBox", new TraitInformation(true, 50));
        t.put(null, "externalResourcesRequired", new TraitInformation(true, 49));
        SVGOMSVGElement.xmlTraitInformation = t;
        (attributeInitializer = new AttributeInitializer(7)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns", "http://www.w3.org/2000/svg");
        SVGOMSVGElement.attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xlink", "http://www.w3.org/1999/xlink");
        SVGOMSVGElement.attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        SVGOMSVGElement.attributeInitializer.addAttribute(null, null, "zoomAndPan", "magnify");
        SVGOMSVGElement.attributeInitializer.addAttribute(null, null, "version", "1.0");
        SVGOMSVGElement.attributeInitializer.addAttribute(null, null, "contentScriptType", "text/ecmascript");
        SVGOMSVGElement.attributeInitializer.addAttribute(null, null, "contentStyleType", "text/css");
    }
}
