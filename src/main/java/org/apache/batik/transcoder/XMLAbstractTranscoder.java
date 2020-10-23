// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import org.apache.batik.transcoder.keys.DOMImplementationKey;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.dom.util.DocumentFactory;
import org.w3c.dom.Document;
import java.io.IOException;
import org.w3c.dom.DOMException;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;

public abstract class XMLAbstractTranscoder extends AbstractTranscoder
{
    public static final TranscodingHints.Key KEY_XML_PARSER_CLASSNAME;
    public static final TranscodingHints.Key KEY_XML_PARSER_VALIDATING;
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT;
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT_NAMESPACE_URI;
    public static final TranscodingHints.Key KEY_DOM_IMPLEMENTATION;
    
    protected XMLAbstractTranscoder() {
        this.hints.put(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
    }
    
    @Override
    public void transcode(final TranscoderInput input, final TranscoderOutput output) throws TranscoderException {
        Document document = null;
        final String uri = input.getURI();
        if (input.getDocument() != null) {
            document = input.getDocument();
        }
        else {
            String parserClassname = (String)this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_CLASSNAME);
            final String namespaceURI = (String)this.hints.get(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI);
            final String documentElement = (String)this.hints.get(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT);
            final DOMImplementation domImpl = (DOMImplementation)this.hints.get(XMLAbstractTranscoder.KEY_DOM_IMPLEMENTATION);
            if (parserClassname == null) {
                parserClassname = XMLResourceDescriptor.getXMLParserClassName();
            }
            if (domImpl == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOM_IMPLEMENTATION"));
                return;
            }
            if (namespaceURI == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT_NAMESPACE_URI"));
                return;
            }
            if (documentElement == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT"));
                return;
            }
            final DocumentFactory f = this.createDocumentFactory(domImpl, parserClassname);
            final Object xmlParserValidating = this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING);
            final boolean validating = xmlParserValidating != null && (boolean)xmlParserValidating;
            f.setValidating(validating);
            try {
                if (input.getInputStream() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getInputStream());
                }
                else if (input.getReader() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getReader());
                }
                else if (input.getXMLReader() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getXMLReader());
                }
                else if (uri != null) {
                    document = f.createDocument(namespaceURI, documentElement, uri);
                }
            }
            catch (DOMException ex) {
                this.handler.fatalError(new TranscoderException(ex));
            }
            catch (IOException ex2) {
                this.handler.fatalError(new TranscoderException(ex2));
            }
        }
        if (document != null) {
            try {
                this.transcode(document, uri, output);
            }
            catch (TranscoderException ex3) {
                this.handler.fatalError(ex3);
            }
        }
    }
    
    protected DocumentFactory createDocumentFactory(final DOMImplementation domImpl, final String parserClassname) {
        return new SAXDocumentFactory(domImpl, parserClassname);
    }
    
    protected abstract void transcode(final Document p0, final String p1, final TranscoderOutput p2) throws TranscoderException;
    
    static {
        KEY_XML_PARSER_CLASSNAME = new StringKey();
        KEY_XML_PARSER_VALIDATING = new BooleanKey();
        KEY_DOCUMENT_ELEMENT = new StringKey();
        KEY_DOCUMENT_ELEMENT_NAMESPACE_URI = new StringKey();
        KEY_DOM_IMPLEMENTATION = new DOMImplementationKey();
    }
}
