// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.font;

import java.awt.geom.Point2D;
import java.awt.font.GlyphMetrics;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.geom.PathLength;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.font.GlyphVector;

public class TextPathLayout
{
    public static final int ALIGN_START = 0;
    public static final int ALIGN_MIDDLE = 1;
    public static final int ALIGN_END = 2;
    public static final int ADJUST_SPACING = 0;
    public static final int ADJUST_GLYPHS = 1;
    
    public static Shape layoutGlyphVector(final GlyphVector glyphs, final Shape path, final int align, final float startOffset, final float textLength, final int lengthAdjustMode) {
        final GeneralPath newPath = new GeneralPath();
        final PathLength pl = new PathLength(path);
        final float pathLength = pl.lengthOfPath();
        if (glyphs == null) {
            return newPath;
        }
        final float glyphsLength = (float)glyphs.getVisualBounds().getWidth();
        if (path == null || glyphs.getNumGlyphs() == 0 || pl.lengthOfPath() == 0.0f || glyphsLength == 0.0f) {
            return newPath;
        }
        final float lengthRatio = textLength / glyphsLength;
        float currentPosition = startOffset;
        if (align == 2) {
            currentPosition += pathLength - textLength;
        }
        else if (align == 1) {
            currentPosition += (pathLength - textLength) / 2.0f;
        }
        for (int i = 0; i < glyphs.getNumGlyphs(); ++i) {
            final GlyphMetrics gm = glyphs.getGlyphMetrics(i);
            float charAdvance = gm.getAdvance();
            Shape glyph = glyphs.getGlyphOutline(i);
            if (lengthAdjustMode == 1) {
                final AffineTransform scale = AffineTransform.getScaleInstance(lengthRatio, 1.0);
                glyph = scale.createTransformedShape(glyph);
                charAdvance *= lengthRatio;
            }
            final float glyphWidth = (float)glyph.getBounds2D().getWidth();
            final float charMidPos = currentPosition + glyphWidth / 2.0f;
            final Point2D charMidPoint = pl.pointAtLength(charMidPos);
            if (charMidPoint != null) {
                final float angle = pl.angleAtLength(charMidPos);
                final AffineTransform glyphTrans = new AffineTransform();
                glyphTrans.translate(charMidPoint.getX(), charMidPoint.getY());
                glyphTrans.rotate(angle);
                glyphTrans.translate(charAdvance / -2.0f, 0.0);
                glyph = glyphTrans.createTransformedShape(glyph);
                newPath.append(glyph, false);
            }
            if (lengthAdjustMode == 0) {
                currentPosition += charAdvance * lengthRatio;
            }
            else {
                currentPosition += charAdvance;
            }
        }
        return newPath;
    }
    
    public static Shape layoutGlyphVector(final GlyphVector glyphs, final Shape path, final int align) {
        return layoutGlyphVector(glyphs, path, align, 0.0f, (float)glyphs.getVisualBounds().getWidth(), 0);
    }
    
    public static Shape layoutGlyphVector(final GlyphVector glyphs, final Shape path) {
        return layoutGlyphVector(glyphs, path, 0);
    }
}
