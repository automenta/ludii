// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class GenericText extends AbstractText
{
    protected boolean readonly;
    
    protected GenericText() {
    }
    
    public GenericText(final String value, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
    }
    
    @Override
    public String getNodeName() {
        return "#text";
    }
    
    @Override
    public short getNodeType() {
        return 3;
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
    protected Text createTextNode(final String text) {
        return this.getOwnerDocument().createTextNode(text);
    }
    
    @Override
    protected Node newNode() {
        return new GenericText();
    }
}
