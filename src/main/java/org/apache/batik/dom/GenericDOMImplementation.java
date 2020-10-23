// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;

public class GenericDOMImplementation extends AbstractDOMImplementation
{
    protected static final DOMImplementation DOM_IMPLEMENTATION;
    
    public static DOMImplementation getDOMImplementation() {
        return GenericDOMImplementation.DOM_IMPLEMENTATION;
    }
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        final Document result = new GenericDocument(doctype, this);
        result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        return result;
    }
    
    @Override
    public DocumentType createDocumentType(String qualifiedName, final String publicId, final String systemId) {
        if (qualifiedName == null) {
            qualifiedName = "";
        }
        final int test = XMLUtilities.testXMLQName(qualifiedName);
        if ((test & 0x1) == 0x0) {
            throw new DOMException((short)5, this.formatMessage("xml.name", new Object[] { qualifiedName }));
        }
        if ((test & 0x2) == 0x0) {
            throw new DOMException((short)5, this.formatMessage("invalid.qname", new Object[] { qualifiedName }));
        }
        return new GenericDocumentType(qualifiedName, publicId, systemId);
    }
    
    static {
        DOM_IMPLEMENTATION = new GenericDOMImplementation();
    }
}
