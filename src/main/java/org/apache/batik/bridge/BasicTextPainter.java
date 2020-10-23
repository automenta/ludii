// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;

public abstract class BasicTextPainter implements TextPainter
{
    private static TextLayoutFactory textLayoutFactory;
    protected FontRenderContext fontRenderContext;
    protected FontRenderContext aaOffFontRenderContext;
    
    public BasicTextPainter() {
        this.fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
        this.aaOffFontRenderContext = new FontRenderContext(new AffineTransform(), false, true);
    }
    
    protected TextLayoutFactory getTextLayoutFactory() {
        return BasicTextPainter.textLayoutFactory;
    }
    
    @Override
    public Mark selectAt(final double x, final double y, final TextNode node) {
        return this.hitTest(x, y, node);
    }
    
    @Override
    public Mark selectTo(final double x, final double y, final Mark beginMark) {
        if (beginMark == null) {
            return null;
        }
        return this.hitTest(x, y, beginMark.getTextNode());
    }
    
    @Override
    public Rectangle2D getGeometryBounds(final TextNode node) {
        return this.getOutline(node).getBounds2D();
    }
    
    protected abstract Mark hitTest(final double p0, final double p1, final TextNode p2);
    
    static {
        BasicTextPainter.textLayoutFactory = new ConcreteTextLayoutFactory();
    }
    
    protected static class BasicMark implements Mark
    {
        private TextNode node;
        private TextHit hit;
        
        protected BasicMark(final TextNode node, final TextHit hit) {
            this.hit = hit;
            this.node = node;
        }
        
        public TextHit getHit() {
            return this.hit;
        }
        
        @Override
        public TextNode getTextNode() {
            return this.node;
        }
        
        @Override
        public int getCharIndex() {
            return this.hit.getCharIndex();
        }
    }
}
