// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import org.w3c.dom.Document;
import java.io.Writer;
import java.io.OutputStream;
import org.xml.sax.XMLFilter;

public class TranscoderOutput
{
    protected XMLFilter xmlFilter;
    protected OutputStream ostream;
    protected Writer writer;
    protected Document document;
    protected String uri;
    
    public TranscoderOutput() {
    }
    
    public TranscoderOutput(final XMLFilter xmlFilter) {
        this.xmlFilter = xmlFilter;
    }
    
    public TranscoderOutput(final OutputStream ostream) {
        this.ostream = ostream;
    }
    
    public TranscoderOutput(final Writer writer) {
        this.writer = writer;
    }
    
    public TranscoderOutput(final Document document) {
        this.document = document;
    }
    
    public TranscoderOutput(final String uri) {
        this.uri = uri;
    }
    
    public void setXMLFilter(final XMLFilter xmlFilter) {
        this.xmlFilter = xmlFilter;
    }
    
    public XMLFilter getXMLFilter() {
        return this.xmlFilter;
    }
    
    public void setOutputStream(final OutputStream ostream) {
        this.ostream = ostream;
    }
    
    public OutputStream getOutputStream() {
        return this.ostream;
    }
    
    public void setWriter(final Writer writer) {
        this.writer = writer;
    }
    
    public Writer getWriter() {
        return this.writer;
    }
    
    public void setDocument(final Document document) {
        this.document = document;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public void setURI(final String uri) {
        this.uri = uri;
    }
    
    public String getURI() {
        return this.uri;
    }
}
