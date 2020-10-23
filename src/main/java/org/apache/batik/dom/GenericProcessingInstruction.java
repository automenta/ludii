// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericProcessingInstruction extends AbstractProcessingInstruction
{
    protected String target;
    protected boolean readonly;
    
    protected GenericProcessingInstruction() {
    }
    
    public GenericProcessingInstruction(final String target, final String data, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setTarget(target);
        this.setData(data);
    }
    
    @Override
    public void setNodeName(final String v) {
        this.setTarget(v);
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
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String v) {
        this.target = v;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        final GenericProcessingInstruction p = (GenericProcessingInstruction)super.export(n, d);
        p.setTarget(this.getTarget());
        return p;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        final GenericProcessingInstruction p = (GenericProcessingInstruction)super.deepExport(n, d);
        p.setTarget(this.getTarget());
        return p;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        final GenericProcessingInstruction p = (GenericProcessingInstruction)super.copyInto(n);
        p.setTarget(this.getTarget());
        return p;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        final GenericProcessingInstruction p = (GenericProcessingInstruction)super.deepCopyInto(n);
        p.setTarget(this.getTarget());
        return p;
    }
    
    @Override
    protected Node newNode() {
        return new GenericProcessingInstruction();
    }
}
