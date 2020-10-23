// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Dimension;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import java.awt.Image;
import org.w3c.dom.Element;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.awt.geom.AffineTransform;

public abstract class DefaultCachedImageHandler implements CachedImageHandler, SVGSyntax, ErrorConstants
{
    static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";
    static final AffineTransform IDENTITY;
    private static Method createGraphics;
    private static boolean initDone;
    private static final Class[] paramc;
    private static Object[] paramo;
    protected ImageCacher imageCacher;
    
    @Override
    public ImageCacher getImageCacher() {
        return this.imageCacher;
    }
    
    void setImageCacher(final ImageCacher imageCacher) {
        if (imageCacher == null) {
            throw new IllegalArgumentException();
        }
        DOMTreeManager dtm = null;
        if (this.imageCacher != null) {
            dtm = this.imageCacher.getDOMTreeManager();
        }
        this.imageCacher = imageCacher;
        if (dtm != null) {
            this.imageCacher.setDOMTreeManager(dtm);
        }
    }
    
    @Override
    public void setDOMTreeManager(final DOMTreeManager domTreeManager) {
        this.imageCacher.setDOMTreeManager(domTreeManager);
    }
    
    private static Graphics2D createGraphics(final BufferedImage buf) {
        if (!DefaultCachedImageHandler.initDone) {
            try {
                final Class clazz = Class.forName("org.apache.batik.ext.awt.image.GraphicsUtil");
                DefaultCachedImageHandler.createGraphics = clazz.getMethod("createGraphics", (Class[])DefaultCachedImageHandler.paramc);
                DefaultCachedImageHandler.paramo = new Object[1];
            }
            catch (Throwable t) {}
            finally {
                DefaultCachedImageHandler.initDone = true;
            }
        }
        if (DefaultCachedImageHandler.createGraphics == null) {
            return buf.createGraphics();
        }
        DefaultCachedImageHandler.paramo[0] = buf;
        Graphics2D g2d = null;
        try {
            g2d = (Graphics2D)DefaultCachedImageHandler.createGraphics.invoke(null, DefaultCachedImageHandler.paramo);
        }
        catch (Exception ex) {}
        return g2d;
    }
    
    @Override
    public Element createElement(final SVGGeneratorContext generatorContext) {
        final Element imageElement = generatorContext.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "image");
        return imageElement;
    }
    
    @Override
    public AffineTransform handleImage(final Image image, final Element imageElement, final int x, final int y, final int width, final int height, final SVGGeneratorContext generatorContext) {
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);
        AffineTransform af = null;
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            try {
                this.handleHREF(image, imageElement, generatorContext);
            }
            catch (SVGGraphics2DIOException e) {
                try {
                    generatorContext.errorHandler.handleError(e);
                }
                catch (SVGGraphics2DIOException io) {
                    throw new SVGGraphics2DRuntimeException(io);
                }
            }
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }
    
    @Override
    public AffineTransform handleImage(final RenderedImage image, final Element imageElement, final int x, final int y, final int width, final int height, final SVGGeneratorContext generatorContext) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        AffineTransform af = null;
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            try {
                this.handleHREF(image, imageElement, generatorContext);
            }
            catch (SVGGraphics2DIOException e) {
                try {
                    generatorContext.errorHandler.handleError(e);
                }
                catch (SVGGraphics2DIOException io) {
                    throw new SVGGraphics2DRuntimeException(io);
                }
            }
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }
    
    @Override
    public AffineTransform handleImage(final RenderableImage image, final Element imageElement, final double x, final double y, final double width, final double height, final SVGGeneratorContext generatorContext) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();
        AffineTransform af = null;
        if (imageWidth == 0.0 || imageHeight == 0.0 || width == 0.0 || height == 0.0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            try {
                this.handleHREF(image, imageElement, generatorContext);
            }
            catch (SVGGraphics2DIOException e) {
                try {
                    generatorContext.errorHandler.handleError(e);
                }
                catch (SVGGraphics2DIOException io) {
                    throw new SVGGraphics2DRuntimeException(io);
                }
            }
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }
    
    protected AffineTransform handleTransform(final Element imageElement, final double x, final double y, final double srcWidth, final double srcHeight, final double dstWidth, final double dstHeight, final SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "x", generatorContext.doubleString(x));
        imageElement.setAttributeNS(null, "y", generatorContext.doubleString(y));
        imageElement.setAttributeNS(null, "width", generatorContext.doubleString(dstWidth));
        imageElement.setAttributeNS(null, "height", generatorContext.doubleString(dstHeight));
        return null;
    }
    
    protected void handleEmptyImage(final Element imageElement) {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }
    
    public void handleHREF(final Image image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else if (image instanceof RenderedImage) {
            this.handleHREF((RenderedImage)image, imageElement, generatorContext);
        }
        else {
            final BufferedImage buf = this.buildBufferedImage(new Dimension(width, height));
            final Graphics2D g = createGraphics(buf);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.handleHREF((RenderedImage)buf, imageElement, generatorContext);
        }
    }
    
    public BufferedImage buildBufferedImage(final Dimension size) {
        return new BufferedImage(size.width, size.height, this.getBufferedImageType());
    }
    
    protected void handleHREF(final RenderedImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        BufferedImage buf = null;
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == this.getBufferedImageType()) {
            buf = (BufferedImage)image;
        }
        else {
            final Dimension size = new Dimension(image.getWidth(), image.getHeight());
            buf = this.buildBufferedImage(size);
            final Graphics2D g = createGraphics(buf);
            g.drawRenderedImage(image, DefaultCachedImageHandler.IDENTITY);
            g.dispose();
        }
        this.cacheBufferedImage(imageElement, buf, generatorContext);
    }
    
    protected void handleHREF(final RenderableImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        final Dimension size = new Dimension((int)Math.ceil(image.getWidth()), (int)Math.ceil(image.getHeight()));
        final BufferedImage buf = this.buildBufferedImage(size);
        final Graphics2D g = createGraphics(buf);
        g.drawRenderableImage(image, DefaultCachedImageHandler.IDENTITY);
        g.dispose();
        this.handleHREF((RenderedImage)buf, imageElement, generatorContext);
    }
    
    protected void cacheBufferedImage(final Element imageElement, final BufferedImage buf, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        ByteArrayOutputStream os;
        try {
            os = new ByteArrayOutputStream();
            this.encodeImage(buf, os);
            os.flush();
            os.close();
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception", e);
        }
        final String ref = this.imageCacher.lookup(os, buf.getWidth(), buf.getHeight(), generatorContext);
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", this.getRefPrefix() + ref);
    }
    
    public abstract String getRefPrefix();
    
    public abstract void encodeImage(final BufferedImage p0, final OutputStream p1) throws IOException;
    
    public abstract int getBufferedImageType();
    
    static {
        IDENTITY = new AffineTransform();
        DefaultCachedImageHandler.createGraphics = null;
        DefaultCachedImageHandler.initDone = false;
        paramc = new Class[] { BufferedImage.class };
        DefaultCachedImageHandler.paramo = null;
    }
}
