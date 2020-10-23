// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;

public interface TextLayoutFactory
{
    TextSpanLayout createTextLayout(final AttributedCharacterIterator p0, final int[] p1, final Point2D p2, final FontRenderContext p3);
}
