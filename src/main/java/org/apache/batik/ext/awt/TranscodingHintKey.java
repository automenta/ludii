// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

final class TranscodingHintKey extends RenderingHints.Key
{
    TranscodingHintKey(final int number) {
        super(number);
    }
    
    @Override
    public boolean isCompatibleValue(final Object val) {
        boolean isCompatible = true;
        if (val != null && !(val instanceof String)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}
