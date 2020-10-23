// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Element;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.w3c.dom.DOMException;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.svg.SVGLength;

public abstract class AbstractSVGLength implements SVGLength
{
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    protected short unitType;
    protected float value;
    protected short direction;
    protected UnitProcessor.Context context;
    protected static final String[] UNITS;
    
    protected abstract SVGOMElement getAssociatedElement();
    
    public AbstractSVGLength(final short direction) {
        this.context = new DefaultContext();
        this.direction = direction;
        this.value = 0.0f;
        this.unitType = 1;
    }
    
    @Override
    public short getUnitType() {
        this.revalidate();
        return this.unitType;
    }
    
    @Override
    public float getValue() {
        this.revalidate();
        try {
            return UnitProcessor.svgToUserSpace(this.value, this.unitType, this.direction, this.context);
        }
        catch (IllegalArgumentException ex) {
            return 0.0f;
        }
    }
    
    @Override
    public void setValue(final float value) throws DOMException {
        this.value = UnitProcessor.userSpaceToSVG(value, this.unitType, this.direction, this.context);
        this.reset();
    }
    
    @Override
    public float getValueInSpecifiedUnits() {
        this.revalidate();
        return this.value;
    }
    
    @Override
    public void setValueInSpecifiedUnits(final float value) throws DOMException {
        this.revalidate();
        this.value = value;
        this.reset();
    }
    
    @Override
    public String getValueAsString() {
        this.revalidate();
        if (this.unitType == 0) {
            return "";
        }
        return Float.toString(this.value) + AbstractSVGLength.UNITS[this.unitType];
    }
    
    @Override
    public void setValueAsString(final String value) throws DOMException {
        this.parse(value);
        this.reset();
    }
    
    @Override
    public void newValueSpecifiedUnits(final short unit, final float value) {
        this.unitType = unit;
        this.value = value;
        this.reset();
    }
    
    @Override
    public void convertToSpecifiedUnits(final short unit) {
        final float v = this.getValue();
        this.unitType = unit;
        this.setValue(v);
    }
    
    protected void reset() {
    }
    
    protected void revalidate() {
    }
    
    protected void parse(final String s) {
        try {
            final LengthParser lengthParser = new LengthParser();
            final UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
            lengthParser.setLengthHandler(ur);
            lengthParser.parse(s);
            this.unitType = ur.unit;
            this.value = ur.value;
        }
        catch (ParseException e) {
            this.unitType = 0;
            this.value = 0.0f;
        }
    }
    
    static {
        UNITS = new String[] { "", "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc" };
    }
    
    protected class DefaultContext implements UnitProcessor.Context
    {
        @Override
        public Element getElement() {
            return AbstractSVGLength.this.getAssociatedElement();
        }
        
        @Override
        public float getPixelUnitToMillimeter() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getPixelUnitToMillimeter();
        }
        
        @Override
        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }
        
        @Override
        public float getFontSize() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getFontSize();
        }
        
        @Override
        public float getXHeight() {
            return 0.5f;
        }
        
        @Override
        public float getViewportWidth() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getViewportWidth();
        }
        
        @Override
        public float getViewportHeight() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getViewportHeight();
        }
    }
}
