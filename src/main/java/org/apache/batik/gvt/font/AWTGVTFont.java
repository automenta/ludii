// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.util.HashMap;
import java.awt.geom.Rectangle2D;
import java.awt.font.GlyphMetrics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.apache.batik.gvt.text.ArabicTextHandler;
import java.awt.font.GlyphVector;
import java.text.StringCharacterIterator;
import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.awt.Font;

public class AWTGVTFont implements GVTFont
{
    protected Font awtFont;
    protected double size;
    protected double scale;
    public static final float FONT_SIZE = 48.0f;
    static Map fontCache;
    
    public AWTGVTFont(final Font font) {
        this.size = font.getSize2D();
        this.awtFont = font.deriveFont(48.0f);
        this.scale = this.size / this.awtFont.getSize2D();
        initializeFontCache(this.awtFont);
    }
    
    public AWTGVTFont(final Font font, final double scale) {
        this.size = font.getSize2D() * scale;
        this.awtFont = font.deriveFont(48.0f);
        this.scale = this.size / this.awtFont.getSize2D();
        initializeFontCache(this.awtFont);
    }
    
    public AWTGVTFont(final Map attributes) {
        final Float sz = attributes.get(TextAttribute.SIZE);
        if (sz != null) {
            this.size = sz;
            attributes.put(TextAttribute.SIZE, 48.0f);
            this.awtFont = new Font(attributes);
        }
        else {
            this.awtFont = new Font(attributes);
            this.size = this.awtFont.getSize2D();
        }
        this.scale = this.size / this.awtFont.getSize2D();
        initializeFontCache(this.awtFont);
    }
    
    public AWTGVTFont(final String name, final int style, final int size) {
        this.awtFont = new Font(name, style, 48);
        this.size = size;
        this.scale = size / this.awtFont.getSize2D();
        initializeFontCache(this.awtFont);
    }
    
    @Override
    public boolean canDisplay(final char c) {
        return this.awtFont.canDisplay(c);
    }
    
    @Override
    public int canDisplayUpTo(final char[] text, final int start, final int limit) {
        return this.awtFont.canDisplayUpTo(text, start, limit);
    }
    
    @Override
    public int canDisplayUpTo(final CharacterIterator iter, final int start, final int limit) {
        return this.awtFont.canDisplayUpTo(iter, start, limit);
    }
    
    @Override
    public int canDisplayUpTo(final String str) {
        return this.awtFont.canDisplayUpTo(str);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final char[] chars) {
        final StringCharacterIterator sci = new StringCharacterIterator(new String(chars));
        final GlyphVector gv = this.awtFont.createGlyphVector(frc, chars);
        return new AWTGVTGlyphVector(gv, this, this.scale, sci);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final CharacterIterator ci) {
        if (ci instanceof AttributedCharacterIterator) {
            final AttributedCharacterIterator aci = (AttributedCharacterIterator)ci;
            if (ArabicTextHandler.containsArabic(aci)) {
                final String str = ArabicTextHandler.createSubstituteString(aci);
                return this.createGlyphVector(frc, str);
            }
        }
        final GlyphVector gv = this.awtFont.createGlyphVector(frc, ci);
        return new AWTGVTGlyphVector(gv, this, this.scale, ci);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final int[] glyphCodes, final CharacterIterator ci) {
        return new AWTGVTGlyphVector(this.awtFont.createGlyphVector(frc, glyphCodes), this, this.scale, ci);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final String str) {
        final StringCharacterIterator sci = new StringCharacterIterator(str);
        return new AWTGVTGlyphVector(this.awtFont.createGlyphVector(frc, str), this, this.scale, sci);
    }
    
    @Override
    public GVTFont deriveFont(final float size) {
        return new AWTGVTFont(this.awtFont, size / this.size);
    }
    
    @Override
    public String getFamilyName() {
        return this.awtFont.getFamily();
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final char[] chars, final int beginIndex, final int limit, final FontRenderContext frc) {
        return new GVTLineMetrics(this.awtFont.getLineMetrics(chars, beginIndex, limit, frc), (float)this.scale);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final CharacterIterator ci, final int beginIndex, final int limit, final FontRenderContext frc) {
        return new GVTLineMetrics(this.awtFont.getLineMetrics(ci, beginIndex, limit, frc), (float)this.scale);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final String str, final FontRenderContext frc) {
        return new GVTLineMetrics(this.awtFont.getLineMetrics(str, frc), (float)this.scale);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final String str, final int beginIndex, final int limit, final FontRenderContext frc) {
        return new GVTLineMetrics(this.awtFont.getLineMetrics(str, beginIndex, limit, frc), (float)this.scale);
    }
    
    @Override
    public float getSize() {
        return (float)this.size;
    }
    
    @Override
    public float getHKern(final int glyphCode1, final int glyphCode2) {
        return 0.0f;
    }
    
    @Override
    public float getVKern(final int glyphCode1, final int glyphCode2) {
        return 0.0f;
    }
    
    public static AWTGlyphGeometryCache.Value getGlyphGeometry(final AWTGVTFont font, final char c, final GlyphVector gv, final int glyphIndex, final Point2D glyphPos) {
        final AWTGlyphGeometryCache glyphCache = AWTGVTFont.fontCache.get(font.awtFont);
        AWTGlyphGeometryCache.Value v = glyphCache.get(c);
        if (v == null) {
            Shape outline = gv.getGlyphOutline(glyphIndex);
            final GlyphMetrics metrics = gv.getGlyphMetrics(glyphIndex);
            final Rectangle2D gmB = metrics.getBounds2D();
            if (AWTGVTGlyphVector.outlinesPositioned()) {
                final AffineTransform tr = AffineTransform.getTranslateInstance(-glyphPos.getX(), -glyphPos.getY());
                outline = tr.createTransformedShape(outline);
            }
            v = new AWTGlyphGeometryCache.Value(outline, gmB);
            glyphCache.put(c, v);
        }
        return v;
    }
    
    static void initializeFontCache(final Font awtFont) {
        if (!AWTGVTFont.fontCache.containsKey(awtFont)) {
            AWTGVTFont.fontCache.put(awtFont, new AWTGlyphGeometryCache());
        }
    }
    
    static void putAWTGVTFont(final AWTGVTFont font) {
        AWTGVTFont.fontCache.put(font.awtFont, font);
    }
    
    static AWTGVTFont getAWTGVTFont(final Font awtFont) {
        return AWTGVTFont.fontCache.get(awtFont);
    }
    
    static {
        AWTGVTFont.fontCache = new HashMap(11);
    }
}
