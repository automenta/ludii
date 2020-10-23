// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.CleanerThread;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.io.IOException;
import org.apache.batik.dom.util.DocumentDescriptor;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.Document;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import java.util.HashMap;
import org.apache.batik.dom.svg.SVGDocumentFactory;

public class DocumentLoader
{
    protected SVGDocumentFactory documentFactory;
    protected HashMap cacheMap;
    protected UserAgent userAgent;
    
    protected DocumentLoader() {
        this.cacheMap = new HashMap();
    }
    
    public DocumentLoader(final UserAgent userAgent) {
        this.cacheMap = new HashMap();
        this.userAgent = userAgent;
        (this.documentFactory = new SAXSVGDocumentFactory(userAgent.getXMLParserClassName(), true)).setValidating(userAgent.isXMLParserValidating());
    }
    
    public Document checkCache(String uri) {
        int n = uri.lastIndexOf(47);
        if (n == -1) {
            n = 0;
        }
        n = uri.indexOf(35, n);
        if (n != -1) {
            uri = uri.substring(0, n);
        }
        final DocumentState state;
        synchronized (this.cacheMap) {
            state = this.cacheMap.get(uri);
        }
        if (state != null) {
            return state.getDocument();
        }
        return null;
    }
    
    public Document loadDocument(final String uri) throws IOException {
        final Document ret = this.checkCache(uri);
        if (ret != null) {
            return ret;
        }
        final SVGDocument document = this.documentFactory.createSVGDocument(uri);
        final DocumentDescriptor desc = this.documentFactory.getDocumentDescriptor();
        final DocumentState state = new DocumentState(uri, document, desc);
        synchronized (this.cacheMap) {
            this.cacheMap.put(uri, state);
        }
        return state.getDocument();
    }
    
    public Document loadDocument(final String uri, final InputStream is) throws IOException {
        final Document ret = this.checkCache(uri);
        if (ret != null) {
            return ret;
        }
        final SVGDocument document = this.documentFactory.createSVGDocument(uri, is);
        final DocumentDescriptor desc = this.documentFactory.getDocumentDescriptor();
        final DocumentState state = new DocumentState(uri, document, desc);
        synchronized (this.cacheMap) {
            this.cacheMap.put(uri, state);
        }
        return state.getDocument();
    }
    
    public UserAgent getUserAgent() {
        return this.userAgent;
    }
    
    public void dispose() {
        synchronized (this.cacheMap) {
            this.cacheMap.clear();
        }
    }
    
    public int getLineNumber(final Element e) {
        final String uri = ((SVGDocument)e.getOwnerDocument()).getURL();
        final DocumentState state;
        synchronized (this.cacheMap) {
            state = this.cacheMap.get(uri);
        }
        if (state == null) {
            return -1;
        }
        return state.desc.getLocationLine(e);
    }
    
    private class DocumentState extends CleanerThread.SoftReferenceCleared
    {
        private String uri;
        private DocumentDescriptor desc;
        
        public DocumentState(final String uri, final Document document, final DocumentDescriptor desc) {
            super(document);
            this.uri = uri;
            this.desc = desc;
        }
        
        @Override
        public void cleared() {
            synchronized (DocumentLoader.this.cacheMap) {
                DocumentLoader.this.cacheMap.remove(this.uri);
            }
        }
        
        public DocumentDescriptor getDocumentDescriptor() {
            return this.desc;
        }
        
        public String getURI() {
            return this.uri;
        }
        
        public Document getDocument() {
            return this.get();
        }
    }
}
