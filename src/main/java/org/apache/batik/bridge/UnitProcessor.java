// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.parser.ParseException;
import org.w3c.dom.Element;

public abstract class UnitProcessor extends org.apache.batik.parser.UnitProcessor
{
    public static Context createContext(final BridgeContext ctx, final Element e) {
        return new DefaultContext(ctx, e);
    }
    
    public static float svgHorizontalCoordinateToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgToObjectBoundingBox(s, attr, (short)2, ctx);
    }
    
    public static float svgVerticalCoordinateToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgToObjectBoundingBox(s, attr, (short)1, ctx);
    }
    
    public static float svgOtherCoordinateToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgToObjectBoundingBox(s, attr, (short)0, ctx);
    }
    
    public static float svgHorizontalLengthToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, (short)2, ctx);
    }
    
    public static float svgVerticalLengthToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, (short)1, ctx);
    }
    
    public static float svgOtherLengthToObjectBoundingBox(final String s, final String attr, final Context ctx) {
        return svgLengthToObjectBoundingBox(s, attr, (short)0, ctx);
    }
    
    public static float svgLengthToObjectBoundingBox(final String s, final String attr, final short d, final Context ctx) {
        final float v = svgToObjectBoundingBox(s, attr, d, ctx);
        if (v < 0.0f) {
            throw new BridgeException(getBridgeContext(ctx), ctx.getElement(), "length.negative", new Object[] { attr, s });
        }
        return v;
    }
    
    public static float svgToObjectBoundingBox(final String s, final String attr, final short d, final Context ctx) {
        try {
            return org.apache.batik.parser.UnitProcessor.svgToObjectBoundingBox(s, attr, d, ctx);
        }
        catch (ParseException pEx) {
            throw new BridgeException(getBridgeContext(ctx), ctx.getElement(), pEx, "attribute.malformed", new Object[] { attr, s, pEx });
        }
    }
    
    public static float svgHorizontalLengthToUserSpace(final String s, final String attr, final Context ctx) {
        return svgLengthToUserSpace(s, attr, (short)2, ctx);
    }
    
    public static float svgVerticalLengthToUserSpace(final String s, final String attr, final Context ctx) {
        return svgLengthToUserSpace(s, attr, (short)1, ctx);
    }
    
    public static float svgOtherLengthToUserSpace(final String s, final String attr, final Context ctx) {
        return svgLengthToUserSpace(s, attr, (short)0, ctx);
    }
    
    public static float svgHorizontalCoordinateToUserSpace(final String s, final String attr, final Context ctx) {
        return svgToUserSpace(s, attr, (short)2, ctx);
    }
    
    public static float svgVerticalCoordinateToUserSpace(final String s, final String attr, final Context ctx) {
        return svgToUserSpace(s, attr, (short)1, ctx);
    }
    
    public static float svgOtherCoordinateToUserSpace(final String s, final String attr, final Context ctx) {
        return svgToUserSpace(s, attr, (short)0, ctx);
    }
    
    public static float svgLengthToUserSpace(final String s, final String attr, final short d, final Context ctx) {
        final float v = svgToUserSpace(s, attr, d, ctx);
        if (v < 0.0f) {
            throw new BridgeException(getBridgeContext(ctx), ctx.getElement(), "length.negative", new Object[] { attr, s });
        }
        return v;
    }
    
    public static float svgToUserSpace(final String s, final String attr, final short d, final Context ctx) {
        try {
            return org.apache.batik.parser.UnitProcessor.svgToUserSpace(s, attr, d, ctx);
        }
        catch (ParseException pEx) {
            throw new BridgeException(getBridgeContext(ctx), ctx.getElement(), pEx, "attribute.malformed", new Object[] { attr, s, pEx });
        }
    }
    
    protected static BridgeContext getBridgeContext(final Context ctx) {
        if (ctx instanceof DefaultContext) {
            return ((DefaultContext)ctx).ctx;
        }
        return null;
    }
    
    public static class DefaultContext implements Context
    {
        protected Element e;
        protected BridgeContext ctx;
        
        public DefaultContext(final BridgeContext ctx, final Element e) {
            this.ctx = ctx;
            this.e = e;
        }
        
        @Override
        public Element getElement() {
            return this.e;
        }
        
        @Override
        public float getPixelUnitToMillimeter() {
            return this.ctx.getUserAgent().getPixelUnitToMillimeter();
        }
        
        @Override
        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }
        
        @Override
        public float getFontSize() {
            return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
        }
        
        @Override
        public float getXHeight() {
            return 0.5f;
        }
        
        @Override
        public float getViewportWidth() {
            return this.ctx.getViewport(this.e).getWidth();
        }
        
        @Override
        public float getViewportHeight() {
            return this.ctx.getViewport(this.e).getHeight();
        }
    }
}
