// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGGlyphRefElement;

public class SVGOMGlyphRefElement extends SVGStylableElement implements SVGGlyphRefElement
{
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedString href;
    
    protected SVGOMGlyphRefElement() {
    }
    
    public SVGOMGlyphRefElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
    }
    
    @Override
    public String getLocalName() {
        return "glyphRef";
    }
    
    @Override
    public SVGAnimatedString getHref() {
        return this.href;
    }
    
    @Override
    public String getGlyphRef() {
        return this.getAttributeNS(null, "glyphRef");
    }
    
    @Override
    public void setGlyphRef(final String glyphRef) throws DOMException {
        this.setAttributeNS(null, "glyphRef", glyphRef);
    }
    
    @Override
    public String getFormat() {
        return this.getAttributeNS(null, "format");
    }
    
    @Override
    public void setFormat(final String format) throws DOMException {
        this.setAttributeNS(null, "format", format);
    }
    
    @Override
    public float getX() {
        return Float.parseFloat(this.getAttributeNS(null, "x"));
    }
    
    @Override
    public void setX(final float x) throws DOMException {
        this.setAttributeNS(null, "x", String.valueOf(x));
    }
    
    @Override
    public float getY() {
        return Float.parseFloat(this.getAttributeNS(null, "y"));
    }
    
    @Override
    public void setY(final float y) throws DOMException {
        this.setAttributeNS(null, "y", String.valueOf(y));
    }
    
    @Override
    public float getDx() {
        return Float.parseFloat(this.getAttributeNS(null, "dx"));
    }
    
    @Override
    public void setDx(final float dx) throws DOMException {
        this.setAttributeNS(null, "dx", String.valueOf(dx));
    }
    
    @Override
    public float getDy() {
        return Float.parseFloat(this.getAttributeNS(null, "dy"));
    }
    
    @Override
    public void setDy(final float dy) throws DOMException {
        this.setAttributeNS(null, "dy", String.valueOf(dy));
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMGlyphRefElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMGlyphRefElement();
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(4)).addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMGlyphRefElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMGlyphRefElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        SVGOMGlyphRefElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}
