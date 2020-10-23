// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;

public class GenericCDATASection extends AbstractText implements CDATASection
{
    protected boolean readonly;
    
    protected GenericCDATASection() {
    }
    
    public GenericCDATASection(final String value, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
    }
    
    @Override
    public String getNodeName() {
        return "#cdata-section";
    }
    
    @Override
    public short getNodeType() {
        return 4;
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
        return this.getOwnerDocument().createCDATASection(text);
    }
    
    @Override
    protected Node newNode() {
        return new GenericCDATASection();
    }
}
