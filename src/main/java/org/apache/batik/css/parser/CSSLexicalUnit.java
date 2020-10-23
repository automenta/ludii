// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.LexicalUnit;

public abstract class CSSLexicalUnit implements LexicalUnit
{
    public static final String UNIT_TEXT_CENTIMETER = "cm";
    public static final String UNIT_TEXT_DEGREE = "deg";
    public static final String UNIT_TEXT_EM = "em";
    public static final String UNIT_TEXT_EX = "ex";
    public static final String UNIT_TEXT_GRADIAN = "grad";
    public static final String UNIT_TEXT_HERTZ = "Hz";
    public static final String UNIT_TEXT_INCH = "in";
    public static final String UNIT_TEXT_KILOHERTZ = "kHz";
    public static final String UNIT_TEXT_MILLIMETER = "mm";
    public static final String UNIT_TEXT_MILLISECOND = "ms";
    public static final String UNIT_TEXT_PERCENTAGE = "%";
    public static final String UNIT_TEXT_PICA = "pc";
    public static final String UNIT_TEXT_PIXEL = "px";
    public static final String UNIT_TEXT_POINT = "pt";
    public static final String UNIT_TEXT_RADIAN = "rad";
    public static final String UNIT_TEXT_REAL = "";
    public static final String UNIT_TEXT_SECOND = "s";
    public static final String TEXT_RGBCOLOR = "rgb";
    public static final String TEXT_RECT_FUNCTION = "rect";
    public static final String TEXT_COUNTER_FUNCTION = "counter";
    public static final String TEXT_COUNTERS_FUNCTION = "counters";
    protected short lexicalUnitType;
    protected LexicalUnit nextLexicalUnit;
    protected LexicalUnit previousLexicalUnit;
    
    protected CSSLexicalUnit(final short t, final LexicalUnit prev) {
        this.lexicalUnitType = t;
        this.previousLexicalUnit = prev;
        if (prev != null) {
            ((CSSLexicalUnit)prev).nextLexicalUnit = this;
        }
    }
    
    @Override
    public short getLexicalUnitType() {
        return this.lexicalUnitType;
    }
    
    @Override
    public LexicalUnit getNextLexicalUnit() {
        return this.nextLexicalUnit;
    }
    
    public void setNextLexicalUnit(final LexicalUnit lu) {
        this.nextLexicalUnit = lu;
    }
    
    @Override
    public LexicalUnit getPreviousLexicalUnit() {
        return this.previousLexicalUnit;
    }
    
    public void setPreviousLexicalUnit(final LexicalUnit lu) {
        this.previousLexicalUnit = lu;
    }
    
    @Override
    public int getIntegerValue() {
        throw new IllegalStateException();
    }
    
    @Override
    public float getFloatValue() {
        throw new IllegalStateException();
    }
    
    @Override
    public String getDimensionUnitText() {
        switch (this.lexicalUnitType) {
            case 19: {
                return "cm";
            }
            case 28: {
                return "deg";
            }
            case 15: {
                return "em";
            }
            case 16: {
                return "ex";
            }
            case 29: {
                return "grad";
            }
            case 33: {
                return "Hz";
            }
            case 18: {
                return "in";
            }
            case 34: {
                return "kHz";
            }
            case 20: {
                return "mm";
            }
            case 31: {
                return "ms";
            }
            case 23: {
                return "%";
            }
            case 22: {
                return "pc";
            }
            case 17: {
                return "px";
            }
            case 21: {
                return "pt";
            }
            case 30: {
                return "rad";
            }
            case 14: {
                return "";
            }
            case 32: {
                return "s";
            }
            default: {
                throw new IllegalStateException("No Unit Text for type: " + this.lexicalUnitType);
            }
        }
    }
    
    @Override
    public String getFunctionName() {
        throw new IllegalStateException();
    }
    
    @Override
    public LexicalUnit getParameters() {
        throw new IllegalStateException();
    }
    
    @Override
    public String getStringValue() {
        throw new IllegalStateException();
    }
    
    @Override
    public LexicalUnit getSubValues() {
        throw new IllegalStateException();
    }
    
