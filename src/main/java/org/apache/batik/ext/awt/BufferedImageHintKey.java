// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.image.BufferedImage;
import java.lang.ref.Reference;
import java.awt.RenderingHints;

final class BufferedImageHintKey extends RenderingHints.Key
{
    BufferedImageHintKey(final int number) {
        super(number);
    }
    
    @Override
    public boolean isCompatibleValue(Object val) {
        if (val == null) {
            return true;
        }
        if (!(val instanceof Reference)) {
            return false;
        }
        final Reference ref = (Reference)val;
        val = ref.get();
        return val == null || val instanceof BufferedImage;
    }
}
