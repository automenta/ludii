// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

public class AvoidTilingHintKey extends RenderingHints.Key
{
    AvoidTilingHintKey(final int number) {
        super(number);
    }
    
    @Override
    public boolean isCompatibleValue(final Object v) {
        return v != null && (v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON || v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_OFF || v == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_DEFAULT);
    }
}
