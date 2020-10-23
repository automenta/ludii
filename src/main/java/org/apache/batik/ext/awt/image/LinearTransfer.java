// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

public class LinearTransfer implements TransferFunction
{
    public byte[] lutData;
    public float slope;
    public float intercept;
    
    public LinearTransfer(final float slope, final float intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }
    
    private void buildLutData() {
        this.lutData = new byte[256];
        final float scaledInt = this.intercept * 255.0f + 0.5f;
        for (int j = 0; j <= 255; ++j) {
            int value = (int)(this.slope * j + scaledInt);
            if (value < 0) {
                value = 0;
            }
            else if (value > 255) {
                value = 255;
            }
            this.lutData[j] = (byte)(0xFF & value);
        }
    }
    
    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}
