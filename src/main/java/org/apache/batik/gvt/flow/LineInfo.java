// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import java.awt.geom.Point2D;
import org.apache.batik.gvt.font.GVTGlyphVector;

public class LineInfo
{
    FlowRegions fr;
    double lineHeight;
    double ascent;
    double descent;
    double hLeading;
    double baseline;
    int numGlyphs;
    int words;
    int size;
    GlyphGroupInfo[] ggis;
    int newSize;
    GlyphGroupInfo[] newGGIS;
    int numRanges;
    double[] ranges;
    double[] rangeAdv;
    BlockInfo bi;
    boolean paraStart;
    boolean paraEnd;
    protected static final int FULL_WORD = 0;
    protected static final int FULL_ADV = 1;
    static final float MAX_COMPRESS = 0.1f;
    static final float COMRESS_SCALE = 3.0f;
    
    public LineInfo(final FlowRegions fr, final BlockInfo bi, final boolean paraStart) {
        this.lineHeight = -1.0;
        this.ascent = -1.0;
        this.descent = -1.0;
        this.hLeading = -1.0;
        this.words = 0;
        this.size = 0;
        this.ggis = null;
        this.newSize = 0;
        this.newGGIS = null;
        this.bi = null;
        this.fr = fr;
        this.bi = bi;
        this.lineHeight = bi.getLineHeight();
        this.ascent = bi.getAscent();
        this.descent = bi.getDescent();
        this.hLeading = (this.lineHeight - (this.ascent + this.descent)) / 2.0;
        this.baseline = (float)(fr.getCurrentY() + this.hLeading + this.ascent);
        this.paraStart = paraStart;
        this.paraEnd = false;
        if (this.lineHeight > 0.0) {
            fr.newLineHeight(this.lineHeight);
            this.updateRangeInfo();
        }
    }
    
    public void setParaEnd(final boolean paraEnd) {
        this.paraEnd = paraEnd;
    }
    
    public boolean addWord(final WordInfo wi) {
        final double nlh = wi.getLineHeight();
        if (nlh <= this.lineHeight) {
            return this.insertWord(wi);
        }
        this.fr.newLineHeight(nlh);
        if (!this.updateRangeInfo()) {
            if (this.lineHeight > 0.0) {
                this.fr.newLineHeight(this.lineHeight);
            }
            return false;
        }
        if (!this.insertWord(wi)) {
            if (this.lineHeight > 0.0) {
                this.setLineHeight(this.lineHeight);
            }
            return false;
        }
        this.lineHeight = nlh;
        if (wi.getAscent() > this.ascent) {
            this.ascent = wi.getAscent();
        }
        if (wi.getDescent() > this.descent) {
            this.descent = wi.getDescent();
        }
        this.hLeading = (nlh - (this.ascent + this.descent)) / 2.0;
        this.baseline = (float)(this.fr.getCurrentY() + this.hLeading + this.ascent);
        return true;
    }
    
    public boolean insertWord(final WordInfo wi) {
        this.mergeGlyphGroups(wi);
        if (!this.assignGlyphGroupRanges(this.newSize, this.newGGIS)) {
            return false;
        }
        this.swapGlyphGroupInfo();
        return true;
    }
    
    public boolean assignGlyphGroupRanges(final int ggSz, final GlyphGroupInfo[] ggis) {
        int i = 0;
        int r = 0;
        while (r < this.numRanges) {
            final double range = this.ranges[2 * r + 1] - this.ranges[2 * r];
            float adv;
            float rangeAdvance;
            for (adv = 0.0f, rangeAdvance = 0.0f; i < ggSz; ++i, rangeAdvance += adv) {
                final GlyphGroupInfo ggi = ggis[i];
                ggi.setRange(r);
                adv = ggi.getAdvance();
                final double delta = range - (rangeAdvance + adv);
                if (delta < 0.0) {
                    break;
                }
            }
            if (i == ggSz) {
                --i;
                rangeAdvance -= adv;
            }
            GlyphGroupInfo ggi;
            float ladv;
            for (ggi = ggis[i], ladv = ggi.getLastAdvance(); rangeAdvance + ladv > range; rangeAdvance -= ggi.getAdvance(), ladv = ggi.getLastAdvance()) {
                --i;
                ladv = 0.0f;
                if (i < 0) {
                    break;
                }
                ggi = ggis[i];
                if (r != ggi.getRange()) {
                    break;
                }
            }
            ++i;
            this.rangeAdv[r] = rangeAdvance + ladv;
            ++r;
            if (i == ggSz) {
                return true;
            }
        }
        return false;
    }
    
