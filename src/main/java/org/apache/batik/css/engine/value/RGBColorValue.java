// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public class RGBColorValue extends AbstractValue
{
    protected Value red;
    protected Value green;
    protected Value blue;
    
    public RGBColorValue(final Value r, final Value g, final Value b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }
    
    @Override
    public short getPrimitiveType() {
        return 25;
    }
    
    @Override
    public String getCssText() {
        return "rgb(" + this.red.getCssText() + ", " + this.green.getCssText() + ", " + this.blue.getCssText() + ')';
    }
    
    @Override
    public Value getRed() throws DOMException {
        return this.red;
    }
    
    @Override
    public Value getGreen() throws DOMException {
        return this.green;
    }
    
    @Override
    public Value getBlue() throws DOMException {
        return this.blue;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
