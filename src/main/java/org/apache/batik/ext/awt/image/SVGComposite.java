// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.PackedColorModel;
import java.awt.image.ColorModel;
import java.awt.AlphaComposite;
import java.awt.Composite;

public class SVGComposite implements Composite
{
    public static final SVGComposite OVER;
    public static final SVGComposite IN;
    public static final SVGComposite OUT;
    public static final SVGComposite ATOP;
    public static final SVGComposite XOR;
    public static final SVGComposite MULTIPLY;
    public static final SVGComposite SCREEN;
    public static final SVGComposite DARKEN;
    public static final SVGComposite LIGHTEN;
    CompositeRule rule;
    
    public CompositeRule getRule() {
        return this.rule;
    }
    
    public SVGComposite(final CompositeRule rule) {
        this.rule = rule;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof SVGComposite) {
            final SVGComposite svgc = (SVGComposite)o;
            return svgc.getRule() == this.getRule();
        }
        if (!(o instanceof AlphaComposite)) {
            return false;
        }
        final AlphaComposite ac = (AlphaComposite)o;
        switch (this.getRule().getRule()) {
            case 1: {
                return ac == AlphaComposite.SrcOver;
            }
            case 2: {
                return ac == AlphaComposite.SrcIn;
            }
            case 3: {
                return ac == AlphaComposite.SrcOut;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean is_INT_PACK(final ColorModel cm) {
        if (!(cm instanceof PackedColorModel)) {
            return false;
        }
        final PackedColorModel pcm = (PackedColorModel)cm;
        final int[] masks = pcm.getMasks();
        return masks.length == 4 && masks[0] == 16711680 && masks[1] == 65280 && masks[2] == 255 && masks[3] == -16777216;
    }
    
    @Override
    public CompositeContext createContext(final ColorModel srcCM, final ColorModel dstCM, final RenderingHints hints) {
        final boolean use_int_pack = this.is_INT_PACK(srcCM) && this.is_INT_PACK(dstCM);
        switch (this.rule.getRule()) {
            case 1: {
                if (!dstCM.hasAlpha()) {
                    if (use_int_pack) {
                        return new OverCompositeContext_INT_PACK_NA(srcCM, dstCM);
                    }
                    return new OverCompositeContext_NA(srcCM, dstCM);
                }
                else {
                    if (!use_int_pack) {
                        return new OverCompositeContext(srcCM, dstCM);
                    }
                    if (srcCM.isAlphaPremultiplied()) {
                        return new OverCompositeContext_INT_PACK(srcCM, dstCM);
                    }
                    return new OverCompositeContext_INT_PACK_UNPRE(srcCM, dstCM);
                }
                break;
            }
            case 2: {
                if (use_int_pack) {
                    return new InCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new InCompositeContext(srcCM, dstCM);
            }
            case 3: {
                if (use_int_pack) {
                    return new OutCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new OutCompositeContext(srcCM, dstCM);
            }
            case 4: {
                if (use_int_pack) {
                    return new AtopCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new AtopCompositeContext(srcCM, dstCM);
            }
            case 5: {
                if (use_int_pack) {
                    return new XorCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new XorCompositeContext(srcCM, dstCM);
            }
            case 6: {
                final float[] coeff = this.rule.getCoefficients();
                if (use_int_pack) {
                    return new ArithCompositeContext_INT_PACK_LUT(srcCM, dstCM, coeff[0], coeff[1], coeff[2], coeff[3]);
                }
                return new ArithCompositeContext(srcCM, dstCM, coeff[0], coeff[1], coeff[2], coeff[3]);
            }
            case 7: {
                if (use_int_pack) {
                    return new MultiplyCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new MultiplyCompositeContext(srcCM, dstCM);
            }
            case 8: {
                if (use_int_pack) {
                    return new ScreenCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new ScreenCompositeContext(srcCM, dstCM);
            }
            case 9: {
                if (use_int_pack) {
                    return new DarkenCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new DarkenCompositeContext(srcCM, dstCM);
            }
            case 10: {
                if (use_int_pack) {
                    return new LightenCompositeContext_INT_PACK(srcCM, dstCM);
                }
                return new LightenCompositeContext(srcCM, dstCM);
            }
            default: {
                throw new UnsupportedOperationException("Unknown composite rule requested.");
            }
        }
    }
    
    static {
        OVER = new SVGComposite(CompositeRule.OVER);
        IN = new SVGComposite(CompositeRule.IN);
        OUT = new SVGComposite(CompositeRule.OUT);
        ATOP = new SVGComposite(CompositeRule.ATOP);
        XOR = new SVGComposite(CompositeRule.XOR);
        MULTIPLY = new SVGComposite(CompositeRule.MULTIPLY);
        SCREEN = new SVGComposite(CompositeRule.SCREEN);
        DARKEN = new SVGComposite(CompositeRule.DARKEN);
        LIGHTEN = new SVGComposite(CompositeRule.LIGHTEN);
    }
    
    public abstract static class AlphaPreCompositeContext implements CompositeContext
    {
        ColorModel srcCM;
        ColorModel dstCM;
        
        AlphaPreCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            this.srcCM = srcCM;
            this.dstCM = dstCM;
        }
        
        @Override
        public void dispose() {
            this.srcCM = null;
            this.dstCM = null;
        }
        
        protected abstract void precompose(final Raster p0, final Raster p1, final WritableRaster p2);
        
        @Override
        public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            ColorModel srcPreCM = this.srcCM;
            if (!this.srcCM.isAlphaPremultiplied()) {
                srcPreCM = GraphicsUtil.coerceData((WritableRaster)src, this.srcCM, true);
            }
            ColorModel dstPreCM = this.dstCM;
            if (!this.dstCM.isAlphaPremultiplied()) {
                dstPreCM = GraphicsUtil.coerceData((WritableRaster)dstIn, this.dstCM, true);
            }
            this.precompose(src, dstIn, dstOut);
            if (!this.srcCM.isAlphaPremultiplied()) {
                GraphicsUtil.coerceData((WritableRaster)src, srcPreCM, false);
            }
            if (!this.dstCM.isAlphaPremultiplied()) {
                GraphicsUtil.coerceData(dstOut, dstPreCM, false);
                if (dstIn != dstOut) {
                    GraphicsUtil.coerceData((WritableRaster)dstIn, dstPreCM, false);
                }
            }
        }
    }
    
    public abstract static class AlphaPreCompositeContext_INT_PACK extends AlphaPreCompositeContext
    {
        AlphaPreCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        protected abstract void precompose_INT_PACK(final int p0, final int p1, final int[] p2, final int p3, final int p4, final int[] p5, final int p6, final int p7, final int[] p8, final int p9, final int p10);
        
        @Override
        protected void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            final int x0 = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int h = dstOut.getHeight();
            final SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
            final int srcScanStride = srcSPPSM.getScanlineStride();
            final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
            final int[] srcPixels = srcDB.getBankData()[0];
            final int srcBase = srcDB.getOffset() + srcSPPSM.getOffset(x0 - src.getSampleModelTranslateX(), y0 - src.getSampleModelTranslateY());
            final SinglePixelPackedSampleModel dstInSPPSM = (SinglePixelPackedSampleModel)dstIn.getSampleModel();
            final int dstInScanStride = dstInSPPSM.getScanlineStride();
            final DataBufferInt dstInDB = (DataBufferInt)dstIn.getDataBuffer();
            final int[] dstInPixels = dstInDB.getBankData()[0];
            final int dstInBase = dstInDB.getOffset() + dstInSPPSM.getOffset(x0 - dstIn.getSampleModelTranslateX(), y0 - dstIn.getSampleModelTranslateY());
            final SinglePixelPackedSampleModel dstOutSPPSM = (SinglePixelPackedSampleModel)dstOut.getSampleModel();
            final int dstOutScanStride = dstOutSPPSM.getScanlineStride();
            final DataBufferInt dstOutDB = (DataBufferInt)dstOut.getDataBuffer();
            final int[] dstOutPixels = dstOutDB.getBankData()[0];
            final int dstOutBase = dstOutDB.getOffset() + dstOutSPPSM.getOffset(x0 - dstOut.getSampleModelTranslateX(), y0 - dstOut.getSampleModelTranslateY());
            final int srcAdjust = srcScanStride - w;
            final int dstInAdjust = dstInScanStride - w;
            final int dstOutAdjust = dstOutScanStride - w;
            this.precompose_INT_PACK(w, h, srcPixels, srcAdjust, srcBase, dstInPixels, dstInAdjust, dstInBase, dstOutPixels, dstOutAdjust, dstOutBase);
        }
    }
    
    public static class OverCompositeContext extends AlphaPreCompositeContext
    {
        OverCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int dstM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] + (dstPix[sp] * dstM + 8388608 >>> 24), ++sp, dstPix[sp] = srcPix[sp] + (dstPix[sp] * dstM + 8388608 >>> 24), ++sp, dstPix[sp] = srcPix[sp] + (dstPix[sp] * dstM + 8388608 >>> 24), ++sp) {
                    dstM = (255 - srcPix[sp + 3]) * 65793;
                    dstPix[sp] = srcPix[sp] + (dstPix[sp] * dstM + 8388608 >>> 24);
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class OverCompositeContext_NA extends AlphaPreCompositeContext
    {
        OverCompositeContext_NA(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int dstM;
                for (int srcSP = 0, dstSP = 0, end = w * 4; srcSP < end; ++srcSP, ++dstSP, dstPix[dstSP] = srcPix[srcSP] + (dstPix[dstSP] * dstM + 8388608 >>> 24), ++srcSP, ++dstSP, dstPix[dstSP] = srcPix[srcSP] + (dstPix[dstSP] * dstM + 8388608 >>> 24), srcSP += 2, ++dstSP) {
                    dstM = (255 - srcPix[srcSP + 3]) * 65793;
                    dstPix[dstSP] = srcPix[srcSP] + (dstPix[dstSP] * dstM + 8388608 >>> 24);
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class OverCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        OverCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcP;
                int dstInP;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = ((srcP & 0xFF000000) + ((dstInP >>> 24) * dstM + 8388608 & 0xFF000000) | (srcP & 0xFF0000) + (((dstInP >> 16 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 8) | (srcP & 0xFF00) + (((dstInP >> 8 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 16) | (srcP & 0xFF) + ((dstInP & 0xFF) * dstM + 8388608 >>> 24))) {
                    srcP = srcPixels[srcSp++];
                    dstInP = dstInPixels[dstInSp++];
                    dstM = (255 - (srcP >>> 24)) * 65793;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class OverCompositeContext_INT_PACK_NA extends AlphaPreCompositeContext_INT_PACK
    {
        OverCompositeContext_INT_PACK_NA(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcP;
                int dstInP;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = ((srcP & 0xFF0000) + (((dstInP >> 16 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 8) | (srcP & 0xFF00) + (((dstInP >> 8 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 16) | (srcP & 0xFF) + ((dstInP & 0xFF) * dstM + 8388608 >>> 24))) {
                    srcP = srcPixels[srcSp++];
                    dstInP = dstInPixels[dstInSp++];
                    dstM = (255 - (srcP >>> 24)) * 65793;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class OverCompositeContext_INT_PACK_UNPRE extends AlphaPreCompositeContext_INT_PACK
    {
        OverCompositeContext_INT_PACK_UNPRE(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
            if (srcCM.isAlphaPremultiplied()) {
                throw new IllegalArgumentException("OverCompositeContext_INT_PACK_UNPRE is only forsources with unpremultiplied alpha");
            }
        }
        
        @Override
        public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            ColorModel dstPreCM = this.dstCM;
            if (!this.dstCM.isAlphaPremultiplied()) {
                dstPreCM = GraphicsUtil.coerceData((WritableRaster)dstIn, this.dstCM, true);
            }
            this.precompose(src, dstIn, dstOut);
            if (!this.dstCM.isAlphaPremultiplied()) {
                GraphicsUtil.coerceData(dstOut, dstPreCM, false);
                if (dstIn != dstOut) {
                    GraphicsUtil.coerceData((WritableRaster)dstIn, dstPreCM, false);
                }
            }
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcP;
                int dstP;
                int srcM;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (((srcP & 0xFF000000) + (dstP >>> 24) * dstM + 8388608 & 0xFF000000) | ((srcP >> 16 & 0xFF) * srcM + (dstP >> 16 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 8 | ((srcP >> 8 & 0xFF) * srcM + (dstP >> 8 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 16 | (srcP & 0xFF) * srcM + (dstP & 0xFF) * dstM + 8388608 >>> 24)) {
                    srcP = srcPixels[srcSp++];
                    dstP = dstInPixels[dstInSp++];
                    srcM = (srcP >>> 24) * 65793;
                    dstM = (255 - (srcP >>> 24)) * 65793;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class InCompositeContext extends AlphaPreCompositeContext
    {
        InCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int srcM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp) {
                    srcM = dstPix[sp + 3] * 65793;
                    dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24;
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class InCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        InCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcM;
                int srcP;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (((srcP >>> 24) * srcM + 8388608 & 0xFF000000) | ((srcP >> 16 & 0xFF) * srcM + 8388608 & 0xFF000000) >>> 8 | ((srcP >> 8 & 0xFF) * srcM + 8388608 & 0xFF000000) >>> 16 | (srcP & 0xFF) * srcM + 8388608 >>> 24)) {
                    srcM = (dstInPixels[dstInSp++] >>> 24) * 65793;
                    srcP = srcPixels[srcSp++];
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class OutCompositeContext extends AlphaPreCompositeContext
    {
        OutCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int srcM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24, ++sp) {
                    srcM = (255 - dstPix[sp + 3]) * 65793;
                    dstPix[sp] = srcPix[sp] * srcM + 8388608 >>> 24;
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class OutCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        OutCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcM;
                int srcP;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (((srcP >>> 24) * srcM + 8388608 & 0xFF000000) | ((srcP >> 16 & 0xFF) * srcM + 8388608 & 0xFF000000) >>> 8 | ((srcP >> 8 & 0xFF) * srcM + 8388608 & 0xFF000000) >>> 16 | (srcP & 0xFF) * srcM + 8388608 >>> 24)) {
                    srcM = (255 - (dstInPixels[dstInSp++] >>> 24)) * 65793;
                    srcP = srcPixels[srcSp++];
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class AtopCompositeContext extends AlphaPreCompositeContext
    {
        AtopCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int srcM;
                int dstM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24, sp += 2) {
                    srcM = dstPix[sp + 3] * 65793;
                    dstM = (255 - srcPix[sp + 3]) * 65793;
                    dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24;
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class AtopCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        AtopCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcP;
                int dstP;
                int srcM;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = ((dstP & 0xFF000000) | ((srcP >> 16 & 0xFF) * srcM + (dstP >> 16 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 8 | ((srcP >> 8 & 0xFF) * srcM + (dstP >> 8 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 16 | (srcP & 0xFF) * srcM + (dstP & 0xFF) * dstM + 8388608 >>> 24)) {
                    srcP = srcPixels[srcSp++];
                    dstP = dstInPixels[dstInSp++];
                    srcM = (dstP >>> 24) * 65793;
                    dstM = (255 - (srcP >>> 24)) * 65793;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class XorCompositeContext extends AlphaPreCompositeContext
    {
        XorCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int srcM;
                int dstM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24, ++sp) {
                    srcM = (255 - dstPix[sp + 3]) * 65793;
                    dstM = (255 - srcPix[sp + 3]) * 65793;
                    dstPix[sp] = srcPix[sp] * srcM + dstPix[sp] * dstM + 8388608 >>> 24;
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class XorCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        XorCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcP;
                int dstP;
                int srcM;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (((srcP >>> 24) * srcM + (dstP >>> 24) * dstM + 8388608 & 0xFF000000) | ((srcP >> 16 & 0xFF) * srcM + (dstP >> 16 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 8 | ((srcP >> 8 & 0xFF) * srcM + (dstP >> 8 & 0xFF) * dstM + 8388608 & 0xFF000000) >>> 16 | (srcP & 0xFF) * srcM + (dstP & 0xFF) * dstM + 8388608 >>> 24)) {
                    srcP = srcPixels[srcSp++];
                    dstP = dstInPixels[dstInSp++];
                    srcM = (255 - (dstP >>> 24)) * 65793;
                    dstM = (255 - (srcP >>> 24)) * 65793;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class ArithCompositeContext extends AlphaPreCompositeContext
    {
        float k1;
        float k2;
        float k3;
        float k4;
        
        ArithCompositeContext(final ColorModel srcCM, final ColorModel dstCM, final float k1, final float k2, final float k3, final float k4) {
            super(srcCM, dstCM);
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.k4 = k4;
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int bands = dstOut.getNumBands();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final float kk1 = this.k1 / 255.0f;
            final float kk2 = this.k4 * 255.0f + 0.5f;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                for (int i = 0; i < srcPix.length; ++i) {
                    int max = 0;
                    for (int b = 1; b < bands; ++b, ++i) {
                        int val = (int)(kk1 * srcPix[i] * dstPix[i] + this.k2 * srcPix[i] + this.k3 * dstPix[i] + kk2);
                        if ((val & 0xFFFFFF00) != 0x0) {
                            if ((val & Integer.MIN_VALUE) != 0x0) {
                                val = 0;
                            }
                            else {
                                val = 255;
                            }
                        }
                        if (val > max) {
                            max = val;
                        }
                        dstPix[i] = val;
                    }
                    int val = (int)(kk1 * srcPix[i] * dstPix[i] + this.k2 * srcPix[i] + this.k3 * dstPix[i] + kk2);
                    if ((val & 0xFFFFFF00) != 0x0) {
                        if ((val & Integer.MIN_VALUE) != 0x0) {
                            val = 0;
                        }
                        else {
                            val = 255;
                        }
                    }
                    if (val > max) {
                        dstPix[i] = val;
                    }
                    else {
                        dstPix[i] = max;
                    }
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class ArithCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        float k1;
        float k2;
        float k3;
        float k4;
        
        ArithCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM, final float k1, final float k2, final float k3, final float k4) {
            super(srcCM, dstCM);
            this.k1 = k1 / 255.0f;
            this.k2 = k2;
            this.k3 = k3;
            this.k4 = k4 * 255.0f + 0.5f;
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            for (int y = 0; y < height; ++y) {
                int a;
                int r;
                int g;
                int b;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (a << 24 | r << 16 | g << 8 | b)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    a = (int)((srcP >>> 24) * (dstP >>> 24) * this.k1 + (srcP >>> 24) * this.k2 + (dstP >>> 24) * this.k3 + this.k4);
                    if ((a & 0xFFFFFF00) != 0x0) {
                        if ((a & Integer.MIN_VALUE) != 0x0) {
                            a = 0;
                        }
                        else {
                            a = 255;
                        }
                    }
                    r = (int)((srcP >> 16 & 0xFF) * (dstP >> 16 & 0xFF) * this.k1 + (srcP >> 16 & 0xFF) * this.k2 + (dstP >> 16 & 0xFF) * this.k3 + this.k4);
                    if ((r & 0xFFFFFF00) != 0x0) {
                        if ((r & Integer.MIN_VALUE) != 0x0) {
                            r = 0;
                        }
                        else {
                            r = 255;
                        }
                    }
                    if (a < r) {
                        a = r;
                    }
                    g = (int)((srcP >> 8 & 0xFF) * (dstP >> 8 & 0xFF) * this.k1 + (srcP >> 8 & 0xFF) * this.k2 + (dstP >> 8 & 0xFF) * this.k3 + this.k4);
                    if ((g & 0xFFFFFF00) != 0x0) {
                        if ((g & Integer.MIN_VALUE) != 0x0) {
                            g = 0;
                        }
                        else {
                            g = 255;
                        }
                    }
                    if (a < g) {
                        a = g;
                    }
                    b = (int)((srcP & 0xFF) * (dstP & 0xFF) * this.k1 + (srcP & 0xFF) * this.k2 + (dstP & 0xFF) * this.k3 + this.k4);
                    if ((b & 0xFFFFFF00) != 0x0) {
                        if ((b & Integer.MIN_VALUE) != 0x0) {
                            b = 0;
                        }
                        else {
                            b = 255;
                        }
                    }
                    if (a < b) {
                        a = b;
                    }
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class ArithCompositeContext_INT_PACK_LUT extends AlphaPreCompositeContext_INT_PACK
    {
        byte[] lut;
        
        ArithCompositeContext_INT_PACK_LUT(final ColorModel srcCM, final ColorModel dstCM, float k1, final float k2, final float k3, float k4) {
            super(srcCM, dstCM);
            k1 /= 255.0f;
            k4 = k4 * 255.0f + 0.5f;
            final int sz = 65536;
            this.lut = new byte[sz];
            for (int i = 0; i < sz; ++i) {
                int val = (int)((i >> 8) * (i & 0xFF) * k1 + (i >> 8) * k2 + (i & 0xFF) * k3 + k4);
                if ((val & 0xFFFFFF00) != 0x0) {
                    if ((val & Integer.MIN_VALUE) != 0x0) {
                        val = 0;
                    }
                    else {
                        val = 255;
                    }
                }
                this.lut[i] = (byte)val;
            }
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final byte[] workTbl = this.lut;
            for (int y = 0; y < height; ++y) {
                int a;
                int r;
                int g;
                int b;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (a << 24 | r << 16 | g << 8 | b)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    a = (0xFF & workTbl[(srcP >> 16 & 0xFF00) | dstP >>> 24]);
                    r = (0xFF & workTbl[(srcP >> 8 & 0xFF00) | (dstP >> 16 & 0xFF)]);
                    g = (0xFF & workTbl[(srcP & 0xFF00) | (dstP >> 8 & 0xFF)]);
                    b = (0xFF & workTbl[(srcP << 8 & 0xFF00) | (dstP & 0xFF)]);
                    if (r > a) {
                        a = r;
                    }
                    if (g > a) {
                        a = g;
                    }
                    if (b > a) {
                        a = b;
                    }
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class MultiplyCompositeContext extends AlphaPreCompositeContext
    {
        MultiplyCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int srcM;
                int dstM;
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = (srcPix[sp] * srcM + dstPix[sp] * dstM + srcPix[sp] * dstPix[sp]) * 65793 + 8388608 >>> 24, ++sp, dstPix[sp] = (srcPix[sp] * srcM + dstPix[sp] * dstM + srcPix[sp] * dstPix[sp]) * 65793 + 8388608 >>> 24, ++sp, dstPix[sp] = srcPix[sp] + dstPix[sp] - (dstPix[sp] * srcPix[sp] * 65793 + 8388608 >>> 24), ++sp) {
                    srcM = 255 - dstPix[sp + 3];
                    dstM = 255 - srcPix[sp + 3];
                    dstPix[sp] = (srcPix[sp] * srcM + dstPix[sp] * dstM + srcPix[sp] * dstPix[sp]) * 65793 + 8388608 >>> 24;
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class MultiplyCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        MultiplyCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcA;
                int dstA;
                int srcR;
                int dstR;
                int srcG;
                int dstG;
                int srcB;
                int dstB;
                int srcM;
                int dstM;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (((srcR * srcM + dstR * dstM + srcR * dstR) * 65793 + 8388608 & 0xFF000000) >>> 8 | ((srcG * srcM + dstG * dstM + srcG * dstG) * 65793 + 8388608 & 0xFF000000) >>> 16 | (srcB * srcM + dstB * dstM + srcB * dstB) * 65793 + 8388608 >>> 24 | srcA + dstA - (srcA * dstA * 65793 + 8388608 >>> 24) << 24)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    srcA = srcP >>> 24;
                    dstA = dstP >>> 24;
                    srcR = (srcP >> 16 & 0xFF);
                    dstR = (dstP >> 16 & 0xFF);
                    srcG = (srcP >> 8 & 0xFF);
                    dstG = (dstP >> 8 & 0xFF);
                    srcB = (srcP & 0xFF);
                    dstB = (dstP & 0xFF);
                    srcM = 255 - dstA;
                    dstM = 255 - srcA;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class ScreenCompositeContext extends AlphaPreCompositeContext
    {
        ScreenCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                int iSrcPix;
                int iDstPix;
                for (int sp = 0, end = w * 4; sp < end; ++sp, iSrcPix = srcPix[sp], iDstPix = dstPix[sp], dstPix[sp] = iSrcPix + iDstPix - (iDstPix * iSrcPix * 65793 + 8388608 >>> 24), ++sp, iSrcPix = srcPix[sp], iDstPix = dstPix[sp], dstPix[sp] = iSrcPix + iDstPix - (iDstPix * iSrcPix * 65793 + 8388608 >>> 24), ++sp, iSrcPix = srcPix[sp], iDstPix = dstPix[sp], dstPix[sp] = iSrcPix + iDstPix - (iDstPix * iSrcPix * 65793 + 8388608 >>> 24), ++sp) {
                    iSrcPix = srcPix[sp];
                    iDstPix = dstPix[sp];
                    dstPix[sp] = iSrcPix + iDstPix - (iDstPix * iSrcPix * 65793 + 8388608 >>> 24);
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class ScreenCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        ScreenCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int srcA;
                int dstA;
                int srcR;
                int dstR;
                int srcG;
                int dstG;
                int srcB;
                int dstB;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (srcR + dstR - (srcR * dstR * 65793 + 8388608 >>> 24) << 16 | srcG + dstG - (srcG * dstG * 65793 + 8388608 >>> 24) << 8 | srcB + dstB - (srcB * dstB * 65793 + 8388608 >>> 24) | srcA + dstA - (srcA * dstA * 65793 + 8388608 >>> 24) << 24)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    srcA = srcP >>> 24;
                    dstA = dstP >>> 24;
                    srcR = (srcP >> 16 & 0xFF);
                    dstR = (dstP >> 16 & 0xFF);
                    srcG = (srcP >> 8 & 0xFF);
                    dstG = (dstP >> 8 & 0xFF);
                    srcB = (srcP & 0xFF);
                    dstB = (dstP & 0xFF);
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class DarkenCompositeContext extends AlphaPreCompositeContext
    {
        DarkenCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] + dstPix[sp] - (dstPix[sp] * srcPix[sp] * 65793 + 8388608 >>> 24), ++sp) {
                    final int srcM = 255 - dstPix[sp + 3];
                    final int dstM = 255 - srcPix[sp + 3];
                    int t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    int t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t2;
                    }
                    else {
                        dstPix[sp] = t1;
                    }
                    ++sp;
                    t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t2;
                    }
                    else {
                        dstPix[sp] = t1;
                    }
                    ++sp;
                    t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t2;
                    }
                    else {
                        dstPix[sp] = t1;
                    }
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class DarkenCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        DarkenCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int dstA;
                int dstR;
                int dstG;
                int dstB;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (dstA << 24 | dstR << 16 | dstG << 8 | dstB)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    int srcV = srcP >>> 24;
                    int dstV = dstP >>> 24;
                    final int srcM = (255 - dstV) * 65793;
                    final int dstM = (255 - srcV) * 65793;
                    dstA = srcV + dstV - (srcV * dstV * 65793 + 8388608 >>> 24);
                    srcV = (srcP >> 16 & 0xFF);
                    dstV = (dstP >> 16 & 0xFF);
                    dstR = (srcM * srcV + 8388608 >>> 24) + dstV;
                    int tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstR > tmp) {
                        dstR = tmp;
                    }
                    srcV = (srcP >> 8 & 0xFF);
                    dstV = (dstP >> 8 & 0xFF);
                    dstG = (srcM * srcV + 8388608 >>> 24) + dstV;
                    tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstG > tmp) {
                        dstG = tmp;
                    }
                    srcV = (srcP & 0xFF);
                    dstV = (dstP & 0xFF);
                    dstB = (srcM * srcV + 8388608 >>> 24) + dstV;
                    tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstB > tmp) {
                        dstB = tmp;
                    }
                    dstA &= 0xFF;
                    dstR &= 0xFF;
                    dstG &= 0xFF;
                    dstB &= 0xFF;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
    
    public static class LightenCompositeContext extends AlphaPreCompositeContext
    {
        LightenCompositeContext(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            final int y2 = y0 + dstOut.getHeight();
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                for (int sp = 0, end = w * 4; sp < end; ++sp, dstPix[sp] = srcPix[sp] + dstPix[sp] - (dstPix[sp] * srcPix[sp] * 65793 + 8388608 >>> 24), ++sp) {
                    final int srcM = 255 - dstPix[sp + 3];
                    final int dstM = 255 - srcPix[sp + 3];
                    int t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    int t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t1;
                    }
                    else {
                        dstPix[sp] = t2;
                    }
                    ++sp;
                    t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t1;
                    }
                    else {
                        dstPix[sp] = t2;
                    }
                    ++sp;
                    t1 = (srcM * srcPix[sp] * 65793 + 8388608 >>> 24) + dstPix[sp];
                    t2 = (dstM * dstPix[sp] * 65793 + 8388608 >>> 24) + srcPix[sp];
                    if (t1 > t2) {
                        dstPix[sp] = t1;
                    }
                    else {
                        dstPix[sp] = t2;
                    }
                }
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
    
    public static class LightenCompositeContext_INT_PACK extends AlphaPreCompositeContext_INT_PACK
    {
        LightenCompositeContext_INT_PACK(final ColorModel srcCM, final ColorModel dstCM) {
            super(srcCM, dstCM);
        }
        
        public void precompose_INT_PACK(final int width, final int height, final int[] srcPixels, final int srcAdjust, int srcSp, final int[] dstInPixels, final int dstInAdjust, int dstInSp, final int[] dstOutPixels, final int dstOutAdjust, int dstOutSp) {
            final int norm = 65793;
            final int pt5 = 8388608;
            for (int y = 0; y < height; ++y) {
                int dstA;
                int dstR;
                int dstG;
                int dstB;
                for (int end = dstOutSp + width; dstOutSp < end; dstOutPixels[dstOutSp++] = (dstA << 24 | dstR << 16 | dstG << 8 | dstB)) {
                    final int srcP = srcPixels[srcSp++];
                    final int dstP = dstInPixels[dstInSp++];
                    int srcV = srcP >>> 24;
                    int dstV = dstP >>> 24;
                    final int srcM = (255 - dstV) * 65793;
                    final int dstM = (255 - srcV) * 65793;
                    dstA = srcV + dstV - (srcV * dstV * 65793 + 8388608 >>> 24);
                    srcV = (srcP >> 16 & 0xFF);
                    dstV = (dstP >> 16 & 0xFF);
                    dstR = (srcM * srcV + 8388608 >>> 24) + dstV;
                    int tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstR < tmp) {
                        dstR = tmp;
                    }
                    srcV = (srcP >> 8 & 0xFF);
                    dstV = (dstP >> 8 & 0xFF);
                    dstG = (srcM * srcV + 8388608 >>> 24) + dstV;
                    tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstG < tmp) {
                        dstG = tmp;
                    }
                    srcV = (srcP & 0xFF);
                    dstV = (dstP & 0xFF);
                    dstB = (srcM * srcV + 8388608 >>> 24) + dstV;
                    tmp = (dstM * dstV + 8388608 >>> 24) + srcV;
                    if (dstB < tmp) {
                        dstB = tmp;
                    }
                    dstA &= 0xFF;
                    dstR &= 0xFF;
                    dstG &= 0xFF;
                    dstB &= 0xFF;
                }
                srcSp += srcAdjust;
                dstInSp += dstInAdjust;
                dstOutSp += dstOutAdjust;
            }
        }
    }
}
