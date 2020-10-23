// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.LookupTable;
import java.awt.image.ByteLookupTable;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.TransferFunction;
import java.awt.image.LookupOp;

public class ComponentTransferRed extends AbstractRed
{
    LookupOp operation;
    
    public ComponentTransferRed(final CachableRed src, final TransferFunction[] funcs, final RenderingHints hints) {
        super(src, src.getBounds(), GraphicsUtil.coerceColorModel(src.getColorModel(), false), src.getSampleModel(), null);
        final byte[][] tableData = { funcs[1].getLookupTable(), funcs[2].getLookupTable(), funcs[3].getLookupTable(), funcs[0].getLookupTable() };
        this.operation = new LookupOp(new ByteLookupTable(0, tableData), hints) {};
    }
    
    @Override
    public WritableRaster copyData(WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        wr = src.copyData(wr);
        GraphicsUtil.coerceData(wr, src.getColorModel(), false);
        final WritableRaster srcWR = wr.createWritableTranslatedChild(0, 0);
        this.operation.filter(srcWR, srcWR);
        return wr;
    }
}
