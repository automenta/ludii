// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public class ComputedValue implements Value
{
    protected Value cascadedValue;
    protected Value computedValue;
    
    public ComputedValue(final Value cv) {
        this.cascadedValue = cv;
    }
    
    public Value getComputedValue() {
        return this.computedValue;
    }
    
    public Value getCascadedValue() {
        return this.cascadedValue;
    }
    
    public void setComputedValue(final Value v) {
        this.computedValue = v;
    }
    
    @Override
    public String getCssText() {
        return this.computedValue.getCssText();
    }
    
    @Override
    public short getCssValueType() {
        return this.computedValue.getCssValueType();
    }
    
    @Override
    public short getPrimitiveType() {
        return this.computedValue.getPrimitiveType();
    }
    
    @Override
    public float getFloatValue() throws DOMException {
        return this.computedValue.getFloatValue();
    }
    
    @Override
    public String getStringValue() throws DOMException {
        return this.computedValue.getStringValue();
    }
    
    @Override
    public Value getRed() throws DOMException {
        return this.computedValue.getRed();
    }
    
    @Override
    public Value getGreen() throws DOMException {
        return this.computedValue.getGreen();
    }
    
    @Override
    public Value getBlue() throws DOMException {
        return this.computedValue.getBlue();
    }
    
    @Override
    public int getLength() throws DOMException {
        return this.computedValue.getLength();
    }
    
    @Override
    public Value item(final int index) throws DOMException {
        return this.computedValue.item(index);
    }
    
    @Override
    public Value getTop() throws DOMException {
        return this.computedValue.getTop();
    }
    
    @Override
    public Value getRight() throws DOMException {
        return this.computedValue.getRight();
    }
    
    @Override
    public Value getBottom() throws DOMException {
        return this.computedValue.getBottom();
    }
    
    @Override
    public Value getLeft() throws DOMException {
        return this.computedValue.getLeft();
    }
    
    @Override
    public String getIdentifier() throws DOMException {
        return this.computedValue.getIdentifier();
    }
    
    @Override
    public String getListStyle() throws DOMException {
        return this.computedValue.getListStyle();
    }
    
    @Override
    public String getSeparator() throws DOMException {
        return this.computedValue.getSeparator();
    }
}
