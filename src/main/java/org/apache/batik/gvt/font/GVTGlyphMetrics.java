// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.geom.Rectangle2D;
import java.awt.font.GlyphMetrics;

public class GVTGlyphMetrics
{
    private GlyphMetrics gm;
    private float verticalAdvance;
    
    public GVTGlyphMetrics(final GlyphMetrics gm, final float verticalAdvance) {
        this.gm = gm;
        this.verticalAdvance = verticalAdvance;
    }
    
    public GVTGlyphMetrics(final float horizontalAdvance, final float verticalAdvance, final Rectangle2D bounds, final byte glyphType) {
        this.gm = new GlyphMetrics(horizontalAdvance, bounds, glyphType);
        this.verticalAdvance = verticalAdvance;
    }
    
    public float getHorizontalAdvance() {
        return this.gm.getAdvance();
    }
    
    public float getVerticalAdvance() {
        return this.verticalAdvance;
    }
    
    public Rectangle2D getBounds2D() {
        return this.gm.getBounds2D();
    }
    
    public float getLSB() {
        return this.gm.getLSB();
    }
    
    public float getRSB() {
        return this.gm.getRSB();
    }
    
    public int getType() {
        return this.gm.getType();
    }
    
    public boolean isCombining() {
        return this.gm.isCombining();
    }
    
    public boolean isComponent() {
        return this.gm.isComponent();
    }
    
    public boolean isLigature() {
        return this.gm.isLigature();
    }
    
    public boolean isStandard() {
        return this.gm.isStandard();
    }
    
    public boolean isWhitespace() {
        return this.gm.isWhitespace();
    }
}
