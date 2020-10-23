// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

public final class TextureFactory
{
    private static TextureFactory fac;
    private Map textures;
    private static final int SIZE = 10;
    private float scale;
    
    private TextureFactory(final float scale) {
        this.textures = new HashMap(1);
        this.scale = 1.0f;
    }
    
    public static TextureFactory getInstance() {
        if (TextureFactory.fac == null) {
            TextureFactory.fac = new TextureFactory(1.0f);
        }
        return TextureFactory.fac;
    }
    
    public static TextureFactory getInstance(final float scale) {
        if (TextureFactory.fac == null) {
            TextureFactory.fac = new TextureFactory(scale);
        }
        return TextureFactory.fac;
    }
    
    public void reset() {
        this.textures.clear();
    }
    
    public Paint getTexture(final int textureId) {
        final Integer _itexture = textureId;
        if (this.textures.containsKey(_itexture)) {
            final Paint paint = this.textures.get(_itexture);
            return paint;
        }
        final Paint paint = this.createTexture(textureId, null, null);
        if (paint != null) {
            this.textures.put(_itexture, paint);
        }
        return paint;
    }
    
    public Paint getTexture(final int textureId, final Color foreground) {
        final ColoredTexture _ctexture = new ColoredTexture(textureId, foreground, null);
        if (this.textures.containsKey(_ctexture)) {
            final Paint paint = this.textures.get(_ctexture);
            return paint;
        }
        final Paint paint = this.createTexture(textureId, foreground, null);
        if (paint != null) {
            this.textures.put(_ctexture, paint);
        }
        return paint;
    }
    
    public Paint getTexture(final int textureId, final Color foreground, final Color background) {
        final ColoredTexture _ctexture = new ColoredTexture(textureId, foreground, background);
        if (this.textures.containsKey(_ctexture)) {
            final Paint paint = this.textures.get(_ctexture);
            return paint;
        }
        final Paint paint = this.createTexture(textureId, foreground, background);
        if (paint != null) {
            this.textures.put(_ctexture, paint);
        }
        return paint;
    }
    
    private Paint createTexture(final int textureId, final Color foreground, final Color background) {
        final BufferedImage img = new BufferedImage(10, 10, 2);
        final Graphics2D g2d = img.createGraphics();
        final Rectangle2D rec = new Rectangle2D.Float(0.0f, 0.0f, 10.0f, 10.0f);
        Paint paint = null;
        boolean ok = false;
        if (background != null) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, 10, 10);
        }
        if (foreground == null) {
            g2d.setColor(Color.black);
        }
        else {
            g2d.setColor(foreground);
        }
        if (textureId == 1) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(i * 10, 0, i * 10, 10);
            }
            ok = true;
        }
        else if (textureId == 0) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(0, i * 10, 10, i * 10);
            }
            ok = true;
        }
        else if (textureId == 3) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(0, i * 10, i * 10, 0);
            }
            ok = true;
        }
        else if (textureId == 2) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(0, i * 10, 10 - i * 10, 10);
            }
            ok = true;
        }
        else if (textureId == 5) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(0, i * 10, i * 10, 0);
                g2d.drawLine(0, i * 10, 10 - i * 10, 10);
            }
            ok = true;
        }
        else if (textureId == 4) {
            for (int i = 0; i < 5; ++i) {
                g2d.drawLine(i * 10, 0, i * 10, 10);
                g2d.drawLine(0, i * 10, 10, i * 10);
            }
            ok = true;
        }
        img.flush();
        if (ok) {
            paint = new TexturePaint(img, rec);
        }
        return paint;
    }
    
    static {
        TextureFactory.fac = null;
    }
    
    private static class ColoredTexture
    {
        final int textureId;
        final Color foreground;
        final Color background;
        
        ColoredTexture(final int textureId, final Color foreground, final Color background) {
            this.textureId = textureId;
            this.foreground = foreground;
            this.background = background;
        }
    }
}
