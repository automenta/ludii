// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.AngleHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.DefaultAngleHandler;
import org.apache.batik.parser.AngleParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAngle;

public class SVGOMAngle implements SVGAngle
{
    private short unitType;
    protected float value;
    protected static final String[] UNITS;
    protected static double[][] K;
    
    @Override
    public short getUnitType() {
        this.revalidate();
        return this.unitType;
    }
    
    @Override
    public float getValue() {
        this.revalidate();
        return toUnit(this.getUnitType(), this.value, (short)2);
    }
    
    @Override
    public void setValue(final float value) throws DOMException {
        this.revalidate();
        this.setUnitType((short)2);
        this.value = value;
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
        return Float.toString(this.value) + SVGOMAngle.UNITS[this.getUnitType()];
    }
    
    @Override
    public void setValueAsString(final String value) throws DOMException {
        this.parse(value);
        this.reset();
    }
    
    @Override
    public void newValueSpecifiedUnits(final short unit, final float value) {
        this.setUnitType(unit);
        this.value = value;
        this.reset();
    }
    
    @Override
    public void convertToSpecifiedUnits(final short unit) {
        this.value = toUnit(this.getUnitType(), this.value, unit);
        this.setUnitType(unit);
    }
    
    protected void reset() {
    }
    
    protected void revalidate() {
    }
    
    protected void parse(final String s) {
        try {
            final AngleParser angleParser = new AngleParser();
            angleParser.setAngleHandler(new DefaultAngleHandler() {
                @Override
                public void angleValue(final float v) throws ParseException {
                    SVGOMAngle.this.value = v;
                }
                
                @Override
                public void deg() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)2);
                }
                
                @Override
                public void rad() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)3);
                }
                
                @Override
                public void grad() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)4);
                }
            });
            this.setUnitType((short)1);
            angleParser.parse(s);
        }
        catch (ParseException e) {
            this.setUnitType((short)0);
            this.value = 0.0f;
        }
    }
    
    public static float toUnit(short fromUnit, final float value, short toUnit) {
        if (fromUnit == 1) {
            fromUnit = 2;
        }
        if (toUnit == 1) {
            toUnit = 2;
        }
        return (float)(SVGOMAngle.K[fromUnit - 2][toUnit - 2] * value);
    }
    
    public void setUnitType(final short unitType) {
        this.unitType = unitType;
    }
    
    static {
        UNITS = new String[] { "", "", "deg", "rad", "grad" };
        SVGOMAngle.K = new double[][] { { 1.0, 0.017453292519943295, 0.015707963267948967 }, { 57.29577951308232, 1.0, 63.66197723675813 }, { 0.9, 0.015707963267948967, 1.0 } };
    }
}
