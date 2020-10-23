// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import org.apache.batik.gvt.font.GVTLineMetrics;
import java.util.Iterator;
import org.apache.batik.gvt.font.GVTFont;
import java.awt.font.TextAttribute;
import java.awt.font.FontRenderContext;
import java.util.Map;
import java.util.List;

public class BlockInfo
{
    public static final int ALIGN_START = 0;
    public static final int ALIGN_MIDDLE = 1;
    public static final int ALIGN_END = 2;
    public static final int ALIGN_FULL = 3;
    protected float top;
    protected float right;
    protected float bottom;
    protected float left;
    protected float indent;
    protected int alignment;
    protected float lineHeight;
    protected List fontList;
    protected Map fontAttrs;
    protected float ascent;
    protected float descent;
    protected boolean flowRegionBreak;
    
    public BlockInfo(final float top, final float right, final float bottom, final float left, final float indent, final int alignment, final float lineHeight, final List fontList, final Map fontAttrs, final boolean flowRegionBreak) {
        this.ascent = -1.0f;
        this.descent = -1.0f;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.indent = indent;
        this.alignment = alignment;
        this.lineHeight = lineHeight;
        this.fontList = fontList;
        this.fontAttrs = fontAttrs;
        this.flowRegionBreak = flowRegionBreak;
    }
    
    public BlockInfo(final float margin, final int alignment) {
        this.ascent = -1.0f;
        this.descent = -1.0f;
        this.setMargin(margin);
        this.indent = 0.0f;
        this.alignment = alignment;
        this.flowRegionBreak = false;
    }
    
    public void setMargin(final float margin) {
        this.top = margin;
        this.right = margin;
        this.bottom = margin;
        this.left = margin;
    }
    
    public void initLineInfo(final FontRenderContext frc) {
        float fontSize = 12.0f;
        final Float fsFloat = this.fontAttrs.get(TextAttribute.SIZE);
        if (fsFloat != null) {
            fontSize = fsFloat;
        }
        final Iterator i$ = this.fontList.iterator();
        if (i$.hasNext()) {
            final Object aFontList = i$.next();
            final GVTFont font = (GVTFont)aFontList;
            final GVTLineMetrics lm = font.getLineMetrics("", frc);
            this.ascent = lm.getAscent();
            this.descent = lm.getDescent();
        }
        if (this.ascent == -1.0f) {
            this.ascent = fontSize * 0.8f;
            this.descent = fontSize * 0.2f;
        }
    }
    
    public float getTopMargin() {
        return this.top;
    }
    
    public float getRightMargin() {
        return this.right;
    }
    
    public float getBottomMargin() {
        return this.bottom;
    }
    
    public float getLeftMargin() {
        return this.left;
    }
    
    public float getIndent() {
        return this.indent;
    }
    
    public int getTextAlignment() {
        return this.alignment;
    }
    
    public float getLineHeight() {
        return this.lineHeight;
    }
    
    public List getFontList() {
        return this.fontList;
    }
    
    public Map getFontAttrs() {
        return this.fontAttrs;
    }
    
    public float getAscent() {
        return this.ascent;
    }
    
    public float getDescent() {
        return this.descent;
    }
    
    public boolean isFlowRegionBreak() {
        return this.flowRegionBreak;
    }
}
