// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Document;
import org.apache.batik.util.ParsedURL;
import java.io.IOException;
import java.net.MalformedURLException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.anim.dom.SVGOMDocument;

public class URIResolver
{
    protected SVGOMDocument document;
    protected String documentURI;
    protected DocumentLoader documentLoader;
    
    public URIResolver(final SVGDocument doc, final DocumentLoader dl) {
        this.document = (SVGOMDocument)doc;
        this.documentLoader = dl;
    }
    
    public Element getElement(final String uri, final Element ref) throws MalformedURLException, IOException {
        final Node n = this.getNode(uri, ref);
        if (n == null) {
            return null;
        }
        if (n.getNodeType() == 9) {
            throw new IllegalArgumentException();
        }
        return (Element)n;
    }
    
    public Node getNode(final String uri, final Element ref) throws MalformedURLException, IOException, SecurityException {
        final String baseURI = this.getRefererBaseURI(ref);
        if (baseURI == null && uri.charAt(0) == '#') {
            return this.getNodeByFragment(uri.substring(1), ref);
        }
        final ParsedURL purl = new ParsedURL(baseURI, uri);
        if (this.documentURI == null) {
            this.documentURI = this.document.getURL();
        }
        final String frag = purl.getRef();
        if (frag != null && this.documentURI != null) {
            final ParsedURL pDocURL = new ParsedURL(this.documentURI);
            if (pDocURL.sameFile(purl)) {
                return this.document.getElementById(frag);
            }
        }
        ParsedURL pDocURL = null;
        if (this.documentURI != null) {
            pDocURL = new ParsedURL(this.documentURI);
        }
        final UserAgent userAgent = this.documentLoader.getUserAgent();
        userAgent.checkLoadExternalResource(purl, pDocURL);
        String purlStr = purl.toString();
        if (frag != null) {
            purlStr = purlStr.substring(0, purlStr.length() - (frag.length() + 1));
        }
        final Document doc = this.documentLoader.loadDocument(purlStr);
        if (frag != null) {
            return doc.getElementById(frag);
        }
        return doc;
    }
    
    protected String getRefererBaseURI(final Element ref) {
        return ref.getBaseURI();
    }
    
    protected Node getNodeByFragment(final String frag, final Element ref) {
        return ref.getOwnerDocument().getElementById(frag);
    }
}
