// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.FontRenderContext;

public interface GVTGlyphVector
{
    GVTFont getFont();
    
    FontRenderContext getFontRenderContext();
    
    int getGlyphCode(final int p0);
    
    int[] getGlyphCodes(final int p0, final int p1, final int[] p2);
    
    GlyphJustificationInfo getGlyphJustificationInfo(final int p0);
    
    Shape getGlyphLogicalBounds(final int p0);
    
    GVTGlyphMetrics getGlyphMetrics(final int p0);
    
    Shape getGlyphOutline(final int p0);
    
    Rectangle2D getGlyphCellBounds(final int p0);
    
    Point2D getGlyphPosition(final int p0);
    
    float[] getGlyphPositions(final int p0, final int p1, final float[] p2);
    
    AffineTransform getGlyphTransform(final int p0);
    
    Shape getGlyphVisualBounds(final int p0);
    
    Rectangle2D getLogicalBounds();
    
    int getNumGlyphs();
    
    Shape getOutline();
    
    Shape getOutline(final float p0, final float p1);
    
    Rectangle2D getGeometricBounds();
    
    Rectangle2D getBounds2D(final AttributedCharacterIterator p0);
    
    void performDefaultLayout();
    
    void setGlyphPosition(final int p0, final Point2D p1);
    
    void setGlyphTransform(final int p0, final AffineTransform p1);
    
    void setGlyphVisible(final int p0, final boolean p1);
    
    boolean isGlyphVisible(final int p0);
    
    int getCharacterCount(final int p0, final int p1);
    
    boolean isReversed();
    
    void maybeReverse(final boolean p0);
    
    void draw(final Graphics2D p0, final AttributedCharacterIterator p1);
}
