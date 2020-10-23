// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.keys;

import java.awt.geom.Rectangle2D;
import org.apache.batik.transcoder.TranscodingHints;

public class Rectangle2DKey extends TranscodingHints.Key
{
    @Override
    public boolean isCompatibleValue(final Object v) {
        return v instanceof Rectangle2D;
    }
}
