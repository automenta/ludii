// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.events.Event;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.Node;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.svg.SVGStyleElement;
import org.apache.batik.css.engine.CSSStyleSheetNode;

public class SVGOMStyleElement extends SVGOMElement implements CSSStyleSheetNode, SVGStyleElement, LinkStyle
{
    protected static final AttributeInitializer attributeInitializer;
    protected transient StyleSheet sheet;
    protected transient org.apache.batik.css.engine.StyleSheet styleSheet;
    protected transient EventListener domCharacterDataModifiedListener;
    
    protected SVGOMStyleElement() {
        this.domCharacterDataModifiedListener = new DOMCharacterDataModifiedListener();
    }
    
    public SVGOMStyleElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.domCharacterDataModifiedListener = new DOMCharacterDataModifiedListener();
    }
    
    @Override
    public String getLocalName() {
        return "style";
    }
    
    @Override
    public org.apache.batik.css.engine.StyleSheet getCSSStyleSheet() {
        if (this.styleSheet == null && this.getType().equals("text/css")) {
            final SVGOMDocument doc = (SVGOMDocument)this.getOwnerDocument();
            final CSSEngine e = doc.getCSSEngine();
            String text = "";
            Node n = this.getFirstChild();
            if (n != null) {
                final StringBuffer sb = new StringBuffer();
                while (n != null) {
                    if (n.getNodeType() == 4 || n.getNodeType() == 3) {
                        sb.append(n.getNodeValue());
                    }
                    n = n.getNextSibling();
                }
                text = sb.toString();
            }
            ParsedURL burl = null;
            final String bu = this.getBaseURI();
            if (bu != null) {
                burl = new ParsedURL(bu);
            }
            final String media = this.getAttributeNS(null, "media");
            this.styleSheet = e.parseStyleSheet(text, burl, media);
            this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedListener, false, null);
        }
        return this.styleSheet;
    }
    
    @Override
    public StyleSheet getSheet() {
        throw new UnsupportedOperationException("LinkStyle.getSheet() is not implemented");
    }
    
    @Override
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }
    
    @Override
    public void setXMLspace(final String space) throws DOMException {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }
    
    @Override
    public String getType() {
        if (this.hasAttributeNS(null, "type")) {
            return this.getAttributeNS(null, "type");
        }
        return "text/css";
    }
    
    @Override
    public void setType(final String type) throws DOMException {
        this.setAttributeNS(null, "type", type);
    }
    
    @Override
    public String getMedia() {
        return this.getAttribute("media");
    }
    
    @Override
    public void setMedia(final String media) throws DOMException {
        this.setAttribute("media", media);
    }
    
    @Override
    public String getTitle() {
        return this.getAttribute("title");
    }
    
    @Override
    public void setTitle(final String title) throws DOMException {
        this.setAttribute("title", title);
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMStyleElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMStyleElement();
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(1)).addAttribute("http://www.w3.org/XML/1998/namespace", "xml", "space", "preserve");
    }
    
    protected class DOMCharacterDataModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            SVGOMStyleElement.this.styleSheet = null;
        }
    }
}
