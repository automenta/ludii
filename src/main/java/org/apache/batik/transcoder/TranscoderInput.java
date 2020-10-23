// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import org.w3c.dom.Document;
import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.XMLReader;

public class TranscoderInput
{
    protected XMLReader xmlReader;
    protected InputStream istream;
    protected Reader reader;
    protected Document document;
    protected String uri;
    
    public TranscoderInput() {
    }
    
    public TranscoderInput(final XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }
    
    public TranscoderInput(final InputStream istream) {
        this.istream = istream;
    }
    
    public TranscoderInput(final Reader reader) {
        this.reader = reader;
    }
    
    public TranscoderInput(final Document document) {
        this.document = document;
    }
    
    public TranscoderInput(final String uri) {
        this.uri = uri;
    }
    
    public void setXMLReader(final XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }
    
    public XMLReader getXMLReader() {
        return this.xmlReader;
    }
    
    public void setInputStream(final InputStream istream) {
        this.istream = istream;
    }
    
    public InputStream getInputStream() {
        return this.istream;
    }
    
    public void setReader(final Reader reader) {
        this.reader = reader;
    }
    
    public Reader getReader() {
        return this.reader;
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
