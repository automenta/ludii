// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.io.Serializable;

public final class ARGBChannel implements Serializable
{
    public static final int CHANNEL_A = 3;
    public static final int CHANNEL_R = 2;
    public static final int CHANNEL_G = 1;
    public static final int CHANNEL_B = 0;
    public static final String RED = "Red";
    public static final String GREEN = "Green";
    public static final String BLUE = "Blue";
    public static final String ALPHA = "Alpha";
    public static final ARGBChannel R;
    public static final ARGBChannel G;
    public static final ARGBChannel B;
    public static final ARGBChannel A;
    private String desc;
    private int val;
    
    private ARGBChannel(final int val, final String desc) {
        this.desc = desc;
        this.val = val;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
    
    public int toInt() {
        return this.val;
    }
    
    public Object readResolve() {
        switch (this.val) {
            case 2: {
                return ARGBChannel.R;
            }
            case 1: {
                return ARGBChannel.G;
            }
            case 0: {
                return ARGBChannel.B;
            }
            case 3: {
                return ARGBChannel.A;
            }
            default: {
                throw new RuntimeException("Unknown ARGBChannel value");
            }
        }
    }
    
    static {
        R = new ARGBChannel(2, "Red");
        G = new ARGBChannel(1, "Green");
        B = new ARGBChannel(0, "Blue");
        A = new ARGBChannel(3, "Alpha");
    }
}
