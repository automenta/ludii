// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.g2d;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class DefaultGraphics2D extends AbstractGraphics2D
{
    private Graphics2D fmg;
    
    public DefaultGraphics2D(final boolean textAsShapes) {
        super(textAsShapes);
        final BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
    }
    
    public DefaultGraphics2D(final DefaultGraphics2D g) {
        super(g);
        final BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
    }
    
    @Override
    public Graphics create() {
        return new DefaultGraphics2D(this);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        System.err.println("drawImage");
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        System.out.println("drawImage");
        return true;
    }
    
    @Override
    public void dispose() {
        System.out.println("dispose");
    }
    
    @Override
    public void draw(final Shape s) {
        System.out.println("draw(Shape)");
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        System.out.println("drawRenderedImage");
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        System.out.println("drawRenderableImage");
    }
    
    @Override
    public void drawString(final String s, final float x, final float y) {
        System.out.println("drawString(String)");
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
        System.err.println("drawString(AttributedCharacterIterator)");
    }
    
    @Override
    public void fill(final Shape s) {
        System.err.println("fill");
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        System.out.println("getDeviceConfiguration");
        return null;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return this.fmg.getFontMetrics(f);
    }
    
    @Override
    public void setXORMode(final Color c1) {
        System.out.println("setXORMode");
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        System.out.println("copyArea");
    }
}
