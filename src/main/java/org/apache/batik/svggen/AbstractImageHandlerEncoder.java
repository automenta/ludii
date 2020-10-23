// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.Dimension;
import java.awt.image.ImageObserver;
import org.w3c.dom.Element;
import java.awt.Image;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.awt.geom.AffineTransform;

public abstract class AbstractImageHandlerEncoder extends DefaultImageHandler
{
    private static final AffineTransform IDENTITY;
    private String imageDir;
    private String urlRoot;
    private static Method createGraphics;
    private static boolean initDone;
    private static final Class[] paramc;
    private static Object[] paramo;
    
    private static Graphics2D createGraphics(final BufferedImage buf) {
        if (!AbstractImageHandlerEncoder.initDone) {
            try {
                final Class clazz = Class.forName("org.apache.batik.ext.awt.image.GraphicsUtil");
                AbstractImageHandlerEncoder.createGraphics = clazz.getMethod("createGraphics", (Class[])AbstractImageHandlerEncoder.paramc);
                AbstractImageHandlerEncoder.paramo = new Object[1];
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable t) {}
            finally {
                AbstractImageHandlerEncoder.initDone = true;
            }
        }
        if (AbstractImageHandlerEncoder.createGraphics == null) {
            return buf.createGraphics();
        }
        AbstractImageHandlerEncoder.paramo[0] = buf;
        Graphics2D g2d = null;
        try {
            g2d = (Graphics2D)AbstractImageHandlerEncoder.createGraphics.invoke(null, AbstractImageHandlerEncoder.paramo);
        }
        catch (Exception ex) {}
        return g2d;
    }
    
    public AbstractImageHandlerEncoder(final String imageDir, final String urlRoot) throws SVGGraphics2DIOException {
        this.imageDir = "";
        this.urlRoot = "";
        if (imageDir == null) {
            throw new SVGGraphics2DRuntimeException("imageDir should not be null");
        }
        final File imageDirFile = new File(imageDir);
        if (!imageDirFile.exists()) {
            throw new SVGGraphics2DRuntimeException("imageDir does not exist");
        }
        this.imageDir = imageDir;
        if (urlRoot != null) {
            this.urlRoot = urlRoot;
        }
        else {
            try {
                this.urlRoot = imageDirFile.toURI().toURL().toString();
            }
            catch (MalformedURLException e) {
                throw new SVGGraphics2DIOException("cannot convert imageDir to a URL value : " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    protected void handleHREF(final Image image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        final Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        final BufferedImage buf = this.buildBufferedImage(size);
        final Graphics2D g = createGraphics(buf);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }
    
    @Override
    protected void handleHREF(final RenderedImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        final Dimension size = new Dimension(image.getWidth(), image.getHeight());
        final BufferedImage buf = this.buildBufferedImage(size);
        final Graphics2D g = createGraphics(buf);
        g.drawRenderedImage(image, AbstractImageHandlerEncoder.IDENTITY);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }
    
    @Override
    protected void handleHREF(final RenderableImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        final Dimension size = new Dimension((int)Math.ceil(image.getWidth()), (int)Math.ceil(image.getHeight()));
        final BufferedImage buf = this.buildBufferedImage(size);
        final Graphics2D g = createGraphics(buf);
        g.drawRenderableImage(image, AbstractImageHandlerEncoder.IDENTITY);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }
    
    private void saveBufferedImageToFile(final Element imageElement, final BufferedImage buf, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        File imageFile;
        for (imageFile = null; imageFile == null; imageFile = null) {
            final String fileId = generatorContext.idGenerator.generateID(this.getPrefix());
            imageFile = new File(this.imageDir, fileId + this.getSuffix());
            if (imageFile.exists()) {}
        }
        this.encodeImage(buf, imageFile);
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", this.urlRoot + "/" + imageFile.getName());
    }
    
    public abstract String getSuffix();
    
    public abstract String getPrefix();
    
    public abstract void encodeImage(final BufferedImage p0, final File p1) throws SVGGraphics2DIOException;
    
    public abstract BufferedImage buildBufferedImage(final Dimension p0);
    
    static {
        IDENTITY = new AffineTransform();
        AbstractImageHandlerEncoder.createGraphics = null;
        AbstractImageHandlerEncoder.initDone = false;
        paramc = new Class[] { BufferedImage.class };
        AbstractImageHandlerEncoder.paramo = null;
    }
}
