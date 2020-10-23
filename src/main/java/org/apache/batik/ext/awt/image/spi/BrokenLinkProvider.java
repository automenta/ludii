// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.awt.Image;
import org.apache.batik.ext.awt.image.renderable.Filter;

public abstract class BrokenLinkProvider
{
    public static final String BROKEN_LINK_PROPERTY = "org.apache.batik.BrokenLinkImage";
    
    public abstract Filter getBrokenLinkImage(final Object p0, final String p1, final Object[] p2);
    
    public static boolean hasBrokenLinkProperty(final Filter f) {
        final Object o = f.getProperty("org.apache.batik.BrokenLinkImage");
        return o != null && o != Image.UndefinedProperty;
    }
}
