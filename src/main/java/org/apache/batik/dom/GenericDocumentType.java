// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;

public class GenericDocumentType extends AbstractChildNode implements DocumentType
{
    protected String qualifiedName;
    protected String publicId;
    protected String systemId;
    
    public GenericDocumentType(final String qualifiedName, final String publicId, final String systemId) {
        this.qualifiedName = qualifiedName;
        this.publicId = publicId;
        this.systemId = systemId;
    }
    
    @Override
    public String getNodeName() {
        return this.qualifiedName;
    }
    
    @Override
    public short getNodeType() {
        return 10;
    }
    
    @Override
    public boolean isReadonly() {
        return true;
    }
    
    @Override
    public void setReadonly(final boolean ro) {
    }
    
    @Override
    public String getName() {
        return this.qualifiedName;
    }
    
    @Override
    public NamedNodeMap getEntities() {
        return null;
    }
    
    @Override
    public NamedNodeMap getNotations() {
        return null;
    }
    
    @Override
    public String getPublicId() {
        return this.publicId;
    }
    
    @Override
    public String getSystemId() {
        return this.systemId;
    }
    
    @Override
    public String getInternalSubset() {
        return null;
    }
    
    @Override
    protected Node newNode() {
        return new GenericDocumentType(this.qualifiedName, this.publicId, this.systemId);
    }
}
