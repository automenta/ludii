// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGNumber;

public abstract class AbstractSVGNumber implements SVGNumber
{
    protected float value;
    
    @Override
    public float getValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final float f) {
        this.value = f;
    }
}
