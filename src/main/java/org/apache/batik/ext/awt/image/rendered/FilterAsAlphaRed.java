// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import java.util.Map;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;

public class FilterAsAlphaRed extends AbstractRed
{
    public FilterAsAlphaRed(final CachableRed src) {
        super(new Any2LumRed(src), src.getBounds(), new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0), new PixelInterleavedSampleModel(0, src.getSampleModel().getWidth(), src.getSampleModel().getHeight(), 1, src.getSampleModel().getWidth(), new int[] { 0 }), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed srcRed = this.getSources().get(0);
        final SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1) {
            return srcRed.copyData(wr);
        }
        final Raster srcRas = srcRed.getData(wr.getBounds());
        final PixelInterleavedSampleModel srcSM = (PixelInterleavedSampleModel)srcRas.getSampleModel();
        final DataBufferByte srcDB = (DataBufferByte)srcRas.getDataBuffer();
        final byte[] src = srcDB.getData();
        final PixelInterleavedSampleModel dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();
        final DataBufferByte dstDB = (DataBufferByte)wr.getDataBuffer();
        final byte[] dst = dstDB.getData();
        final int srcX0 = srcRas.getMinX() - srcRas.getSampleModelTranslateX();
        int srcY0 = srcRas.getMinY() - srcRas.getSampleModelTranslateY();
        final int dstX0 = wr.getMinX() - wr.getSampleModelTranslateX();
        final int dstX2 = dstX0 + wr.getWidth() - 1;
        int dstY0 = wr.getMinY() - wr.getSampleModelTranslateY();
        final int srcStep = srcSM.getPixelStride();
        final int[] offsets = srcSM.getBandOffsets();
        final int srcLOff = offsets[0];
        int srcAOff = offsets[1];
        if (srcRed.getColorModel().isAlphaPremultiplied()) {
            for (int y = 0; y < srcRas.getHeight(); ++y) {
                int srcI;
                int dstI;
                int dstE;
                for (srcI = srcDB.getOffset() + srcSM.getOffset(srcX0, srcY0), dstI = dstDB.getOffset() + dstSM.getOffset(dstX0, dstY0), dstE = dstDB.getOffset() + dstSM.getOffset(dstX2 + 1, dstY0), srcI += srcLOff; dstI < dstE; dst[dstI++] = src[srcI], srcI += srcStep) {}
                ++srcY0;
                ++dstY0;
            }
        }
        else {
            srcAOff -= srcLOff;
            for (int y = 0; y < srcRas.getHeight(); ++y) {
                int srcI;
                int dstI;
                int dstE;
                int sl;
                int sa;
                for (srcI = srcDB.getOffset() + srcSM.getOffset(srcX0, srcY0), dstI = dstDB.getOffset() + dstSM.getOffset(dstX0, dstY0), dstE = dstDB.getOffset() + dstSM.getOffset(dstX2 + 1, dstY0), srcI += srcLOff; dstI < dstE; dst[dstI++] = (byte)(sl * sa + 128 >> 8), srcI += srcStep) {
                    sl = (src[srcI] & 0xFF);
                    sa = (src[srcI + srcAOff] & 0xFF);
                }
                ++srcY0;
                ++dstY0;
            }
        }
        return wr;
    }
}
