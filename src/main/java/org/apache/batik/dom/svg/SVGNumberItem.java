// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

public class SVGNumberItem extends AbstractSVGNumber implements SVGItem
{
    protected AbstractSVGList parentList;
    
    public SVGNumberItem(final float value) {
        this.value = value;
    }
    
    @Override
    public String getValueAsString() {
        return Float.toString(this.value);
    }
    
    @Override
    public void setParent(final AbstractSVGList list) {
        this.parentList = list;
    }
    
    @Override
    public AbstractSVGList getParent() {
        return this.parentList;
    }
    
    protected void reset() {
        if (this.parentList != null) {
            this.parentList.itemChanged();
        }
    }
}
