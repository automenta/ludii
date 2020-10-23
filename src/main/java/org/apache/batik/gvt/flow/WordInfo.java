// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.GVTFont;

public class WordInfo
{
    int index;
    float ascent;
    float descent;
    float lineHeight;
    GlyphGroupInfo[] glyphGroups;
    Object flowLine;
    
    public WordInfo(final int index) {
        this.index = -1;
        this.ascent = -1.0f;
        this.descent = -1.0f;
        this.lineHeight = -1.0f;
        this.glyphGroups = null;
        this.flowLine = null;
        this.index = index;
    }
    
    WordInfo(final int index, final float ascent, final float descent, final float lineHeight, final GlyphGroupInfo[] glyphGroups) {
        this.index = -1;
        this.ascent = -1.0f;
        this.descent = -1.0f;
        this.lineHeight = -1.0f;
        this.glyphGroups = null;
        this.flowLine = null;
        this.index = index;
        this.ascent = ascent;
        this.descent = descent;
        this.lineHeight = lineHeight;
        this.glyphGroups = glyphGroups;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public float getAscent() {
        return this.ascent;
    }
    
    public void setAscent(final float ascent) {
        this.ascent = ascent;
    }
    
    public float getDescent() {
        return this.descent;
    }
    
    public void setDescent(final float descent) {
        this.descent = descent;
    }
    
    public void addLineMetrics(final GVTFont font, final GVTLineMetrics lm) {
        if (this.ascent < lm.getAscent()) {
            this.ascent = lm.getAscent();
        }
        if (this.descent < lm.getDescent()) {
            this.descent = lm.getDescent();
        }
    }
    
    public float getLineHeight() {
        return this.lineHeight;
    }
    
    public void setLineHeight(final float lineHeight) {
        this.lineHeight = lineHeight;
    }
    
    public void addLineHeight(final float lineHeight) {
        if (this.lineHeight < lineHeight) {
            this.lineHeight = lineHeight;
        }
    }
    
    public Object getFlowLine() {
        return this.flowLine;
    }
    
    public void setFlowLine(final Object fl) {
        this.flowLine = fl;
    }
    
    public int getNumGlyphGroups() {
        if (this.glyphGroups == null) {
            return -1;
        }
        return this.glyphGroups.length;
    }
    
    public void setGlyphGroups(final GlyphGroupInfo[] glyphGroups) {
        this.glyphGroups = glyphGroups;
    }
    
    public GlyphGroupInfo getGlyphGroup(final int idx) {
        if (this.glyphGroups == null) {
            return null;
        }
        return this.glyphGroups[idx];
    }
}
