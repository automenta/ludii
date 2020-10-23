// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Entity;

public abstract class AbstractEntity extends AbstractParentNode implements Entity
{
    protected String nodeName;
    protected String publicId;
    protected String systemId;
    
    @Override
    public short getNodeType() {
        return 6;
    }
    
    @Override
    public void setNodeName(final String v) {
        this.nodeName = v;
    }
    
    @Override
    public String getNodeName() {
        return this.nodeName;
    }
    
    @Override
    public String getPublicId() {
        return this.publicId;
    }
    
    public void setPublicId(final String id) {
        this.publicId = id;
    }
    
    @Override
    public String getSystemId() {
        return this.systemId;
    }
    
    public void setSystemId(final String id) {
        this.systemId = id;
    }
    
    @Override
    public String getNotationName() {
        return this.getNodeName();
    }
    
    public void setNotationName(final String name) {
        this.setNodeName(name);
    }
    
    @Override
    public String getInputEncoding() {
        return null;
    }
    
    @Override
    public String getXmlEncoding() {
        return null;
    }
    
    @Override
    public String getXmlVersion() {
        return null;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected void checkChildType(final Node n, final boolean replace) {
        switch (n.getNodeType()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 11: {}
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[] { this.getNodeType(), this.getNodeName(), n.getNodeType(), n.getNodeName() });
            }
        }
    }
}
