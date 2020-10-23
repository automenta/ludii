// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import java.text.AttributedCharacterIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.FontRenderContext;
import java.util.Iterator;
import java.util.List;

public class MultiGlyphVector implements GVTGlyphVector
{
    GVTGlyphVector[] gvs;
    int[] nGlyphs;
    int[] off;
    int nGlyph;
    
    public MultiGlyphVector(final List gvs) {
        final int nSlots = gvs.size();
        this.gvs = new GVTGlyphVector[nSlots];
        this.nGlyphs = new int[nSlots];
        this.off = new int[nSlots];
        final Iterator iter = gvs.iterator();
        int i = 0;
        while (iter.hasNext()) {
            this.off[i] = this.nGlyph;
            final GVTGlyphVector gv = iter.next();
            this.gvs[i] = gv;
            this.nGlyphs[i] = gv.getNumGlyphs();
            this.nGlyph += this.nGlyphs[i];
            ++i;
        }
        final int[] nGlyphs = this.nGlyphs;
        final int n = i - 1;
        ++nGlyphs[n];
    }
    
    @Override
    public int getNumGlyphs() {
        return this.nGlyph;
    }
    
    int getGVIdx(final int glyphIdx) {
        if (glyphIdx > this.nGlyph) {
            return -1;
        }
        if (glyphIdx == this.nGlyph) {
            return this.gvs.length - 1;
        }
        for (int i = 0; i < this.nGlyphs.length; ++i) {
            if (glyphIdx - this.off[i] < this.nGlyphs[i]) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public GVTFont getFont() {
        throw new IllegalArgumentException("Can't be correctly Implemented");
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.gvs[0].getFontRenderContext();
    }
    
    @Override
    public int getGlyphCode(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphCode(glyphIndex - this.off[idx]);
    }
    
    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphJustificationInfo(glyphIndex - this.off[idx]);
    }
    
    @Override
    public Shape getGlyphLogicalBounds(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphLogicalBounds(glyphIndex - this.off[idx]);
    }
    
    @Override
    public GVTGlyphMetrics getGlyphMetrics(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphMetrics(glyphIndex - this.off[idx]);
    }
    
    @Override
    public Shape getGlyphOutline(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphOutline(glyphIndex - this.off[idx]);
    }
    
    @Override
    public Rectangle2D getGlyphCellBounds(final int glyphIndex) {
        return this.getGlyphLogicalBounds(glyphIndex).getBounds2D();
    }
    
    @Override
    public Point2D getGlyphPosition(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphPosition(glyphIndex - this.off[idx]);
    }
    
    @Override
    public AffineTransform getGlyphTransform(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphTransform(glyphIndex - this.off[idx]);
    }
    
    @Override
    public Shape getGlyphVisualBounds(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].getGlyphVisualBounds(glyphIndex - this.off[idx]);
    }
    
    @Override
    public void setGlyphPosition(final int glyphIndex, final Point2D newPos) {
        final int idx = this.getGVIdx(glyphIndex);
        this.gvs[idx].setGlyphPosition(glyphIndex - this.off[idx], newPos);
    }
    
    @Override
    public void setGlyphTransform(final int glyphIndex, final AffineTransform newTX) {
        final int idx = this.getGVIdx(glyphIndex);
        this.gvs[idx].setGlyphTransform(glyphIndex - this.off[idx], newTX);
    }
    
    @Override
    public void setGlyphVisible(final int glyphIndex, final boolean visible) {
        final int idx = this.getGVIdx(glyphIndex);
        this.gvs[idx].setGlyphVisible(glyphIndex - this.off[idx], visible);
    }
    
    @Override
    public boolean isGlyphVisible(final int glyphIndex) {
        final int idx = this.getGVIdx(glyphIndex);
        return this.gvs[idx].isGlyphVisible(glyphIndex - this.off[idx]);
    }
    
    @Override
    public int[] getGlyphCodes(final int beginGlyphIndex, int numEntries, final int[] codeReturn) {
        int[] ret = codeReturn;
        if (ret == null) {
            ret = new int[numEntries];
        }
        int[] tmp = null;
        int gvIdx = this.getGVIdx(beginGlyphIndex);
        int gi = beginGlyphIndex - this.off[gvIdx];
        int len;
        for (int i = 0; numEntries != 0; numEntries -= len, i += len) {
            len = numEntries;
            if (gi + len > this.nGlyphs[gvIdx]) {
                len = this.nGlyphs[gvIdx] - gi;
            }
            final GVTGlyphVector gv = this.gvs[gvIdx];
            if (i == 0) {
                gv.getGlyphCodes(gi, len, ret);
            }
            else {
                if (tmp == null || tmp.length < len) {
                    tmp = new int[len];
                }
                gv.getGlyphCodes(gi, len, tmp);
                System.arraycopy(tmp, 0, ret, i, len);
            }
            gi = 0;
            ++gvIdx;
        }
        return ret;
    }
    
