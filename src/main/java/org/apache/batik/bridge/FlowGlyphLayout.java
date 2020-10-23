// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;

public class FlowGlyphLayout extends GlyphLayout
{
    public static final char SOFT_HYPHEN = '\u00ad';
    public static final char ZERO_WIDTH_SPACE = '\u200b';
    public static final char ZERO_WIDTH_JOINER = '\u200d';
    public static final char SPACE = ' ';
    
    public FlowGlyphLayout(final AttributedCharacterIterator aci, final int[] charMap, final Point2D offset, final FontRenderContext frc) {
        super(aci, charMap, offset, frc);
    }
}
