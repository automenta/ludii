// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class GenericDocument extends AbstractDocument
{
    protected static final String ATTR_ID = "id";
    protected boolean readonly;
    
    protected GenericDocument() {
    }
    
    public GenericDocument(final DocumentType dt, final DOMImplementation impl) {
        super(dt, impl);
    }
    
    @Override
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public void setReadonly(final boolean v) {
        this.readonly = v;
    }
    
    @Override
    public boolean isId(final Attr node) {
        return node.getNamespaceURI() == null && "id".equals(node.getNodeName());
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        return new GenericElement(tagName.intern(), this);
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return new GenericDocumentFragment(this);
    }
    
    @Override
    public Text createTextNode(final String data) {
        return new GenericText(data, this);
    }
    
    @Override
    public Comment createComment(final String data) {
        return new GenericComment(data, this);
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        return new GenericCDATASection(data, this);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        return new GenericProcessingInstruction(target, data, this);
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        return new GenericAttr(name.intern(), this);
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        return new GenericEntityReference(name, this);
    }
    
    @Override
    public Element createElementNS(String namespaceURI, final String qualifiedName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), this);
        }
        return new GenericElementNS(namespaceURI.intern(), qualifiedName.intern(), this);
    }
    
    @Override
    public Attr createAttributeNS(String namespaceURI, final String qualifiedName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericAttr(qualifiedName.intern(), this);
        }
        return new GenericAttrNS(namespaceURI.intern(), qualifiedName.intern(), this);
    }
    
    @Override
    protected Node newNode() {
        return new GenericDocument();
    }
}
