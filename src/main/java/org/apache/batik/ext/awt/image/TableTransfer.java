// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

public class TableTransfer implements TransferFunction
{
    public byte[] lutData;
    public int[] tableValues;
    private int n;
    
    public TableTransfer(final int[] tableValues) {
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }
    
    private void buildLutData() {
        this.lutData = new byte[256];
        for (int j = 0; j <= 255; ++j) {
            final float fi = j * (this.n - 1) / 255.0f;
            final int ffi = (int)Math.floor(fi);
            final int cfi = (ffi + 1 > this.n - 1) ? (this.n - 1) : (ffi + 1);
            final float r = fi - ffi;
            this.lutData[j] = (byte)((int)(this.tableValues[ffi] + r * (this.tableValues[cfi] - this.tableValues[ffi])) & 0xFF);
        }
    }
    
    @Override
    public byte[] getLookupTable() {
        this.buildLutData();
        return this.lutData;
    }
}
