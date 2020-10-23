// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

public abstract class AbstractSVGItem implements SVGItem
{
    protected AbstractSVGList parent;
    protected String itemStringValue;
    
    protected abstract String getStringValue();
    
    protected AbstractSVGItem() {
    }
    
    @Override
    public void setParent(final AbstractSVGList list) {
        this.parent = list;
    }
    
    @Override
    public AbstractSVGList getParent() {
        return this.parent;
    }
    
    protected void resetAttribute() {
        if (this.parent != null) {
            this.itemStringValue = null;
            this.parent.itemChanged();
        }
    }
    
    @Override
    public String getValueAsString() {
        if (this.itemStringValue == null) {
            this.itemStringValue = this.getStringValue();
        }
        return this.itemStringValue;
    }
}
