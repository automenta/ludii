// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class SVGGraphicsConfiguration extends GraphicsConfiguration
{
    private GraphicsDevice device;
    private final int width;
    private final int height;
    private BufferedImage img;
    private GraphicsConfiguration gc;
    
    public SVGGraphicsConfiguration(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public GraphicsDevice getDevice() {
        if (this.device == null) {
            this.device = new SVGGraphicsDevice("JFreeSVG-GraphicsDevice", this);
        }
        return this.device;
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.getColorModel(3);
    }
    
    @Override
    public ColorModel getColorModel(final int transparency) {
        if (transparency == 3) {
            return ColorModel.getRGBdefault();
        }
        if (transparency == 1) {
            return new DirectColorModel(32, 16711680, 65280, 255);
        }
        return null;
    }
    
    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }
    
    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.width, this.height);
    }
    
    @Override
    public BufferedImage createCompatibleImage(final int width, final int height) {
        final ColorModel model = this.getColorModel();
        final WritableRaster raster = model.createCompatibleWritableRaster(width, height);
        return new BufferedImage(model, raster, model.isAlphaPremultiplied(), null);
    }
    
    @Override
    public VolatileImage createCompatibleVolatileImage(final int width, final int height, final ImageCapabilities caps, final int transparency) throws AWTException {
        if (this.img == null) {
            this.img = new BufferedImage(1, 1, 2);
            this.gc = this.img.createGraphics().getDeviceConfiguration();
        }
        return this.gc.createCompatibleVolatileImage(width, height, caps, transparency);
    }
}
