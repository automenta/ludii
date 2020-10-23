// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

public final class ColorSpaceHintKey extends RenderingHints.Key
{
    public static Object VALUE_COLORSPACE_ARGB;
    public static Object VALUE_COLORSPACE_RGB;
    public static Object VALUE_COLORSPACE_GREY;
    public static Object VALUE_COLORSPACE_AGREY;
    public static Object VALUE_COLORSPACE_ALPHA;
    public static Object VALUE_COLORSPACE_ALPHA_CONVERT;
    public static final String PROPERTY_COLORSPACE = "org.apache.batik.gvt.filter.Colorspace";
    
    ColorSpaceHintKey(final int number) {
        super(number);
    }
    
    @Override
    public boolean isCompatibleValue(final Object val) {
        return val == ColorSpaceHintKey.VALUE_COLORSPACE_ARGB || val == ColorSpaceHintKey.VALUE_COLORSPACE_RGB || val == ColorSpaceHintKey.VALUE_COLORSPACE_GREY || val == ColorSpaceHintKey.VALUE_COLORSPACE_AGREY || val == ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA || val == ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA_CONVERT;
    }
    
    static {
        ColorSpaceHintKey.VALUE_COLORSPACE_ARGB = new Object();
        ColorSpaceHintKey.VALUE_COLORSPACE_RGB = new Object();
        ColorSpaceHintKey.VALUE_COLORSPACE_GREY = new Object();
        ColorSpaceHintKey.VALUE_COLORSPACE_AGREY = new Object();
        ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA = new Object();
        ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA_CONVERT = new Object();
    }
}
