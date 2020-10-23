// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import org.apache.batik.util.Platform;
import java.awt.RenderingHints;
import java.awt.Color;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.awt.Graphics2D;
import org.apache.batik.gvt.text.ArabicTextHandler;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.FontRenderContext;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.CharacterIterator;
import java.awt.font.GlyphVector;
import java.text.AttributedCharacterIterator;

public class AWTGVTGlyphVector implements GVTGlyphVector
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    private GlyphVector awtGlyphVector;
    private AWTGVTFont gvtFont;
    private CharacterIterator ci;
    private Point2D[] defaultGlyphPositions;
    private Point2D.Float[] glyphPositions;
    private AffineTransform[] glyphTransforms;
    private Shape[] glyphOutlines;
    private Shape[] glyphVisualBounds;
    private Shape[] glyphLogicalBounds;
    private boolean[] glyphVisible;
    private GVTGlyphMetrics[] glyphMetrics;
    private GeneralPath outline;
    private Rectangle2D visualBounds;
    private Rectangle2D logicalBounds;
    private Rectangle2D bounds2D;
    private double scaleFactor;
    private float ascent;
    private float descent;
    private TextPaintInfo cacheTPI;
    private static final boolean outlinesPositioned;
    private static final boolean drawGlyphVectorWorks;
    private static final boolean glyphVectorTransformWorks;
    
    public AWTGVTGlyphVector(final GlyphVector glyphVector, final AWTGVTFont font, final double scaleFactor, final CharacterIterator ci) {
        this.awtGlyphVector = glyphVector;
        this.gvtFont = font;
        this.scaleFactor = scaleFactor;
        this.ci = ci;
        final GVTLineMetrics lineMetrics = this.gvtFont.getLineMetrics("By", this.awtGlyphVector.getFontRenderContext());
        this.ascent = lineMetrics.getAscent();
        this.descent = lineMetrics.getDescent();
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        final int numGlyphs = glyphVector.getNumGlyphs();
        this.glyphPositions = new Point2D.Float[numGlyphs + 1];
        this.glyphTransforms = new AffineTransform[numGlyphs];
        this.glyphOutlines = new Shape[numGlyphs];
        this.glyphVisualBounds = new Shape[numGlyphs];
        this.glyphLogicalBounds = new Shape[numGlyphs];
        this.glyphVisible = new boolean[numGlyphs];
        this.glyphMetrics = new GVTGlyphMetrics[numGlyphs];
        for (int i = 0; i < numGlyphs; ++i) {
            this.glyphVisible[i] = true;
        }
    }
    
    @Override
    public GVTFont getFont() {
        return this.gvtFont;
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.awtGlyphVector.getFontRenderContext();
    }
    
    @Override
    public int getGlyphCode(final int glyphIndex) {
        return this.awtGlyphVector.getGlyphCode(glyphIndex);
    }
    
    @Override
    public int[] getGlyphCodes(final int beginGlyphIndex, final int numEntries, final int[] codeReturn) {
        return this.awtGlyphVector.getGlyphCodes(beginGlyphIndex, numEntries, codeReturn);
    }
    
    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(final int glyphIndex) {
        return this.awtGlyphVector.getGlyphJustificationInfo(glyphIndex);
    }
    
    @Override
    public Rectangle2D getBounds2D(final AttributedCharacterIterator aci) {
        aci.first();
        final TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(AWTGVTGlyphVector.PAINT_INFO);
        if (this.bounds2D != null && TextPaintInfo.equivilent(tpi, this.cacheTPI)) {
            return this.bounds2D;
        }
        if (tpi == null) {
            return null;
        }
        if (!tpi.visible) {
            return null;
        }
        this.cacheTPI = new TextPaintInfo(tpi);
        Shape outline = null;
        if (tpi.fillPaint != null) {
            outline = this.getOutline();
            this.bounds2D = outline.getBounds2D();
        }
        final Stroke stroke = tpi.strokeStroke;
        final Paint paint = tpi.strokePaint;
        if (stroke != null && paint != null) {
            if (outline == null) {
                outline = this.getOutline();
            }
            final Rectangle2D strokeBounds = stroke.createStrokedShape(outline).getBounds2D();
            if (this.bounds2D == null) {
                this.bounds2D = strokeBounds;
            }
            else {
                this.bounds2D.add(strokeBounds);
            }
        }
        if (this.bounds2D == null) {
            return null;
        }
        if (this.bounds2D.getWidth() == 0.0 || this.bounds2D.getHeight() == 0.0) {
            this.bounds2D = null;
        }
        return this.bounds2D;
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        if (this.logicalBounds == null) {
            this.computeGlyphLogicalBounds();
        }
        return this.logicalBounds;
    }
    
    @Override
    public Shape getGlyphLogicalBounds(final int glyphIndex) {
        if (this.glyphLogicalBounds[glyphIndex] == null && this.glyphVisible[glyphIndex]) {
            this.computeGlyphLogicalBounds();
        }
        return this.glyphLogicalBounds[glyphIndex];
    }
    
    private void computeGlyphLogicalBounds() {
        final Shape[] tempLogicalBounds = new Shape[this.getNumGlyphs()];
        final boolean[] rotated = new boolean[this.getNumGlyphs()];
        double maxWidth = -1.0;
        double maxHeight = -1.0;
        for (int i = 0; i < this.getNumGlyphs(); ++i) {
            if (!this.glyphVisible[i]) {
                tempLogicalBounds[i] = null;
            }
            else {
                final AffineTransform glyphTransform = this.getGlyphTransform(i);
                final GVTGlyphMetrics glyphMetrics = this.getGlyphMetrics(i);
                final float glyphX = 0.0f;
                final float glyphY = (float)(-this.ascent / this.scaleFactor);
                final float glyphWidth = (float)(glyphMetrics.getHorizontalAdvance() / this.scaleFactor);
                final float glyphHeight = (float)(glyphMetrics.getVerticalAdvance() / this.scaleFactor);
                final Rectangle2D glyphBounds = new Rectangle2D.Double(glyphX, glyphY, glyphWidth, glyphHeight);
                if (glyphBounds.isEmpty()) {
                    if (i > 0) {
                        rotated[i] = rotated[i - 1];
                    }
                    else {
                        rotated[i] = true;
                    }
                }
                else {
                    final Point2D p1 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMinY());
                    final Point2D p2 = new Point2D.Double(glyphBounds.getMaxX(), glyphBounds.getMinY());
                    final Point2D p3 = new Point2D.Double(glyphBounds.getMinX(), glyphBounds.getMaxY());
                    final Point2D gpos = this.getGlyphPosition(i);
                    final AffineTransform tr = AffineTransform.getTranslateInstance(gpos.getX(), gpos.getY());
                    if (glyphTransform != null) {
                        tr.concatenate(glyphTransform);
                    }
                    tr.scale(this.scaleFactor, this.scaleFactor);
                    tempLogicalBounds[i] = tr.createTransformedShape(glyphBounds);
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
                    if ((Math.abs(tdx12) < 0.001 && Math.abs(tdy13) < 0.001) || (Math.abs(tdx13) < 0.001 && Math.abs(tdy12) < 0.001)) {
                        rotated[i] = false;
                    }
                    else {
                        rotated[i] = true;
                    }
                    final Rectangle2D rectBounds = tempLogicalBounds[i].getBounds2D();
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
        for (int j = 0; j < this.getNumGlyphs(); ++j) {
            if (tempLogicalBounds[j] != null) {
                logicalBoundsPath.append(tempLogicalBounds[j], false);
            }
        }
        this.logicalBounds = logicalBoundsPath.getBounds2D();
        if (this.logicalBounds.getHeight() < maxHeight * 1.5) {
            for (int j = 0; j < this.getNumGlyphs(); ++j) {
                if (!rotated[j]) {
                    if (tempLogicalBounds[j] != null) {
                        final Rectangle2D glyphBounds2 = tempLogicalBounds[j].getBounds2D();
                        final double x = glyphBounds2.getMinX();
                        double width = glyphBounds2.getWidth();
                        if (j < this.getNumGlyphs() - 1 && tempLogicalBounds[j + 1] != null) {
                            final Rectangle2D ngb = tempLogicalBounds[j + 1].getBounds2D();
                            if (ngb.getX() > x) {
                                final double nw = ngb.getX() - x;
                                if (nw < width * 1.15 && nw > width * 0.85) {
                                    final double delta = (nw - width) * 0.5;
                                    width += delta;
                                    ngb.setRect(ngb.getX() - delta, ngb.getY(), ngb.getWidth() + delta, ngb.getHeight());
                                }
                            }
                        }
                        tempLogicalBounds[j] = new Rectangle2D.Double(x, this.logicalBounds.getMinY(), width, this.logicalBounds.getHeight());
                    }
                }
            }
        }
        else if (this.logicalBounds.getWidth() < maxWidth * 1.5) {
            for (int j = 0; j < this.getNumGlyphs(); ++j) {
                if (!rotated[j]) {
                    if (tempLogicalBounds[j] != null) {
                        final Rectangle2D glyphBounds2 = tempLogicalBounds[j].getBounds2D();
                        final double y = glyphBounds2.getMinY();
                        double height = glyphBounds2.getHeight();
                        if (j < this.getNumGlyphs() - 1 && tempLogicalBounds[j + 1] != null) {
                            final Rectangle2D ngb = tempLogicalBounds[j + 1].getBounds2D();
                            if (ngb.getY() > y) {
                                final double nh = ngb.getY() - y;
                                if (nh < height * 1.15 && nh > height * 0.85) {
                                    final double delta = (nh - height) * 0.5;
                                    height += delta;
                                    ngb.setRect(ngb.getX(), ngb.getY() - delta, ngb.getWidth(), ngb.getHeight() + delta);
                                }
                            }
                        }
                        tempLogicalBounds[j] = new Rectangle2D.Double(this.logicalBounds.getMinX(), y, this.logicalBounds.getWidth(), height);
                    }
                }
            }
        }
        System.arraycopy(tempLogicalBounds, 0, this.glyphLogicalBounds, 0, this.getNumGlyphs());
    }
    
    @Override
    public GVTGlyphMetrics getGlyphMetrics(final int glyphIndex) {
        if (this.glyphMetrics[glyphIndex] != null) {
            return this.glyphMetrics[glyphIndex];
        }
        final Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
        final char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
        this.ci.setIndex(this.ci.getBeginIndex());
        final AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
        final Rectangle2D gmB = v.getBounds2D();
        final Rectangle2D bounds = new Rectangle2D.Double(gmB.getX() * this.scaleFactor, gmB.getY() * this.scaleFactor, gmB.getWidth() * this.scaleFactor, gmB.getHeight() * this.scaleFactor);
        final float adv = (float)(this.defaultGlyphPositions[glyphIndex + 1].getX() - this.defaultGlyphPositions[glyphIndex].getX());
        return this.glyphMetrics[glyphIndex] = new GVTGlyphMetrics((float)(adv * this.scaleFactor), this.ascent + this.descent, bounds, (byte)0);
    }
    
    @Override
    public Shape getGlyphOutline(final int glyphIndex) {
        if (this.glyphOutlines[glyphIndex] == null) {
            final Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
            final char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
            this.ci.setIndex(this.ci.getBeginIndex());
            final AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
            final Shape glyphOutline = v.getOutline();
            final AffineTransform tr = AffineTransform.getTranslateInstance(this.getGlyphPosition(glyphIndex).getX(), this.getGlyphPosition(glyphIndex).getY());
            final AffineTransform glyphTransform = this.getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tr.scale(this.scaleFactor, this.scaleFactor);
            this.glyphOutlines[glyphIndex] = tr.createTransformedShape(glyphOutline);
        }
        return this.glyphOutlines[glyphIndex];
    }
    
    static boolean outlinesPositioned() {
        return AWTGVTGlyphVector.outlinesPositioned;
    }
    
    @Override
    public Rectangle2D getGlyphCellBounds(final int glyphIndex) {
        return this.getGlyphLogicalBounds(glyphIndex).getBounds2D();
    }
    
    @Override
    public Point2D getGlyphPosition(final int glyphIndex) {
        return this.glyphPositions[glyphIndex];
    }
    
    @Override
    public float[] getGlyphPositions(final int beginGlyphIndex, final int numEntries, float[] positionReturn) {
        if (positionReturn == null) {
            positionReturn = new float[numEntries * 2];
        }
        for (int i = beginGlyphIndex; i < beginGlyphIndex + numEntries; ++i) {
            final Point2D glyphPos = this.getGlyphPosition(i);
            positionReturn[(i - beginGlyphIndex) * 2] = (float)glyphPos.getX();
            positionReturn[(i - beginGlyphIndex) * 2 + 1] = (float)glyphPos.getY();
        }
        return positionReturn;
    }
    
    @Override
    public AffineTransform getGlyphTransform(final int glyphIndex) {
        return this.glyphTransforms[glyphIndex];
    }
    
    @Override
    public Shape getGlyphVisualBounds(final int glyphIndex) {
        if (this.glyphVisualBounds[glyphIndex] == null) {
            final Point2D glyphPos = this.defaultGlyphPositions[glyphIndex];
            final char c = this.ci.setIndex(this.ci.getBeginIndex() + glyphIndex);
            this.ci.setIndex(this.ci.getBeginIndex());
            final AWTGlyphGeometryCache.Value v = AWTGVTFont.getGlyphGeometry(this.gvtFont, c, this.awtGlyphVector, glyphIndex, glyphPos);
            final Rectangle2D glyphBounds = v.getOutlineBounds2D();
            final AffineTransform tr = AffineTransform.getTranslateInstance(this.getGlyphPosition(glyphIndex).getX(), this.getGlyphPosition(glyphIndex).getY());
            final AffineTransform glyphTransform = this.getGlyphTransform(glyphIndex);
            if (glyphTransform != null) {
                tr.concatenate(glyphTransform);
            }
            tr.scale(this.scaleFactor, this.scaleFactor);
            this.glyphVisualBounds[glyphIndex] = tr.createTransformedShape(glyphBounds);
        }
        return this.glyphVisualBounds[glyphIndex];
    }
    
    @Override
    public int getNumGlyphs() {
        return this.awtGlyphVector.getNumGlyphs();
    }
    
    @Override
    public Shape getOutline() {
        if (this.outline != null) {
            return this.outline;
        }
        this.outline = new GeneralPath();
        for (int i = 0; i < this.getNumGlyphs(); ++i) {
            if (this.glyphVisible[i]) {
                final Shape glyphOutline = this.getGlyphOutline(i);
                this.outline.append(glyphOutline, false);
            }
        }
        return this.outline;
    }
    
    @Override
    public Shape getOutline(final float x, final float y) {
        Shape outline = this.getOutline();
        final AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
        outline = tr.createTransformedShape(outline);
        return outline;
    }
    
    @Override
    public Rectangle2D getGeometricBounds() {
        if (this.visualBounds == null) {
            final Shape outline = this.getOutline();
            this.visualBounds = outline.getBounds2D();
        }
        return this.visualBounds;
    }
    
    @Override
    public void performDefaultLayout() {
        if (this.defaultGlyphPositions == null) {
            this.awtGlyphVector.performDefaultLayout();
            this.defaultGlyphPositions = new Point2D.Float[this.getNumGlyphs() + 1];
            for (int i = 0; i <= this.getNumGlyphs(); ++i) {
                this.defaultGlyphPositions[i] = this.awtGlyphVector.getGlyphPosition(i);
            }
        }
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        final float shiftLeft = 0.0f;
        int j;
        for (j = 0; j < this.getNumGlyphs(); ++j) {
            this.glyphTransforms[j] = null;
            this.glyphVisualBounds[j] = null;
            this.glyphLogicalBounds[j] = null;
            this.glyphOutlines[j] = null;
            this.glyphMetrics[j] = null;
            final Point2D glyphPos = this.defaultGlyphPositions[j];
            final float x = (float)(glyphPos.getX() * this.scaleFactor - shiftLeft);
            final float y = (float)(glyphPos.getY() * this.scaleFactor);
            this.ci.setIndex(j + this.ci.getBeginIndex());
            if (this.glyphPositions[j] == null) {
                this.glyphPositions[j] = new Point2D.Float(x, y);
            }
            else {
                this.glyphPositions[j].x = x;
                this.glyphPositions[j].y = y;
            }
        }
        final Point2D glyphPos = this.defaultGlyphPositions[j];
        this.glyphPositions[j] = new Point2D.Float((float)(glyphPos.getX() * this.scaleFactor - shiftLeft), (float)(glyphPos.getY() * this.scaleFactor));
    }
    
    @Override
    public void setGlyphPosition(final int glyphIndex, final Point2D newPos) {
        this.glyphPositions[glyphIndex].x = (float)newPos.getX();
        this.glyphPositions[glyphIndex].y = (float)newPos.getY();
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        if (glyphIndex != this.getNumGlyphs()) {
            this.glyphVisualBounds[glyphIndex] = null;
            this.glyphLogicalBounds[glyphIndex] = null;
            this.glyphOutlines[glyphIndex] = null;
            this.glyphMetrics[glyphIndex] = null;
        }
    }
    
    @Override
    public void setGlyphTransform(final int glyphIndex, final AffineTransform newTX) {
        this.glyphTransforms[glyphIndex] = newTX;
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        this.glyphVisualBounds[glyphIndex] = null;
        this.glyphLogicalBounds[glyphIndex] = null;
        this.glyphOutlines[glyphIndex] = null;
        this.glyphMetrics[glyphIndex] = null;
    }
    
    @Override
    public void setGlyphVisible(final int glyphIndex, final boolean visible) {
        if (visible == this.glyphVisible[glyphIndex]) {
            return;
        }
        this.glyphVisible[glyphIndex] = visible;
        this.outline = null;
        this.visualBounds = null;
        this.logicalBounds = null;
        this.bounds2D = null;
        this.glyphVisualBounds[glyphIndex] = null;
        this.glyphLogicalBounds[glyphIndex] = null;
        this.glyphOutlines[glyphIndex] = null;
        this.glyphMetrics[glyphIndex] = null;
    }
    
    @Override
    public boolean isGlyphVisible(final int glyphIndex) {
        return this.glyphVisible[glyphIndex];
    }
    
    @Override
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        if (startGlyphIndex < 0) {
            startGlyphIndex = 0;
        }
        if (endGlyphIndex >= this.getNumGlyphs()) {
            endGlyphIndex = this.getNumGlyphs() - 1;
        }
        int charCount = 0;
        final int start = startGlyphIndex + this.ci.getBeginIndex();
        final int end = endGlyphIndex + this.ci.getBeginIndex();
        char c = this.ci.setIndex(start);
        while (this.ci.getIndex() <= end) {
            charCount += ArabicTextHandler.getNumChars(c);
            c = this.ci.next();
        }
        return charCount;
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
        final int numGlyphs = this.getNumGlyphs();
        aci.first();
        final TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO);
        if (tpi == null) {
            return;
        }
        if (!tpi.visible) {
            return;
        }
        final Paint fillPaint = tpi.fillPaint;
        final Stroke stroke = tpi.strokeStroke;
        final Paint strokePaint = tpi.strokePaint;
        if (fillPaint == null && (strokePaint == null || stroke == null)) {
            return;
        }
        boolean useHinting = AWTGVTGlyphVector.drawGlyphVectorWorks;
        if (useHinting && stroke != null && strokePaint != null) {
            useHinting = false;
        }
        if (useHinting && fillPaint != null && !(fillPaint instanceof Color)) {
            useHinting = false;
        }
        if (useHinting) {
            final Object v1 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            final Object v2 = graphics2D.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            if (v1 == RenderingHints.VALUE_TEXT_ANTIALIAS_ON && v2 == RenderingHints.VALUE_STROKE_PURE) {
                useHinting = false;
            }
        }
        final int typeGRot = 16;
        final int typeGTrans = 32;
        if (useHinting) {
            final AffineTransform at = graphics2D.getTransform();
            final int type = at.getType();
            if ((type & 0x20) != 0x0 || (type & 0x10) != 0x0) {
                useHinting = false;
            }
        }
        if (useHinting) {
            for (int i = 0; i < numGlyphs; ++i) {
                if (!this.glyphVisible[i]) {
                    useHinting = false;
                    break;
                }
                final AffineTransform at2 = this.glyphTransforms[i];
                if (at2 != null) {
                    final int type2 = at2.getType();
                    if ((type2 & 0xFFFFFFFE) != 0x0) {
                        if (!AWTGVTGlyphVector.glyphVectorTransformWorks || (type2 & 0x20) != 0x0 || (type2 & 0x10) != 0x0) {
                            useHinting = false;
                            break;
                        }
                    }
                }
            }
        }
        if (useHinting) {
            final double sf = this.scaleFactor;
            final double[] mat = new double[6];
            for (int j = 0; j < numGlyphs; ++j) {
                Point2D pos = this.glyphPositions[j];
                double x = pos.getX();
                double y = pos.getY();
                AffineTransform at3 = this.glyphTransforms[j];
                if (at3 != null) {
                    at3.getMatrix(mat);
                    x += mat[4];
                    y += mat[5];
                    if (mat[0] != 1.0 || mat[1] != 0.0 || mat[2] != 0.0 || mat[3] != 1.0) {
                        mat[5] = (mat[4] = 0.0);
                        at3 = new AffineTransform(mat);
                    }
                    else {
                        at3 = null;
                    }
                }
                pos = new Point2D.Double(x / sf, y / sf);
                this.awtGlyphVector.setGlyphPosition(j, pos);
                this.awtGlyphVector.setGlyphTransform(j, at3);
            }
            graphics2D.scale(sf, sf);
            graphics2D.setPaint(fillPaint);
            graphics2D.drawGlyphVector(this.awtGlyphVector, 0.0f, 0.0f);
            graphics2D.scale(1.0 / sf, 1.0 / sf);
            for (int j = 0; j < numGlyphs; ++j) {
                final Point2D pos = this.defaultGlyphPositions[j];
                this.awtGlyphVector.setGlyphPosition(j, pos);
                this.awtGlyphVector.setGlyphTransform(j, null);
            }
        }
        else {
            final Shape outline = this.getOutline();
            if (fillPaint != null) {
                graphics2D.setPaint(fillPaint);
                graphics2D.fill(outline);
            }
            if (stroke != null && strokePaint != null) {
                graphics2D.setStroke(stroke);
                graphics2D.setPaint(strokePaint);
                graphics2D.draw(outline);
            }
        }
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
        final String s = System.getProperty("java.specification.version");
        if ("1.6".compareTo(s) <= 0) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = false;
            glyphVectorTransformWorks = true;
        }
        else if ("1.4".compareTo(s) <= 0) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = true;
            glyphVectorTransformWorks = true;
        }
        else if (Platform.isOSX) {
            outlinesPositioned = true;
            drawGlyphVectorWorks = false;
            glyphVectorTransformWorks = false;
        }
        else {
            outlinesPositioned = false;
            drawGlyphVectorWorks = true;
            glyphVectorTransformWorks = false;
        }
    }
}