    public boolean setLineHeight(final double lh) {
        this.fr.newLineHeight(lh);
        if (this.updateRangeInfo()) {
            this.lineHeight = lh;
            return true;
        }
        if (this.lineHeight > 0.0) {
            this.fr.newLineHeight(this.lineHeight);
        }
        return false;
    }
    
    public double getCurrentY() {
        return this.fr.getCurrentY();
    }
    
    public boolean gotoY(final double y) {
        if (this.fr.gotoY(y)) {
            return true;
        }
        if (this.lineHeight > 0.0) {
            this.updateRangeInfo();
        }
        this.baseline = (float)(this.fr.getCurrentY() + this.hLeading + this.ascent);
        return false;
    }
    
    protected boolean updateRangeInfo() {
        this.fr.resetRange();
        final int nr = this.fr.getNumRangeOnLine();
        if (nr == 0) {
            return false;
        }
        this.numRanges = nr;
        if (this.ranges == null) {
            this.rangeAdv = new double[this.numRanges];
            this.ranges = new double[2 * this.numRanges];
        }
        else if (this.numRanges > this.rangeAdv.length) {
            int sz = 2 * this.rangeAdv.length;
            if (sz < this.numRanges) {
                sz = this.numRanges;
            }
            this.rangeAdv = new double[sz];
            this.ranges = new double[2 * sz];
        }
        for (int r = 0; r < this.numRanges; ++r) {
            final double[] rangeBounds = this.fr.nextRange();
            double r2 = rangeBounds[0];
            if (r == 0) {
                double delta = this.bi.getLeftMargin();
                if (this.paraStart) {
                    final double indent = this.bi.getIndent();
                    if (delta < -indent) {
                        delta = 0.0;
                    }
                    else {
                        delta += indent;
                    }
                }
                r2 += delta;
            }
            double r3 = rangeBounds[1];
            if (r == this.numRanges - 1) {
                r3 -= this.bi.getRightMargin();
            }
            this.ranges[2 * r] = r2;
            this.ranges[2 * r + 1] = r3;
        }
        return true;
    }
    
    protected void swapGlyphGroupInfo() {
        final GlyphGroupInfo[] tmp = this.ggis;
        this.ggis = this.newGGIS;
        this.newGGIS = tmp;
        this.size = this.newSize;
        this.newSize = 0;
    }
    
    protected void mergeGlyphGroups(final WordInfo wi) {
        final int numGG = wi.getNumGlyphGroups();
        this.newSize = 0;
        if (this.ggis == null) {
            this.newSize = numGG;
            this.newGGIS = new GlyphGroupInfo[numGG];
            for (int i = 0; i < numGG; ++i) {
                this.newGGIS[i] = wi.getGlyphGroup(i);
            }
        }
        else {
            int s = 0;
            int j = 0;
            GlyphGroupInfo nggi = wi.getGlyphGroup(j);
            int nStart = nggi.getStart();
            GlyphGroupInfo oggi = this.ggis[this.size - 1];
            int oStart = oggi.getStart();
            this.newGGIS = assureSize(this.newGGIS, this.size + numGG);
            if (nStart < oStart) {
                oggi = this.ggis[s];
                oStart = oggi.getStart();
                while (s < this.size && j < numGG) {
                    if (nStart < oStart) {
                        this.newGGIS[this.newSize++] = nggi;
                        if (++j >= numGG) {
                            continue;
                        }
                        nggi = wi.getGlyphGroup(j);
                        nStart = nggi.getStart();
                    }
                    else {
                        this.newGGIS[this.newSize++] = oggi;
                        if (++s >= this.size) {
                            continue;
                        }
                        oggi = this.ggis[s];
                        oStart = oggi.getStart();
                    }
                }
            }
            while (s < this.size) {
                this.newGGIS[this.newSize++] = this.ggis[s++];
            }
            while (j < numGG) {
                this.newGGIS[this.newSize++] = wi.getGlyphGroup(j++);
            }
        }
    }
    
