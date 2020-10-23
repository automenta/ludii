// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Graphics2D;

public interface TextSpanLayout
{
    public static final int DECORATION_UNDERLINE = 1;
    public static final int DECORATION_STRIKETHROUGH = 2;
    public static final int DECORATION_OVERLINE = 4;
    public static final int DECORATION_ALL = 7;
    
    void draw(final Graphics2D p0);
    
    Shape getDecorationOutline(final int p0);
    
    Rectangle2D getBounds2D();
    
    Rectangle2D getGeometricBounds();
    
    Shape getOutline();
    
    Point2D getAdvance2D();
    
    float[] getGlyphAdvances();
    
    GVTGlyphMetrics getGlyphMetrics(final int p0);
    
    GVTLineMetrics getLineMetrics();
    
    Point2D getTextPathAdvance();
    
    Point2D getOffset();
    
    void setScale(final float p0, final float p1, final boolean p2);
    
    void setOffset(final Point2D p0);
    
    Shape getHighlightShape(final int p0, final int p1);
    
    TextHit hitTestChar(final float p0, final float p1);
    
    boolean isVertical();
    
    boolean isOnATextPath();
    
    int getGlyphCount();
    
    int getCharacterCount(final int p0, final int p1);
    
    int getGlyphIndex(final int p0);
    
    boolean isLeftToRight();
    
    boolean hasCharacterIndex(final int p0);
    
    GVTGlyphVector getGlyphVector();
    
    double getComputedOrientationAngle(final int p0);
    
    boolean isAltGlyph();
    
    boolean isReversed();
    
    void maybeReverse(final boolean p0);
}
