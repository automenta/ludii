// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public abstract class AbstractNotation extends AbstractNode implements Notation
{
    protected String nodeName;
    protected String publicId;
    protected String systemId;
    
    @Override
    public short getNodeType() {
        return 12;
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
    public void setTextContent(final String s) throws DOMException {
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }
}
