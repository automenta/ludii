// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;

public abstract class AbstractProcessingInstruction extends AbstractChildNode implements ProcessingInstruction
{
    protected String data;
    
    @Override
    public String getNodeName() {
        return this.getTarget();
    }
    
    @Override
    public short getNodeType() {
        return 7;
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return this.getData();
    }
    
    @Override
    public void setNodeValue(final String nodeValue) throws DOMException {
        this.setData(nodeValue);
    }
    
    @Override
    public String getData() {
        return this.data;
    }
    
    @Override
    public void setData(final String data) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        final String val = this.data;
        this.fireDOMCharacterDataModifiedEvent(val, this.data = data);
        if (this.getParentNode() != null) {
            ((AbstractParentNode)this.getParentNode()).fireDOMSubtreeModifiedEvent();
        }
    }
    
    @Override
    public String getTextContent() {
        return this.getNodeValue();
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        final AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.export(n, d);
        p.data = this.data;
        return p;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        final AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.deepExport(n, d);
        p.data = this.data;
        return p;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        final AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.copyInto(n);
        p.data = this.data;
        return p;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        final AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.deepCopyInto(n);
        p.data = this.data;
        return p;
    }
}
