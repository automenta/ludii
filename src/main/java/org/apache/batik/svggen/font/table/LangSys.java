// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LangSys
{
    private int lookupOrder;
    private int reqFeatureIndex;
    private int featureCount;
    private int[] featureIndex;
    
    protected LangSys(final RandomAccessFile raf) throws IOException {
        this.lookupOrder = raf.readUnsignedShort();
        this.reqFeatureIndex = raf.readUnsignedShort();
        this.featureCount = raf.readUnsignedShort();
        this.featureIndex = new int[this.featureCount];
        for (int i = 0; i < this.featureCount; ++i) {
            this.featureIndex[i] = raf.readUnsignedShort();
        }
    }
    
    protected boolean isFeatureIndexed(final int n) {
        for (int i = 0; i < this.featureCount; ++i) {
            if (this.featureIndex[i] == n) {
                return true;
            }
        }
        return false;
    }
}
