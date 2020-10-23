// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

public interface SVGItem
{
    void setParent(final AbstractSVGList p0);
    
    AbstractSVGList getParent();
    
    String getValueAsString();
}
