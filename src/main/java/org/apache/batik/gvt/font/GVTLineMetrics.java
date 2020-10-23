// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.font.LineMetrics;

public class GVTLineMetrics
{
    protected float ascent;
    protected int baselineIndex;
    protected float[] baselineOffsets;
    protected float descent;
    protected float height;
    protected float leading;
    protected int numChars;
    protected float strikethroughOffset;
    protected float strikethroughThickness;
    protected float underlineOffset;
    protected float underlineThickness;
    protected float overlineOffset;
    protected float overlineThickness;
    
    public GVTLineMetrics(final LineMetrics lineMetrics) {
        this.ascent = lineMetrics.getAscent();
        this.baselineIndex = lineMetrics.getBaselineIndex();
        this.baselineOffsets = lineMetrics.getBaselineOffsets();
        this.descent = lineMetrics.getDescent();
        this.height = lineMetrics.getHeight();
        this.leading = lineMetrics.getLeading();
        this.numChars = lineMetrics.getNumChars();
        this.strikethroughOffset = lineMetrics.getStrikethroughOffset();
        this.strikethroughThickness = lineMetrics.getStrikethroughThickness();
        this.underlineOffset = lineMetrics.getUnderlineOffset();
        this.underlineThickness = lineMetrics.getUnderlineThickness();
        this.overlineOffset = -this.ascent;
        this.overlineThickness = this.underlineThickness;
    }
    
    public GVTLineMetrics(final LineMetrics lineMetrics, final float scaleFactor) {
        this.ascent = lineMetrics.getAscent() * scaleFactor;
        this.baselineIndex = lineMetrics.getBaselineIndex();
        this.baselineOffsets = lineMetrics.getBaselineOffsets();
        for (int i = 0; i < this.baselineOffsets.length; ++i) {
            final float[] baselineOffsets = this.baselineOffsets;
            final int n = i;
            baselineOffsets[n] *= scaleFactor;
        }
        this.descent = lineMetrics.getDescent() * scaleFactor;
        this.height = lineMetrics.getHeight() * scaleFactor;
        this.leading = lineMetrics.getLeading();
        this.numChars = lineMetrics.getNumChars();
        this.strikethroughOffset = lineMetrics.getStrikethroughOffset() * scaleFactor;
        this.strikethroughThickness = lineMetrics.getStrikethroughThickness() * scaleFactor;
        this.underlineOffset = lineMetrics.getUnderlineOffset() * scaleFactor;
        this.underlineThickness = lineMetrics.getUnderlineThickness() * scaleFactor;
        this.overlineOffset = -this.ascent;
        this.overlineThickness = this.underlineThickness;
    }
    
    public GVTLineMetrics(final float ascent, final int baselineIndex, final float[] baselineOffsets, final float descent, final float height, final float leading, final int numChars, final float strikethroughOffset, final float strikethroughThickness, final float underlineOffset, final float underlineThickness, final float overlineOffset, final float overlineThickness) {
        this.ascent = ascent;
        this.baselineIndex = baselineIndex;
        this.baselineOffsets = baselineOffsets;
        this.descent = descent;
        this.height = height;
        this.leading = leading;
        this.numChars = numChars;
        this.strikethroughOffset = strikethroughOffset;
        this.strikethroughThickness = strikethroughThickness;
        this.underlineOffset = underlineOffset;
        this.underlineThickness = underlineThickness;
        this.overlineOffset = overlineOffset;
        this.overlineThickness = overlineThickness;
    }
    
    public float getAscent() {
        return this.ascent;
    }
    
    public int getBaselineIndex() {
        return this.baselineIndex;
    }
    
    public float[] getBaselineOffsets() {
        return this.baselineOffsets;
    }
    
    public float getDescent() {
        return this.descent;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public float getLeading() {
        return this.leading;
    }
    
    public int getNumChars() {
        return this.numChars;
    }
    
    public float getStrikethroughOffset() {
        return this.strikethroughOffset;
    }
    
    public float getStrikethroughThickness() {
        return this.strikethroughThickness;
    }
    
    public float getUnderlineOffset() {
        return this.underlineOffset;
    }
    
    public float getUnderlineThickness() {
        return this.underlineThickness;
    }
    
    public float getOverlineOffset() {
        return this.overlineOffset;
    }
    
    public float getOverlineThickness() {
        return this.overlineThickness;
    }
}
