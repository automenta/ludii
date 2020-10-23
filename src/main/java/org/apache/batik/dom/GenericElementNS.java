// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericElementNS extends AbstractElementNS
{
    protected String nodeName;
    protected boolean readonly;
    
    protected GenericElementNS() {
    }
    
    public GenericElementNS(final String nsURI, final String name, final AbstractDocument owner) {
        super(nsURI, name, owner);
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
        final GenericElementNS ge = (GenericElementNS)super.export(n, d);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        final GenericElementNS ge = (GenericElementNS)super.deepExport(n, d);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        final GenericElementNS ge = (GenericElementNS)super.copyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        final GenericElementNS ge = (GenericElementNS)super.deepCopyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node newNode() {
        return new GenericElementNS();
    }
}
