// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

public class IdentityTransfer implements TransferFunction
{
    public static byte[] lutData;
    
    @Override
    public byte[] getLookupTable() {
        return IdentityTransfer.lutData;
    }
    
    static {
        IdentityTransfer.lutData = new byte[256];
        for (int j = 0; j <= 255; ++j) {
            IdentityTransfer.lutData[j] = (byte)j;
        }
    }
}
