// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.CharacterData;

public abstract class AbstractCharacterData extends AbstractChildNode implements CharacterData
{
    protected String nodeValue;
    
    public AbstractCharacterData() {
        this.nodeValue = "";
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return this.nodeValue;
    }
    
    @Override
    public void setNodeValue(final String nodeValue) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        final String val = this.nodeValue;
        this.fireDOMCharacterDataModifiedEvent(val, this.nodeValue = ((nodeValue == null) ? "" : nodeValue));
        if (this.getParentNode() != null) {
            ((AbstractParentNode)this.getParentNode()).fireDOMSubtreeModifiedEvent();
        }
    }
    
    @Override
    public String getData() throws DOMException {
        return this.getNodeValue();
    }
    
    @Override
    public void setData(final String data) throws DOMException {
        this.setNodeValue(data);
    }
    
    @Override
    public int getLength() {
        return this.nodeValue.length();
    }
    
    @Override
    public String substringData(final int offset, final int count) throws DOMException {
        this.checkOffsetCount(offset, count);
        final String v = this.getNodeValue();
        return v.substring(offset, Math.min(v.length(), offset + count));
    }
    
    @Override
    public void appendData(final String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        this.setNodeValue(this.getNodeValue() + ((arg == null) ? "" : arg));
    }
    
    @Override
    public void insertData(final int offset, final String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        if (offset < 0 || offset > this.getLength()) {
            throw this.createDOMException((short)1, "offset", new Object[] { offset });
        }
        final String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + arg + v.substring(offset, v.length()));
    }
    
    @Override
    public void deleteData(final int offset, final int count) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        this.checkOffsetCount(offset, count);
        final String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + v.substring(Math.min(v.length(), offset + count), v.length()));
    }
    
    @Override
    public void replaceData(final int offset, final int count, final String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        this.checkOffsetCount(offset, count);
        final String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + arg + v.substring(Math.min(v.length(), offset + count), v.length()));
    }
    
    protected void checkOffsetCount(final int offset, final int count) throws DOMException {
        if (offset < 0 || offset >= this.getLength()) {
            throw this.createDOMException((short)1, "offset", new Object[] { offset });
        }
        if (count < 0) {
            throw this.createDOMException((short)1, "negative.count", new Object[] { count });
        }
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }
}
