// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;

public class FlowTextLayoutFactory implements TextLayoutFactory
{
    @Override
    public TextSpanLayout createTextLayout(final AttributedCharacterIterator aci, final int[] charMap, final Point2D offset, final FontRenderContext frc) {
        return new FlowGlyphLayout(aci, charMap, offset, frc);
    }
}
