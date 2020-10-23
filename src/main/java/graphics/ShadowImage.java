// 
// Decompiled by Procyon v0.5.36
// 

package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;

public class ShadowImage extends BufferedImage
{
    public ShadowImage(final BufferedImage imgSrc, final float intensity, final Color color) {
        super(imgSrc.getWidth() * 2, imgSrc.getHeight() * 2, 2);
        this.render(imgSrc, intensity, color);
    }
    
    void render(final BufferedImage imgSrc, final float intensity, final Color color) {
        BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), 2);
        final Graphics2D g2d = (Graphics2D)img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final int x0 = imgSrc.getWidth() / 2;
        final int y0 = imgSrc.getHeight() / 2;
        final int off = 0;
        final Color clrMask = color;
        g2d.setPaint(clrMask);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        final Composite oldComp = g2d.getComposite();
        final float darkness = 0.666f * intensity;
        g2d.setComposite(AlphaComposite.getInstance(12, darkness));
        g2d.drawImage(imgSrc, x0 + off, y0 + off, null);
        g2d.setComposite(oldComp);
        final short[] invert = new short[256];
        final short[] straight = new short[256];
        for (int i = 0; i < 256; ++i) {
            invert[i] = (short)(255 - i);
            straight[i] = (short)i;
        }
        final short[][] alphaInvert = { straight, straight, straight, invert };
        final BufferedImageOp invertOp = new LookupOp(new ShortLookupTable(0, alphaInvert), null);
        img = invertOp.filter(img, null);
        final int blurRadius = Math.max(1, (imgSrc.getWidth() + imgSrc.getHeight()) / 8);
        final int NUM_PASSES = 1;
        for (int pass = 0; pass < 1; ++pass) {
            img = Filters.gaussianBlurFilter(blurRadius, true).filter(img, null);
            img = Filters.gaussianBlurFilter(blurRadius, false).filter(img, null);
        }
        this.getGraphics().drawImage(img, 0, 0, null);
    }
}
