// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import org.w3c.dom.Element;

public abstract class UnitProcessor
{
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    static final double SQRT2;
    
    protected UnitProcessor() {
    }
    
    public static float svgToObjectBoundingBox(final String s, final String attr, final short d, final Context ctx) throws ParseException {
        final LengthParser lengthParser = new LengthParser();
        final UnitResolver ur = new UnitResolver();
        lengthParser.setLengthHandler(ur);
        lengthParser.parse(s);
        return svgToObjectBoundingBox(ur.value, ur.unit, d, ctx);
    }
    
    public static float svgToObjectBoundingBox(final float value, final short type, final short d, final Context ctx) {
        switch (type) {
            case 1: {
                return value;
            }
            case 2: {
                return value / 100.0f;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10: {
                return svgToUserSpace(value, type, d, ctx);
            }
            default: {
                throw new IllegalArgumentException("Length has unknown type");
            }
        }
    }
    
    public static float svgToUserSpace(final String s, final String attr, final short d, final Context ctx) throws ParseException {
        final LengthParser lengthParser = new LengthParser();
        final UnitResolver ur = new UnitResolver();
        lengthParser.setLengthHandler(ur);
        lengthParser.parse(s);
        return svgToUserSpace(ur.value, ur.unit, d, ctx);
    }
    
    public static float svgToUserSpace(final float v, final short type, final short d, final Context ctx) {
        switch (type) {
            case 1:
            case 5: {
                return v;
            }
            case 7: {
                return v / ctx.getPixelUnitToMillimeter();
            }
            case 6: {
                return v * 10.0f / ctx.getPixelUnitToMillimeter();
            }
            case 8: {
                return v * 25.4f / ctx.getPixelUnitToMillimeter();
            }
            case 9: {
                return v * 25.4f / (72.0f * ctx.getPixelUnitToMillimeter());
            }
            case 10: {
                return v * 25.4f / (6.0f * ctx.getPixelUnitToMillimeter());
            }
            case 3: {
                return emsToPixels(v, d, ctx);
            }
            case 4: {
                return exsToPixels(v, d, ctx);
            }
            case 2: {
                return percentagesToPixels(v, d, ctx);
            }
            default: {
                throw new IllegalArgumentException("Length has unknown type");
            }
        }
    }
    
    public static float userSpaceToSVG(final float v, final short type, final short d, final Context ctx) {
        switch (type) {
            case 1:
            case 5: {
                return v;
            }
            case 7: {
                return v * ctx.getPixelUnitToMillimeter();
            }
            case 6: {
                return v * ctx.getPixelUnitToMillimeter() / 10.0f;
            }
            case 8: {
                return v * ctx.getPixelUnitToMillimeter() / 25.4f;
            }
            case 9: {
                return v * (72.0f * ctx.getPixelUnitToMillimeter()) / 25.4f;
            }
            case 10: {
                return v * (6.0f * ctx.getPixelUnitToMillimeter()) / 25.4f;
            }
            case 3: {
                return pixelsToEms(v, d, ctx);
            }
            case 4: {
                return pixelsToExs(v, d, ctx);
            }
            case 2: {
                return pixelsToPercentages(v, d, ctx);
            }
            default: {
                throw new IllegalArgumentException("Length has unknown type");
            }
        }
    }
    
    protected static float percentagesToPixels(final float v, final short d, final Context ctx) {
        if (d == 2) {
            final float w = ctx.getViewportWidth();
            return w * v / 100.0f;
        }
        if (d == 1) {
            final float h = ctx.getViewportHeight();
            return h * v / 100.0f;
        }
        final double w2 = ctx.getViewportWidth();
        final double h2 = ctx.getViewportHeight();
        final double vpp = Math.sqrt(w2 * w2 + h2 * h2) / UnitProcessor.SQRT2;
        return (float)(vpp * v / 100.0);
    }
    
    protected static float pixelsToPercentages(final float v, final short d, final Context ctx) {
        if (d == 2) {
            final float w = ctx.getViewportWidth();
            return v * 100.0f / w;
        }
        if (d == 1) {
            final float h = ctx.getViewportHeight();
            return v * 100.0f / h;
        }
        final double w2 = ctx.getViewportWidth();
        final double h2 = ctx.getViewportHeight();
        final double vpp = Math.sqrt(w2 * w2 + h2 * h2) / UnitProcessor.SQRT2;
        return (float)(v * 100.0 / vpp);
    }
    
    protected static float pixelsToEms(final float v, final short d, final Context ctx) {
        return v / ctx.getFontSize();
    }
    
    protected static float emsToPixels(final float v, final short d, final Context ctx) {
        return v * ctx.getFontSize();
    }
    
    protected static float pixelsToExs(final float v, final short d, final Context ctx) {
        final float xh = ctx.getXHeight();
        return v / xh / ctx.getFontSize();
    }
    
    protected static float exsToPixels(final float v, final short d, final Context ctx) {
        final float xh = ctx.getXHeight();
        return v * xh * ctx.getFontSize();
    }
    
    static {
        SQRT2 = Math.sqrt(2.0);
    }
    
    public static class UnitResolver implements LengthHandler
    {
        public float value;
        public short unit;
        
        public UnitResolver() {
            this.unit = 1;
        }
        
        @Override
        public void startLength() throws ParseException {
        }
        
        @Override
        public void lengthValue(final float v) throws ParseException {
            this.value = v;
        }
        
        @Override
        public void em() throws ParseException {
            this.unit = 3;
        }
        
        @Override
        public void ex() throws ParseException {
            this.unit = 4;
        }
        
        @Override
        public void in() throws ParseException {
            this.unit = 8;
        }
        
        @Override
        public void cm() throws ParseException {
            this.unit = 6;
        }
        
        @Override
        public void mm() throws ParseException {
            this.unit = 7;
        }
        
        @Override
        public void pc() throws ParseException {
            this.unit = 10;
        }
        
        @Override
        public void pt() throws ParseException {
            this.unit = 9;
        }
        
        @Override
        public void px() throws ParseException {
            this.unit = 5;
        }
        
        @Override
        public void percentage() throws ParseException {
            this.unit = 2;
        }
        
        @Override
        public void endLength() throws ParseException {
        }
    }
    
    public interface Context
    {
        Element getElement();
        
        float getPixelUnitToMillimeter();
        
        float getPixelToMM();
        
        float getFontSize();
        
        float getXHeight();
        
        float getViewportWidth();
        
        float getViewportHeight();
    }
}
