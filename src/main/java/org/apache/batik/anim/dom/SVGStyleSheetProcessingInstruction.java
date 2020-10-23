// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import java.util.HashMap;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.dom.StyleSheetProcessingInstruction;

public class SVGStyleSheetProcessingInstruction extends StyleSheetProcessingInstruction implements CSSStyleSheetNode
{
    protected StyleSheet styleSheet;
    
    protected SVGStyleSheetProcessingInstruction() {
    }
    
    public SVGStyleSheetProcessingInstruction(final String data, final AbstractDocument owner, final StyleSheetFactory f) {
        super(data, owner, f);
    }
    
    public String getStyleSheetURI() {
        final SVGOMDocument svgDoc = (SVGOMDocument)this.getOwnerDocument();
        final ParsedURL url = svgDoc.getParsedURL();
        final String href = this.getPseudoAttributes().get("href");
        if (url != null) {
            return new ParsedURL(url, href).toString();
        }
        return href;
    }
    
    @Override
    public StyleSheet getCSSStyleSheet() {
        if (this.styleSheet == null) {
            final HashMap<String, String> attrs = this.getPseudoAttributes();
            final String type = attrs.get("type");
            if ("text/css".equals(type)) {
                final String title = attrs.get("title");
                final String media = attrs.get("media");
                final String href = attrs.get("href");
                final String alternate = attrs.get("alternate");
                final SVGOMDocument doc = (SVGOMDocument)this.getOwnerDocument();
                final ParsedURL durl = doc.getParsedURL();
                final ParsedURL burl = new ParsedURL(durl, href);
                final CSSEngine e = doc.getCSSEngine();
                (this.styleSheet = e.parseStyleSheet(burl, media)).setAlternate("yes".equals(alternate));
                this.styleSheet.setTitle(title);
            }
        }
        return this.styleSheet;
    }
    
    @Override
    public void setData(final String data) throws DOMException {
        super.setData(data);
        this.styleSheet = null;
    }
    
    @Override
    protected Node newNode() {
        return new SVGStyleSheetProcessingInstruction();
    }
}
