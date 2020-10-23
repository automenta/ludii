// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import org.apache.batik.gvt.font.GVTGlyphVector;

public class GlyphGroupInfo
{
    int start;
    int end;
    int glyphCount;
    int lastGlyphCount;
    boolean hideLast;
    float advance;
    float lastAdvance;
    int range;
    GVTGlyphVector gv;
    boolean[] hide;
    
    public GlyphGroupInfo(final GVTGlyphVector gv, final int start, final int end, final boolean[] glyphHide, final boolean glyphGroupHideLast, final float[] glyphPos, final float[] advAdj, final float[] lastAdvAdj, final boolean[] space) {
        this.gv = gv;
        this.start = start;
        this.end = end;
        this.hide = new boolean[this.end - this.start + 1];
        this.hideLast = glyphGroupHideLast;
        System.arraycopy(glyphHide, this.start, this.hide, 0, this.hide.length);
        float ladv;
        float adv = ladv = glyphPos[2 * end + 2] - glyphPos[2 * start];
        adv += advAdj[end];
        int glyphCount = end - start + 1;
        for (int g = start; g < end; ++g) {
            if (glyphHide[g]) {
                --glyphCount;
            }
        }
        int lastGlyphCount = glyphCount;
        for (int g2 = end; g2 >= start; --g2) {
            ladv += lastAdvAdj[g2];
            if (!space[g2]) {
                break;
            }
            --lastGlyphCount;
        }
        if (this.hideLast) {
            --lastGlyphCount;
        }
        this.glyphCount = glyphCount;
        this.lastGlyphCount = lastGlyphCount;
        this.advance = adv;
        this.lastAdvance = ladv;
    }
    
    public GVTGlyphVector getGlyphVector() {
        return this.gv;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public int getGlyphCount() {
        return this.glyphCount;
    }
    
    public int getLastGlyphCount() {
        return this.lastGlyphCount;
    }
    
    public boolean[] getHide() {
        return this.hide;
    }
    
    public boolean getHideLast() {
        return this.hideLast;
    }
    
    public float getAdvance() {
        return this.advance;
    }
    
    public float getLastAdvance() {
        return this.lastAdvance;
    }
    
    public void setRange(final int range) {
        this.range = range;
    }
    
    public int getRange() {
        return this.range;
    }
}