    @Override
    public float[] getGlyphPositions(final int beginGlyphIndex, int numEntries, final float[] positionReturn) {
        float[] ret = positionReturn;
        if (ret == null) {
            ret = new float[numEntries * 2];
        }
        float[] tmp = null;
        int gvIdx = this.getGVIdx(beginGlyphIndex);
        int gi = beginGlyphIndex - this.off[gvIdx];
        int len;
        for (int i = 0; numEntries != 0; numEntries -= len, i += len * 2) {
            len = numEntries;
            if (gi + len > this.nGlyphs[gvIdx]) {
                len = this.nGlyphs[gvIdx] - gi;
            }
            final GVTGlyphVector gv = this.gvs[gvIdx];
            if (i == 0) {
                gv.getGlyphPositions(gi, len, ret);
            }
            else {
                if (tmp == null || tmp.length < len * 2) {
                    tmp = new float[len * 2];
                }
                gv.getGlyphPositions(gi, len, tmp);
                System.arraycopy(tmp, 0, ret, i, len * 2);
            }
            gi = 0;
            ++gvIdx;
        }
        return ret;
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        Rectangle2D ret = null;
        for (final GVTGlyphVector gv : this.gvs) {
            final Rectangle2D b = gv.getLogicalBounds();
            if (ret == null) {
                ret = b;
            }
            else {
                ret.add(b);
            }
        }
        return ret;
    }
    
    @Override
    public Shape getOutline() {
        GeneralPath ret = null;
        for (final GVTGlyphVector gv : this.gvs) {
            final Shape s = gv.getOutline();
            if (ret == null) {
                ret = new GeneralPath(s);
            }
            else {
                ret.append(s, false);
            }
        }
        return ret;
    }
    
    @Override
    public Shape getOutline(final float x, final float y) {
        Shape outline = this.getOutline();
        final AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
        outline = tr.createTransformedShape(outline);
        return outline;
    }
    
    @Override
    public Rectangle2D getBounds2D(final AttributedCharacterIterator aci) {
        Rectangle2D ret = null;
        int begin = aci.getBeginIndex();
        for (final GVTGlyphVector gv : this.gvs) {
            final int end = gv.getCharacterCount(0, gv.getNumGlyphs()) + 1;
            final Rectangle2D b = gv.getBounds2D(new AttributedCharacterSpanIterator(aci, begin, end));
            if (ret == null) {
                ret = b;
            }
            else {
                ret.add(b);
            }
            begin = end;
        }
        return ret;
    }
    
    @Override
    public Rectangle2D getGeometricBounds() {
        Rectangle2D ret = null;
        for (final GVTGlyphVector gv : this.gvs) {
            final Rectangle2D b = gv.getGeometricBounds();
            if (ret == null) {
                ret = b;
            }
            else {
                ret.add(b);
            }
        }
        return ret;
    }
    
    @Override
    public void performDefaultLayout() {
        for (final GVTGlyphVector gv : this.gvs) {
            gv.performDefaultLayout();
        }
    }
    
    @Override
    public int getCharacterCount(int startGlyphIndex, final int endGlyphIndex) {
        final int idx1 = this.getGVIdx(startGlyphIndex);
        final int idx2 = this.getGVIdx(endGlyphIndex);
        int ret = 0;
        for (int idx3 = idx1; idx3 <= idx2; ++idx3) {
            final int gi1 = startGlyphIndex - this.off[idx3];
            int gi2 = endGlyphIndex - this.off[idx3];
            if (gi2 >= this.nGlyphs[idx3]) {
                gi2 = this.nGlyphs[idx3] - 1;
            }
            ret += this.gvs[idx3].getCharacterCount(gi1, gi2);
            startGlyphIndex += gi2 - gi1 + 1;
        }
        return ret;
    }
    
    @Override
    public boolean isReversed() {
        return false;
    }
    
    @Override
    public void maybeReverse(final boolean mirror) {
    }
    
    @Override
    public void draw(final Graphics2D g2d, final AttributedCharacterIterator aci) {
        int begin = aci.getBeginIndex();
        for (final GVTGlyphVector gv : this.gvs) {
            final int end = gv.getCharacterCount(0, gv.getNumGlyphs()) + 1;
            gv.draw(g2d, new AttributedCharacterSpanIterator(aci, begin, end));
            begin = end;
        }
    }
}
