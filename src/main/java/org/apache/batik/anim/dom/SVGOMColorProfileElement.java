// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGColorProfileElement;

public class SVGOMColorProfileElement extends SVGOMURIReferenceElement implements SVGColorProfileElement
{
    protected static final AttributeInitializer attributeInitializer;
    
    protected SVGOMColorProfileElement() {
    }
    
    public SVGOMColorProfileElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "color-profile";
    }
    
    @Override
    public String getLocal() {
        return this.getAttributeNS(null, "local");
    }
    
    @Override
    public void setLocal(final String local) throws DOMException {
        this.setAttributeNS(null, "local", local);
    }
    
    @Override
    public String getName() {
        return this.getAttributeNS(null, "name");
    }
    
    @Override
    public void setName(final String name) throws DOMException {
        this.setAttributeNS(null, "name", name);
    }
    
    @Override
    public short getRenderingIntent() {
        final Attr attr = this.getAttributeNodeNS(null, "rendering-intent");
        if (attr == null) {
            return 1;
        }
        final String val = attr.getValue();
        switch (val.length()) {
            case 4: {
                if (val.equals("auto")) {
                    return 1;
                }
                break;
            }
            case 10: {
                if (val.equals("perceptual")) {
                    return 2;
                }
                if (val.equals("saturate")) {
                    return 4;
                }
                break;
            }
            case 21: {
                if (val.equals("absolute-colorimetric")) {
                    return 5;
                }
                if (val.equals("relative-colorimetric")) {
                    return 3;
                }
                break;
            }
        }
        return 0;
    }
    
    @Override
    public void setRenderingIntent(final short renderingIntent) throws DOMException {
        switch (renderingIntent) {
            case 1: {
                this.setAttributeNS(null, "rendering-intent", "auto");
                break;
            }
            case 2: {
                this.setAttributeNS(null, "rendering-intent", "perceptual");
                break;
            }
            case 3: {
                this.setAttributeNS(null, "rendering-intent", "relative-colorimetric");
                break;
            }
            case 4: {
                this.setAttributeNS(null, "rendering-intent", "saturate");
                break;
            }
            case 5: {
                this.setAttributeNS(null, "rendering-intent", "absolute-colorimetric");
                break;
            }
        }
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMColorProfileElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMColorProfileElement();
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(5)).addAttribute(null, null, "rendering-intent", "auto");
        SVGOMColorProfileElement.attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        SVGOMColorProfileElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        SVGOMColorProfileElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        SVGOMColorProfileElement.attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}
