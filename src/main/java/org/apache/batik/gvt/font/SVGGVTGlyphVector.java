// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.awt.Graphics2D;
import org.apache.batik.gvt.text.ArabicTextHandler;
import java.awt.geom.AffineTransform;
import java.awt.font.GlyphJustificationInfo;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

public final class SVGGVTGlyphVector implements GVTGlyphVector
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    private GVTFont font;
    private Glyph[] glyphs;
    private FontRenderContext frc;
    private GeneralPath outline;
    private Rectangle2D logicalBounds;
    private Rectangle2D bounds2D;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;
    private Point2D endPos;
    private TextPaintInfo cacheTPI;
    
    public SVGGVTGlyphVector(final GVTFont font, final Glyph[] glyphs, final FontRenderContext frc) {
        this.font = font;
        this.glyphs = glyphs;
        this.frc = frc;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
        this.glyphLogicalBounds = new Shape[glyphs.length];
        this.glyphVisible = new boolean[glyphs.length];
        for (int i = 0; i < glyphs.length; ++i) {
            this.glyphVisible[i] = true;
        }
        this.endPos = glyphs[glyphs.length - 1].getPosition();
        this.endPos = new Point2D.Float((float)(this.endPos.getX() + glyphs[glyphs.length - 1].getHorizAdvX()), (float)this.endPos.getY());
    }
    
    @Override
    public GVTFont getFont() {
        return this.font;
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.frc;
    }
    
    @Override
    public int getGlyphCode(final int glyphIndex) throws IndexOutOfBoundsException {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex " + glyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        return this.glyphs[glyphIndex].getGlyphCode();
    }
    
    @Override
    public int[] getGlyphCodes(final int beginGlyphIndex, final int numEntries, int[] codeReturn) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, " + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        if (beginGlyphIndex + numEntries > this.glyphs.length) {
            throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries (" + beginGlyphIndex + "+" + numEntries + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (codeReturn == null) {
            codeReturn = new int[numEntries];
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            codeReturn[i - beginGlyphIndex] = this.glyphs[i].getGlyphCode();
        }
        return codeReturn;
    }
    
    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(final int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + ".");
        }
        return null;
    }
    
    @Override
    public Shape getGlyphLogicalBounds(final int glyphIndex) {
        if (this.glyphLogicalBounds[glyphIndex] == null && this.glyphVisible[glyphIndex]) {
            this.computeGlyphLogicalBounds();
        }
        return this.glyphLogicalBounds[glyphIndex];
    }
    
    private void computeGlyphLogicalBounds() {
        float ascent = 0.0f;
        float descent = 0.0f;
        if (this.font != null) {
            final GVTLineMetrics lineMetrics = this.font.getLineMetrics("By", this.frc);
            ascent = lineMetrics.getAscent();
            descent = lineMetrics.getDescent();
            if (descent < 0.0f) {
                descent = -descent;
            }
        }
        if (ascent == 0.0f) {
            float maxAscent = 0.0f;
            float maxDescent = 0.0f;
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                if (this.glyphVisible[i]) {
                    final GVTGlyphMetrics glyphMetrics = this.getGlyphMetrics(i);
                    final Rectangle2D glyphBounds = glyphMetrics.getBounds2D();
                    ascent = (float)(-glyphBounds.getMinY());
                    descent = (float)(glyphBounds.getHeight() - ascent);
                    if (ascent > maxAscent) {
                        maxAscent = ascent;
                    }
                    if (descent > maxDescent) {
                        maxDescent = descent;
                    }
                }
            }
            ascent = maxAscent;
            descent = maxDescent;
        }
        final Shape[] tempLogicalBounds = new Shape[this.getNumGlyphs()];
        final boolean[] rotated = new boolean[this.getNumGlyphs()];
        double maxWidth = -1.0;
        double maxHeight = -1.0;
        for (int j = 0; j < this.getNumGlyphs(); ++j) {
            if (!this.glyphVisible[j]) {
                tempLogicalBounds[j] = null;
            }
            else {
                final AffineTransform glyphTransform = this.getGlyphTransform(j);
                final GVTGlyphMetrics glyphMetrics2 = this.getGlyphMetrics(j);
                final Rectangle2D glyphBounds2 = new Rectangle2D.Double(0.0, -ascent, glyphMetrics2.getHorizontalAdvance(), ascent + descent);
                if (glyphBounds2.isEmpty()) {
                    if (j > 0) {
                        rotated[j] = rotated[j - 1];
                    }
                    else {
                        rotated[j] = true;
                    }
                }
                else {
                    final Point2D p1 = new Point2D.Double(glyphBounds2.getMinX(), glyphBounds2.getMinY());
                    final Point2D p2 = new Point2D.Double(glyphBounds2.getMaxX(), glyphBounds2.getMinY());
                    final Point2D p3 = new Point2D.Double(glyphBounds2.getMinX(), glyphBounds2.getMaxY());
                    final Point2D gpos = this.getGlyphPosition(j);
                    final AffineTransform tr = AffineTransform.getTranslateInstance(gpos.getX(), gpos.getY());
                    if (glyphTransform != null) {
                        tr.concatenate(glyphTransform);
                    }
                    tempLogicalBounds[j] = tr.createTransformedShape(glyphBounds2);
                    final Point2D tp1 = new Point2D.Double();
                    final Point2D tp2 = new Point2D.Double();
                    final Point2D tp3 = new Point2D.Double();
                    tr.transform(p1, tp1);
                    tr.transform(p2, tp2);
                    tr.transform(p3, tp3);
                    final double tdx12 = tp1.getX() - tp2.getX();
                    final double tdx13 = tp1.getX() - tp3.getX();
                    final double tdy12 = tp1.getY() - tp2.getY();
                    final double tdy13 = tp1.getY() - tp3.getY();
                    if (Math.abs(tdx12) < 0.001 && Math.abs(tdy13) < 0.001) {
                        rotated[j] = false;
                    }
                    else if (Math.abs(tdx13) < 0.001 && Math.abs(tdy12) < 0.001) {
                        rotated[j] = false;
                    }
                    else {
                        rotated[j] = true;
                    }
                    final Rectangle2D rectBounds = tempLogicalBounds[j].getBounds2D();
                    if (rectBounds.getWidth() > maxWidth) {
                        maxWidth = rectBounds.getWidth();
                    }
                    if (rectBounds.getHeight() > maxHeight) {
                        maxHeight = rectBounds.getHeight();
                    }
                }
            }
        }
        final GeneralPath logicalBoundsPath = new GeneralPath();
        for (int k = 0; k < this.getNumGlyphs(); ++k) {
            if (tempLogicalBounds[k] != null) {
                logicalBoundsPath.append(tempLogicalBounds[k], false);
            }
        }
        final Rectangle2D fullBounds = logicalBoundsPath.getBounds2D();
        if (fullBounds.getHeight() < maxHeight * 1.5) {
            for (int l = 0; l < this.getNumGlyphs(); ++l) {
                if (!rotated[l]) {
                    if (tempLogicalBounds[l] != null) {
                        final Rectangle2D glyphBounds2 = tempLogicalBounds[l].getBounds2D();
                        final double x = glyphBounds2.getMinX();
                        double width = glyphBounds2.getWidth();
                        if (l < this.getNumGlyphs() - 1 && tempLogicalBounds[l + 1] != null) {
                            final Rectangle2D ngb = tempLogicalBounds[l + 1].getBounds2D();
                            if (ngb.getX() > x) {
                                final double nw = ngb.getX() - x;
                                if (nw < width * 1.15 && nw > width * 0.85) {
                                    final double delta = (nw - width) * 0.5;
                                    width += delta;
                                    ngb.setRect(ngb.getX() - delta, ngb.getY(), ngb.getWidth() + delta, ngb.getHeight());
                                }
                            }
                        }
                        tempLogicalBounds[l] = new Rectangle2D.Double(x, fullBounds.getMinY(), width, fullBounds.getHeight());
                    }
                }
            }
        }
        else if (fullBounds.getWidth() < maxWidth * 1.5) {
            for (int l = 0; l < this.getNumGlyphs(); ++l) {
                if (!rotated[l]) {
                    if (tempLogicalBounds[l] != null) {
                        final Rectangle2D glyphBounds2 = tempLogicalBounds[l].getBounds2D();
                        final double y = glyphBounds2.getMinY();
                        double height = glyphBounds2.getHeight();
                        if (l < this.getNumGlyphs() - 1 && tempLogicalBounds[l + 1] != null) {
                            final Rectangle2D ngb = tempLogicalBounds[l + 1].getBounds2D();
                            if (ngb.getY() > y) {
                                final double nh = ngb.getY() - y;
                                if (nh < height * 1.15 && nh > height * 0.85) {
                                    final double delta = (nh - height) * 0.5;
                                    height += delta;
                                    ngb.setRect(ngb.getX(), ngb.getY() - delta, ngb.getWidth(), ngb.getHeight() + delta);
                                }
                            }
                        }
                        tempLogicalBounds[l] = new Rectangle2D.Double(fullBounds.getMinX(), y, fullBounds.getWidth(), height);
                    }
                }
            }
        }
        System.arraycopy(tempLogicalBounds, 0, this.glyphLogicalBounds, 0, this.getNumGlyphs());
    }
    
    @Override
    public GVTGlyphMetrics getGlyphMetrics(final int idx) {
        if (idx < 0 || idx > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        if (idx < this.glyphs.length - 1 && this.font != null) {
            final float hkern = this.font.getHKern(this.glyphs[idx].getGlyphCode(), this.glyphs[idx + 1].getGlyphCode());
            final float vkern = this.font.getVKern(this.glyphs[idx].getGlyphCode(), this.glyphs[idx + 1].getGlyphCode());
            return this.glyphs[idx].getGlyphMetrics(hkern, vkern);
        }
        return this.glyphs[idx].getGlyphMetrics();
    }
    
    @Override
    public Shape getGlyphOutline(final int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + ".");
        }
        return this.glyphs[glyphIndex].getOutline();
    }
    
    @Override
    public Rectangle2D getGlyphCellBounds(final int glyphIndex) {
        return this.getGlyphLogicalBounds(glyphIndex).getBounds2D();
    }
    
    @Override
    public Point2D getGlyphPosition(final int glyphIndex) {
        if (glyphIndex == this.glyphs.length) {
            return this.endPos;
        }
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getPosition();
    }
    
    @Override
    public float[] getGlyphPositions(final int beginGlyphIndex, int numEntries, float[] positionReturn) {
        if (numEntries < 0) {
            throw new IllegalArgumentException("numEntries argument value, " + numEntries + ", is illegal. It must be > 0.");
        }
        if (beginGlyphIndex < 0) {
            throw new IndexOutOfBoundsException("beginGlyphIndex " + beginGlyphIndex + " is out of bounds, should be between 0 and " + (this.glyphs.length - 1));
        }
        if (beginGlyphIndex + numEntries > this.glyphs.length + 1) {
            throw new IndexOutOfBoundsException("beginGlyphIndex + numEntries (" + beginGlyphIndex + '+' + numEntries + ") exceeds the number of glpyhs in this GlyphVector");
        }
        if (positionReturn == null) {
            positionReturn = new float[numEntries * 2];
        }
        if (beginGlyphIndex + numEntries == this.glyphs.length + 1) {
            --numEntries;
            positionReturn[numEntries * 2] = (float)this.endPos.getX();
            positionReturn[numEntries * 2 + 1] = (float)this.endPos.getY();
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            final Point2D glyphPos = this.glyphs[i].getPosition();
            positionReturn[(i - beginGlyphIndex) * 2] = (float)glyphPos.getX();
            positionReturn[(i - beginGlyphIndex) * 2 + 1] = (float)glyphPos.getY();
        }
        return positionReturn;
    }
    
    @Override
    public AffineTransform getGlyphTransform(final int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getTransform();
    }
    
    @Override
    public Shape getGlyphVisualBounds(final int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        return this.glyphs[glyphIndex].getOutline();
    }
    
    @Override
    public Rectangle2D getBounds2D(final AttributedCharacterIterator aci) {
        aci.first();
        final TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(SVGGVTGlyphVector.PAINT_INFO);
        if (this.bounds2D != null && TextPaintInfo.equivilent(tpi, this.cacheTPI)) {
            return this.bounds2D;
        }
        Rectangle2D b = null;
        if (tpi.visible) {
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                if (this.glyphVisible[i]) {
                    final Rectangle2D glyphBounds = this.glyphs[i].getBounds2D();
                    if (glyphBounds != null) {
                        if (b == null) {
                            b = glyphBounds;
                        }
                        else {
                            b.add(glyphBounds);
                        }
                    }
                }
            }
        }
        this.bounds2D = b;
        if (this.bounds2D == null) {
            this.bounds2D = new Rectangle2D.Float();
        }
        this.cacheTPI = new TextPaintInfo(tpi);
        return this.bounds2D;
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        if (this.logicalBounds == null) {
            final GeneralPath logicalBoundsPath = new GeneralPath();
            for (int i = 0; i < this.getNumGlyphs(); ++i) {
                final Shape glyphLogicalBounds = this.getGlyphLogicalBounds(i);
                if (glyphLogicalBounds != null) {
                    logicalBoundsPath.append(glyphLogicalBounds, false);
                }
            }
            this.logicalBounds = logicalBoundsPath.getBounds2D();
        }
        return this.logicalBounds;
    }
    
    @Override
    public int getNumGlyphs() {
        if (this.glyphs != null) {
            return this.glyphs.length;
        }
        return 0;
    }
    
    @Override
    public Shape getOutline() {
        if (this.outline == null) {
            this.outline = new GeneralPath();
            for (int i = 0; i < this.glyphs.length; ++i) {
                if (this.glyphVisible[i]) {
                    final Shape glyphOutline = this.glyphs[i].getOutline();
                    if (glyphOutline != null) {
                        this.outline.append(glyphOutline, false);
                    }
                }
            }
        }
        return this.outline;
    }
    
    @Override
    public Shape getOutline(final float x, final float y) {
        final Shape outline = this.getOutline();
        final AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
        final Shape translatedOutline = tr.createTransformedShape(outline);
        return translatedOutline;
    }
    
    @Override
    public Rectangle2D getGeometricBounds() {
        return this.getOutline().getBounds2D();
    }
    
    @Override
    public void performDefaultLayout() {
        this.logicalBounds = null;
        this.outline = null;
        this.bounds2D = null;
        float currentX = 0.0f;
        final float currentY = 0.0f;
        for (int i = 0; i < this.glyphs.length; ++i) {
            Glyph g = this.glyphs[i];
            g.setTransform(null);
            this.glyphLogicalBounds[i] = null;
            String uni = g.getUnicode();
            if (uni != null && uni.length() != 0 && ArabicTextHandler.arabicCharTransparent(uni.charAt(0))) {
                int j;
                for (j = i + 1; j < this.glyphs.length; ++j) {
                    uni = this.glyphs[j].getUnicode();
                    if (uni == null) {
                        break;
                    }
                    if (uni.length() == 0) {
                        break;
                    }
                    final char ch = uni.charAt(0);
                    if (!ArabicTextHandler.arabicCharTransparent(ch)) {
                        break;
                    }
                }
                if (j != this.glyphs.length) {
                    final Glyph bg = this.glyphs[j];
                    final float rEdge = currentX + bg.getHorizAdvX();
                    for (int k = i; k < j; ++k) {
                        g = this.glyphs[k];
                        g.setTransform(null);
                        this.glyphLogicalBounds[i] = null;
                        g.setPosition(new Point2D.Float(rEdge - g.getHorizAdvX(), currentY));
                    }
                    i = j;
                    g = bg;
                }
            }
            g.setPosition(new Point2D.Float(currentX, currentY));
            currentX += g.getHorizAdvX();
        }
        this.endPos = new Point2D.Float(currentX, currentY);
    }
    
    @Override
    public void setGlyphPosition(final int glyphIndex, final Point2D newPos) throws IndexOutOfBoundsException {
        if (glyphIndex == this.glyphs.length) {
            this.endPos = (Point2D)newPos.clone();
            return;
        }
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        this.glyphs[glyphIndex].setPosition(newPos);
        this.glyphLogicalBounds[glyphIndex] = null;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
    }
    
    @Override
    public void setGlyphTransform(final int glyphIndex, final AffineTransform newTX) {
        if (glyphIndex < 0 || glyphIndex > this.glyphs.length - 1) {
            throw new IndexOutOfBoundsException("glyphIndex: " + glyphIndex + ", is out of bounds. Should be between 0 and " + (this.glyphs.length - 1) + '.');
        }
        this.glyphs[glyphIndex].setTransform(newTX);
        this.glyphLogicalBounds[glyphIndex] = null;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
    }
    
    @Override
    public void setGlyphVisible(final int glyphIndex, final boolean visible) {
        if (visible == this.glyphVisible[glyphIndex]) {
            return;
        }
        this.glyphVisible[glyphIndex] = visible;
        this.outline = null;
        this.bounds2D = null;
        this.logicalBounds = null;
        this.glyphLogicalBounds[glyphIndex] = null;
    }
    
    @Override
    public boolean isGlyphVisible(final int glyphIndex) {
        return this.glyphVisible[glyphIndex];
    }
    
    @Override
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        int numChars = 0;
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex > this.glyphs.length - 1) {
            endGlyphIndex = this.glyphs.length - 1;
        }
        for (int i = startGlyphIndex; i <= endGlyphIndex; ++i) {
            final Glyph glyph = this.glyphs[i];
            if (glyph.getGlyphCode() == -1) {
                ++numChars;
            }
            else {
                final String glyphUnicode = glyph.getUnicode();
                numChars += glyphUnicode.length();
            }
        }
        return numChars;
    }
    
    @Override
    public boolean isReversed() {
        return false;
    }
    
    @Override
    public void maybeReverse(final boolean mirror) {
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final AttributedCharacterIterator aci) {
        aci.first();
        final TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(SVGGVTGlyphVector.PAINT_INFO);
        if (!tpi.visible) {
            return;
        }
        for (int i = 0; i < this.glyphs.length; ++i) {
            if (this.glyphVisible[i]) {
                this.glyphs[i].draw(graphics2D);
            }
        }
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    }
}
