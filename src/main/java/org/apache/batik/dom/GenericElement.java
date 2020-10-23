// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

public class GenericElement extends AbstractElement
{
    protected String nodeName;
    protected boolean readonly;
    
    protected GenericElement() {
    }
    
    public GenericElement(final String name, final AbstractDocument owner) throws DOMException {
        super(name, owner);
        this.nodeName = name;
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
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public void setReadonly(final boolean v) {
        this.readonly = v;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final GenericElement ge = (GenericElement)n;
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final GenericElement ge = (GenericElement)n;
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        final GenericElement ge = (GenericElement)super.copyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        final GenericElement ge = (GenericElement)super.deepCopyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node newNode() {
        return new GenericElement();
    }
}
