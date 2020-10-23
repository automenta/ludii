// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.xml.sax.SAXNotSupportedException;
import java.util.Iterator;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.batik.util.HaltingThread;
import org.xml.sax.Attributes;
import java.util.LinkedList;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.InterruptedIOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.w3c.dom.DocumentType;
import org.xml.sax.Locator;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentFactory extends DefaultHandler implements LexicalHandler, DocumentFactory
{
    protected DOMImplementation implementation;
    protected String parserClassName;
    protected XMLReader parser;
    protected Document document;
    protected DocumentDescriptor documentDescriptor;
    protected boolean createDocumentDescriptor;
    protected Node currentNode;
    protected Locator locator;
    protected StringBuffer stringBuffer;
    protected DocumentType doctype;
    protected boolean stringContent;
    protected boolean inDTD;
    protected boolean inCDATA;
    protected boolean inProlog;
    protected boolean isValidating;
    protected boolean isStandalone;
    protected String xmlVersion;
    protected HashTableStack namespaces;
    protected ErrorHandler errorHandler;
    protected List preInfo;
    static SAXParserFactory saxFactory;
    
    public SAXDocumentFactory(final DOMImplementation impl, final String parser) {
        this.stringBuffer = new StringBuffer();
        this.implementation = impl;
        this.parserClassName = parser;
    }
    
    public SAXDocumentFactory(final DOMImplementation impl, final String parser, final boolean dd) {
        this.stringBuffer = new StringBuffer();
        this.implementation = impl;
        this.parserClassName = parser;
        this.createDocumentDescriptor = dd;
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri) throws IOException {
        return this.createDocument(ns, root, uri, new InputSource(uri));
    }
    
    public Document createDocument(final String uri) throws IOException {
        return this.createDocument(new InputSource(uri));
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri, final InputStream is) throws IOException {
        final InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return this.createDocument(ns, root, uri, inp);
    }
    
    public Document createDocument(final String uri, final InputStream is) throws IOException {
        final InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return this.createDocument(inp);
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri, final Reader r) throws IOException {
        final InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return this.createDocument(ns, root, uri, inp);
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri, final XMLReader r) throws IOException {
        r.setContentHandler(this);
        r.setDTDHandler(this);
        r.setEntityResolver(this);
        try {
            r.parse(uri);
        }
        catch (SAXException e) {
            final Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException)ex;
            }
            throw new SAXIOException(e);
        }
        this.currentNode = null;
        final Document ret = this.document;
        this.document = null;
        this.doctype = null;
        return ret;
    }
    
    public Document createDocument(final String uri, final Reader r) throws IOException {
        final InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return this.createDocument(inp);
    }
    
    protected Document createDocument(final String ns, final String root, final String uri, final InputSource is) throws IOException {
        final Document ret = this.createDocument(is);
        final Element docElem = ret.getDocumentElement();
        String lname = root;
        String nsURI = ns;
        if (ns == null) {
            final int idx = lname.indexOf(58);
            final String nsp = (idx == -1 || idx == lname.length() - 1) ? "" : lname.substring(0, idx);
            nsURI = this.namespaces.get(nsp);
            if (idx != -1 && idx != lname.length() - 1) {
                lname = lname.substring(idx + 1);
            }
        }
        final String docElemNS = docElem.getNamespaceURI();
        if (docElemNS != nsURI && (docElemNS == null || !docElemNS.equals(nsURI))) {
            throw new IOException("Root element namespace does not match that requested:\nRequested: " + nsURI + "\n" + "Found: " + docElemNS);
        }
        if (docElemNS != null) {
            if (!docElem.getLocalName().equals(lname)) {
                throw new IOException("Root element does not match that requested:\nRequested: " + lname + "\n" + "Found: " + docElem.getLocalName());
            }
        }
        else if (!docElem.getNodeName().equals(lname)) {
            throw new IOException("Root element does not match that requested:\nRequested: " + lname + "\n" + "Found: " + docElem.getNodeName());
        }
        return ret;
    }
    
    protected Document createDocument(final InputSource is) throws IOException {
        try {
            if (this.parserClassName != null) {
                this.parser = XMLReaderFactory.createXMLReader(this.parserClassName);
            }
            else {
                SAXParser saxParser;
                try {
                    saxParser = SAXDocumentFactory.saxFactory.newSAXParser();
                }
                catch (ParserConfigurationException pce) {
                    throw new IOException("Could not create SAXParser: " + pce.getMessage());
                }
                this.parser = saxParser.getXMLReader();
            }
            this.parser.setContentHandler(this);
            this.parser.setDTDHandler(this);
            this.parser.setEntityResolver(this);
            this.parser.setErrorHandler((this.errorHandler == null) ? this : this.errorHandler);
            this.parser.setFeature("http://xml.org/sax/features/namespaces", true);
            this.parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            this.parser.setFeature("http://xml.org/sax/features/validation", this.isValidating);
            this.parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            this.parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            this.parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            this.parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            this.parser.parse(is);
        }
        catch (SAXException e) {
            final Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException)ex;
            }
            throw new SAXIOException(e);
        }
        this.currentNode = null;
        final Document ret = this.document;
        this.document = null;
        this.doctype = null;
        this.locator = null;
        this.parser = null;
        return ret;
    }
    
    @Override
    public DocumentDescriptor getDocumentDescriptor() {
        return this.documentDescriptor;
    }
    
    @Override
    public void setDocumentLocator(final Locator l) {
        this.locator = l;
    }
    
    @Override
    public void setValidating(final boolean isValidating) {
        this.isValidating = isValidating;
    }
    
    @Override
    public boolean isValidating() {
        return this.isValidating;
    }
    
    public void setErrorHandler(final ErrorHandler eh) {
        this.errorHandler = eh;
    }
    
    public DOMImplementation getDOMImplementation(final String ver) {
        return this.implementation;
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.preInfo = new LinkedList();
        (this.namespaces = new HashTableStack()).put("xml", "http://www.w3.org/XML/1998/namespace");
        this.namespaces.put("xmlns", "http://www.w3.org/2000/xmlns/");
        this.namespaces.put("", null);
        this.inDTD = false;
        this.inCDATA = false;
        this.inProlog = true;
        this.currentNode = null;
        this.document = null;
        this.doctype = null;
        this.isStandalone = false;
        this.xmlVersion = "1.0";
        this.stringBuffer.setLength(0);
        this.stringContent = false;
        if (this.createDocumentDescriptor) {
            this.documentDescriptor = new DocumentDescriptor();
        }
        else {
            this.documentDescriptor = null;
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String rawName, final Attributes attributes) throws SAXException {
        if (HaltingThread.hasBeenHalted()) {
            throw new SAXException(new InterruptedIOException());
        }
        if (this.inProlog) {
            this.inProlog = false;
            if (this.parser != null) {
                try {
                    this.isStandalone = this.parser.getFeature("http://xml.org/sax/features/is-standalone");
                }
                catch (SAXNotRecognizedException ex) {}
                try {
                    this.xmlVersion = (String)this.parser.getProperty("http://xml.org/sax/properties/document-xml-version");
                }
                catch (SAXNotRecognizedException ex2) {}
            }
        }
        final int len = attributes.getLength();
        this.namespaces.push();
        String version = null;
        for (int i = 0; i < len; ++i) {
            final String aname = attributes.getQName(i);
            final int slen = aname.length();
            if (slen >= 5) {
                if (aname.equals("version")) {
                    version = attributes.getValue(i);
                }
                else if (aname.startsWith("xmlns")) {
                    if (slen == 5) {
                        String ns = attributes.getValue(i);
                        if (ns.length() == 0) {
                            ns = null;
                        }
                        this.namespaces.put("", ns);
                    }
                    else if (aname.charAt(5) == ':') {
                        String ns = attributes.getValue(i);
                        if (ns.length() == 0) {
                            ns = null;
                        }
                        this.namespaces.put(aname.substring(6), ns);
                    }
                }
            }
        }
        this.appendStringData();
        int idx = rawName.indexOf(58);
        final String nsp = (idx == -1 || idx == rawName.length() - 1) ? "" : rawName.substring(0, idx);
        String nsURI = this.namespaces.get(nsp);
        Element e;
        if (this.currentNode == null) {
            this.implementation = this.getDOMImplementation(version);
            this.document = this.implementation.createDocument(nsURI, rawName, this.doctype);
            final Iterator j = this.preInfo.iterator();
            e = (Element)(this.currentNode = this.document.getDocumentElement());
            while (j.hasNext()) {
                final PreInfo pi = j.next();
                final Node n = pi.createNode(this.document);
                this.document.insertBefore(n, e);
            }
            this.preInfo = null;
        }
        else {
            e = this.document.createElementNS(nsURI, rawName);
            this.currentNode.appendChild(e);
            this.currentNode = e;
        }
        if (this.createDocumentDescriptor && this.locator != null) {
            this.documentDescriptor.setLocation(e, this.locator.getLineNumber(), this.locator.getColumnNumber());
        }
        for (int k = 0; k < len; ++k) {
            final String aname2 = attributes.getQName(k);
            if (aname2.equals("xmlns")) {
                e.setAttributeNS("http://www.w3.org/2000/xmlns/", aname2, attributes.getValue(k));
            }
            else {
                idx = aname2.indexOf(58);
                nsURI = ((idx == -1) ? null : this.namespaces.get(aname2.substring(0, idx)));
                e.setAttributeNS(nsURI, aname2, attributes.getValue(k));
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String rawName) throws SAXException {
        this.appendStringData();
        if (this.currentNode != null) {
            this.currentNode = this.currentNode.getParentNode();
        }
        this.namespaces.pop();
    }
    
    public void appendStringData() {
        if (!this.stringContent) {
            return;
        }
        final String str = this.stringBuffer.toString();
        this.stringBuffer.setLength(0);
        this.stringContent = false;
        if (this.currentNode == null) {
            if (this.inCDATA) {
                this.preInfo.add(new CDataInfo(str));
            }
            else {
                this.preInfo.add(new TextInfo(str));
            }
        }
        else {
            Node n;
            if (this.inCDATA) {
                n = this.document.createCDATASection(str);
            }
            else {
                n = this.document.createTextNode(str);
            }
            this.currentNode.appendChild(n);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.stringBuffer.append(ch, start, length);
        this.stringContent = true;
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.stringBuffer.append(ch, start, length);
        this.stringContent = true;
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.inDTD) {
            return;
        }
        this.appendStringData();
        if (this.currentNode == null) {
            this.preInfo.add(new ProcessingInstructionInfo(target, data));
        }
        else {
            this.currentNode.appendChild(this.document.createProcessingInstruction(target, data));
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.appendStringData();
        this.doctype = this.implementation.createDocumentType(name, publicId, systemId);
        this.inDTD = true;
    }
    
    @Override
    public void endDTD() throws SAXException {
        this.inDTD = false;
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.appendStringData();
        this.inCDATA = true;
        this.stringContent = true;
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.appendStringData();
        this.inCDATA = false;
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.inDTD) {
            return;
        }
        this.appendStringData();
        final String str = new String(ch, start, length);
        if (this.currentNode == null) {
            this.preInfo.add(new CommentInfo(str));
        }
        else {
            this.currentNode.appendChild(this.document.createComment(str));
        }
    }
    
    static {
        SAXDocumentFactory.saxFactory = SAXParserFactory.newInstance();
        try {
            SAXDocumentFactory.saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            SAXDocumentFactory.saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            SAXDocumentFactory.saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
        catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        }
        catch (SAXNotSupportedException e2) {
            e2.printStackTrace();
        }
        catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        }
    }
    
    static class ProcessingInstructionInfo implements PreInfo
    {
        public String target;
        public String data;
        
        public ProcessingInstructionInfo(final String target, final String data) {
            this.target = target;
            this.data = data;
        }
        
        @Override
        public Node createNode(final Document doc) {
            return doc.createProcessingInstruction(this.target, this.data);
        }
    }
    
    static class CommentInfo implements PreInfo
    {
        public String comment;
        
        public CommentInfo(final String comment) {
            this.comment = comment;
        }
        
        @Override
        public Node createNode(final Document doc) {
            return doc.createComment(this.comment);
        }
    }
    
    static class CDataInfo implements PreInfo
    {
        public String cdata;
        
        public CDataInfo(final String cdata) {
            this.cdata = cdata;
        }
        
        @Override
        public Node createNode(final Document doc) {
            return doc.createCDATASection(this.cdata);
        }
    }
    
    static class TextInfo implements PreInfo
    {
        public String text;
        
        public TextInfo(final String text) {
            this.text = text;
        }
        
        @Override
        public Node createNode(final Document doc) {
            return doc.createTextNode(this.text);
        }
    }
    
    protected interface PreInfo
    {
        Node createNode(final Document p0);
    }
}
