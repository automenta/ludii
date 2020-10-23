// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.Shape;
import java.awt.RenderingHints;

final class AreaOfInterestHintKey extends RenderingHints.Key
{
    AreaOfInterestHintKey(final int number) {
        super(number);
    }
    
    @Override
    public boolean isCompatibleValue(final Object val) {
        boolean isCompatible = true;
        if (val != null && !(val instanceof Shape)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}