    public static CSSLexicalUnit createSimple(final short t, final LexicalUnit prev) {
        return new SimpleLexicalUnit(t, prev);
    }
    
    public static CSSLexicalUnit createInteger(final int val, final LexicalUnit prev) {
        return new IntegerLexicalUnit(val, prev);
    }
    
    public static CSSLexicalUnit createFloat(final short t, final float val, final LexicalUnit prev) {
        return new FloatLexicalUnit(t, val, prev);
    }
    
    public static CSSLexicalUnit createDimension(final float val, final String dim, final LexicalUnit prev) {
        return new DimensionLexicalUnit(val, dim, prev);
    }
    
    public static CSSLexicalUnit createFunction(final String f, final LexicalUnit params, final LexicalUnit prev) {
        return new FunctionLexicalUnit(f, params, prev);
    }
    
    public static CSSLexicalUnit createPredefinedFunction(final short t, final LexicalUnit params, final LexicalUnit prev) {
        return new PredefinedFunctionLexicalUnit(t, params, prev);
    }
    
    public static CSSLexicalUnit createString(final short t, final String val, final LexicalUnit prev) {
        return new StringLexicalUnit(t, val, prev);
    }
    
    protected static class SimpleLexicalUnit extends CSSLexicalUnit
    {
        public SimpleLexicalUnit(final short t, final LexicalUnit prev) {
            super(t, prev);
        }
    }
    
    protected static class IntegerLexicalUnit extends CSSLexicalUnit
    {
        protected int value;
        
        public IntegerLexicalUnit(final int val, final LexicalUnit prev) {
            super((short)13, prev);
            this.value = val;
        }
        
        @Override
        public int getIntegerValue() {
            return this.value;
        }
    }
    
    protected static class FloatLexicalUnit extends CSSLexicalUnit
    {
        protected float value;
        
        public FloatLexicalUnit(final short t, final float val, final LexicalUnit prev) {
            super(t, prev);
            this.value = val;
        }
        
        @Override
        public float getFloatValue() {
            return this.value;
        }
    }
    
    protected static class DimensionLexicalUnit extends CSSLexicalUnit
    {
        protected float value;
        protected String dimension;
        
        public DimensionLexicalUnit(final float val, final String dim, final LexicalUnit prev) {
            super((short)42, prev);
            this.value = val;
            this.dimension = dim;
        }
        
        @Override
        public float getFloatValue() {
            return this.value;
        }
        
        @Override
        public String getDimensionUnitText() {
            return this.dimension;
        }
    }
    
    protected static class FunctionLexicalUnit extends CSSLexicalUnit
    {
        protected String name;
        protected LexicalUnit parameters;
        
        public FunctionLexicalUnit(final String f, final LexicalUnit params, final LexicalUnit prev) {
            super((short)41, prev);
            this.name = f;
            this.parameters = params;
        }
        
        @Override
        public String getFunctionName() {
            return this.name;
        }
        
        @Override
        public LexicalUnit getParameters() {
            return this.parameters;
        }
    }
    
    protected static class PredefinedFunctionLexicalUnit extends CSSLexicalUnit
    {
        protected LexicalUnit parameters;
        
        public PredefinedFunctionLexicalUnit(final short t, final LexicalUnit params, final LexicalUnit prev) {
            super(t, prev);
            this.parameters = params;
        }
        
        @Override
        public String getFunctionName() {
            switch (this.lexicalUnitType) {
                case 27: {
                    return "rgb";
                }
                case 38: {
                    return "rect";
                }
                case 25: {
                    return "counter";
                }
                case 26: {
                    return "counters";
                }
                default: {
                    return super.getFunctionName();
                }
            }
        }
        
        @Override
        public LexicalUnit getParameters() {
            return this.parameters;
        }
    }
    
    protected static class StringLexicalUnit extends CSSLexicalUnit
    {
        protected String value;
        
        public StringLexicalUnit(final short t, final String val, final LexicalUnit prev) {
            super(t, prev);
            this.value = val;
        }
        
        @Override
        public String getStringValue() {
            return this.value;
        }
    }
}
