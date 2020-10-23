// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import java.util.Map;

public class FilterAlphaRed extends AbstractRed
{
    public FilterAlphaRed(final CachableRed src) {
        super(src, src.getBounds(), src.getColorModel(), src.getSampleModel(), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed srcRed = this.getSources().get(0);
        final SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1) {
            return srcRed.copyData(wr);
        }
        PadRed.ZeroRecter.zeroRect(wr);
        final Raster srcRas = srcRed.getData(wr.getBounds());
        AbstractRed.copyBand(srcRas, srcRas.getNumBands() - 1, wr, wr.getNumBands() - 1);
        return wr;
    }
}
