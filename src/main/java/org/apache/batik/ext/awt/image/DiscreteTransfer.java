// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

public class DiscreteTransfer implements TransferFunction
{
    public byte[] lutData;
    public int[] tableValues;
    private int n;
    
    public DiscreteTransfer(final int[] tableValues) {
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }
    
    private void buildLutData() {
        this.lutData = new byte[256];
        for (int j = 0; j <= 255; ++j) {
            int i = (int)Math.floor(j * this.n / 255.0f);
            if (i == this.n) {
                i = this.n - 1;
            }
            this.lutData[j] = (byte)(this.tableValues[i] & 0xFF);
        }
    }
    
    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}