    public void layout() {
        if (this.size == 0) {
            return;
        }
        this.assignGlyphGroupRanges(this.size, this.ggis);
        final GVTGlyphVector gv = this.ggis[0].getGlyphVector();
        final int justType = 0;
        double ggAdv = 0.0;
        double gAdv = 0.0;
        final int[] rangeGG = new int[this.numRanges];
        final int[] rangeG = new int[this.numRanges];
        final GlyphGroupInfo[] rangeLastGGI = new GlyphGroupInfo[this.numRanges];
        GlyphGroupInfo ggi = this.ggis[0];
        int r = ggi.getRange();
        final int[] array = rangeGG;
        final int n = r;
        ++array[n];
        final int[] array2 = rangeG;
        final int n2 = r;
        array2[n2] += ggi.getGlyphCount();
        for (int i = 1; i < this.size; ++i) {
            ggi = this.ggis[i];
            r = ggi.getRange();
            if (rangeLastGGI[r] == null || !rangeLastGGI[r].getHideLast()) {
                final int[] array3 = rangeGG;
                final int n3 = r;
                ++array3[n3];
            }
            rangeLastGGI[r] = ggi;
            final int[] array4 = rangeG;
            final int n4 = r;
            array4[n4] += ggi.getGlyphCount();
            final GlyphGroupInfo pggi = this.ggis[i - 1];
            final int pr = pggi.getRange();
            if (r != pr) {
                final int[] array5 = rangeG;
                final int n5 = pr;
                array5[n5] += pggi.getLastGlyphCount() - pggi.getGlyphCount();
            }
        }
        final int[] array6 = rangeG;
        final int n6 = r;
        array6[n6] += ggi.getLastGlyphCount() - ggi.getGlyphCount();
        int currRange = -1;
        double locX = 0.0;
        double range = 0.0;
        double rAdv = 0.0;
        r = -1;
        ggi = null;
        for (int j = 0; j < this.size; ++j) {
            final GlyphGroupInfo pggi2 = ggi;
            final int prevRange = currRange;
            ggi = this.ggis[j];
            currRange = ggi.getRange();
            if (currRange != prevRange) {
                locX = this.ranges[2 * currRange];
                range = this.ranges[2 * currRange + 1] - locX;
                rAdv = this.rangeAdv[currRange];
                int textAlign = this.bi.getTextAlignment();
                if (this.paraEnd && textAlign == 3) {
                    textAlign = 0;
                }
                switch (textAlign) {
                    default: {
                        final double delta = range - rAdv;
                        if (justType == 0) {
                            final int numSp = rangeGG[currRange] - 1;
                            if (numSp >= 1) {
                                ggAdv = delta / numSp;
                            }
                        }
                        else {
                            final int numSp = rangeG[currRange] - 1;
                            if (numSp >= 1) {
                                gAdv = delta / numSp;
                            }
                        }
                        break;
                    }
                    case 0: {
                        break;
                    }
                    case 1: {
                        locX += (range - rAdv) / 2.0;
                        break;
                    }
                    case 2: {
                        locX += range - rAdv;
                        break;
                    }
                }
            }
            else if (pggi2 != null && pggi2.getHideLast()) {
                gv.setGlyphVisible(pggi2.getEnd(), false);
            }
            final int start = ggi.getStart();
            final int end = ggi.getEnd();
            final boolean[] hide = ggi.getHide();
            Point2D p2d = gv.getGlyphPosition(start);
            final double deltaX = p2d.getX();
            double advAdj = 0.0;
            for (int g = start; g <= end; ++g) {
                final Point2D np2d = gv.getGlyphPosition(g + 1);
                if (hide[g - start]) {
                    gv.setGlyphVisible(g, false);
                    advAdj += np2d.getX() - p2d.getX();
                }
                else {
                    gv.setGlyphVisible(g, true);
                }
                p2d.setLocation(p2d.getX() - deltaX - advAdj + locX, p2d.getY() + this.baseline);
                gv.setGlyphPosition(g, p2d);
                p2d = np2d;
                advAdj -= gAdv;
            }
            if (ggi.getHideLast()) {
                locX += ggi.getAdvance() - advAdj;
            }
            else {
                locX += ggi.getAdvance() - advAdj + ggAdv;
            }
        }
    }
    
    public static GlyphGroupInfo[] assureSize(final GlyphGroupInfo[] ggis, int sz) {
        if (ggis == null) {
            if (sz < 10) {
                sz = 10;
            }
            return new GlyphGroupInfo[sz];
        }
        if (sz <= ggis.length) {
            return ggis;
        }
        int nsz = ggis.length * 2;
        if (nsz < sz) {
            nsz = sz;
        }
        return new GlyphGroupInfo[nsz];
    }
}
