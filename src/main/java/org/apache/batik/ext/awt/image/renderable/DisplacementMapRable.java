// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.ARGBChannel;
import java.util.List;

public interface DisplacementMapRable extends FilterColorInterpolation
{
    public static final int CHANNEL_R = 1;
    public static final int CHANNEL_G = 2;
    public static final int CHANNEL_B = 3;
    public static final int CHANNEL_A = 4;
    
    void setSources(final List p0);
    
    void setScale(final double p0);
    
    double getScale();
    
    void setXChannelSelector(final ARGBChannel p0);
    
    ARGBChannel getXChannelSelector();
    
    void setYChannelSelector(final ARGBChannel p0);
    
    ARGBChannel getYChannelSelector();
}
