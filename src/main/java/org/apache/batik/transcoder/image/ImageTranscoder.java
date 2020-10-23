// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.image;

import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.PaintKey;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.gvt.renderer.ImageRendererFactory;
import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.TranscoderException;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Paint;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.geom.Rectangle2D;
import org.apache.batik.transcoder.TranscoderOutput;
import org.w3c.dom.Document;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.SVGAbstractTranscoder;

public abstract class ImageTranscoder extends SVGAbstractTranscoder
{
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR;
    public static final TranscodingHints.Key KEY_FORCE_TRANSPARENT_WHITE;
    
    protected ImageTranscoder() {
    }
    
    @Override
    protected void transcode(final Document document, final String uri, final TranscoderOutput output) throws TranscoderException {
        super.transcode(document, uri, output);
        final int w = (int)(this.width + 0.5);
        final int h = (int)(this.height + 0.5);
        ImageRenderer renderer = this.createRenderer();
        renderer.updateOffScreen(w, h);
        renderer.setTransform(this.curTxf);
        renderer.setTree(this.root);
        this.root = null;
        try {
            final Shape raoi = new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height);
            renderer.repaint(this.curTxf.createInverse().createTransformedShape(raoi));
            BufferedImage rend = renderer.getOffScreen();
            renderer = null;
            final BufferedImage dest = this.createImage(w, h);
            final Graphics2D g2d = GraphicsUtil.createGraphics(dest);
            if (this.hints.containsKey(ImageTranscoder.KEY_BACKGROUND_COLOR)) {
                final Paint bgcolor = (Paint)this.hints.get(ImageTranscoder.KEY_BACKGROUND_COLOR);
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setPaint(bgcolor);
                g2d.fillRect(0, 0, w, h);
            }
            if (rend != null) {
                g2d.drawRenderedImage(rend, new AffineTransform());
            }
            g2d.dispose();
            rend = null;
            this.writeImage(dest, output);
        }
        catch (Exception ex) {
            throw new TranscoderException(ex);
        }
    }
    
    protected ImageRenderer createRenderer() {
        final ImageRendererFactory rendFactory = new ConcreteImageRendererFactory();
        return rendFactory.createStaticImageRenderer();
    }
    
    protected void forceTransparentWhite(final BufferedImage img, final SinglePixelPackedSampleModel sppsm) {
        final int w = img.getWidth();
        final int h = img.getHeight();
        final DataBufferInt biDB = (DataBufferInt)img.getRaster().getDataBuffer();
        final int scanStride = sppsm.getScanlineStride();
        final int dbOffset = biDB.getOffset();
        final int[] pixels = biDB.getBankData()[0];
        int p = dbOffset;
        final int adjust = scanStride - w;
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int pel = 0;
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                pel = pixels[p];
                a = (pel >> 24 & 0xFF);
                r = (pel >> 16 & 0xFF);
                g = (pel >> 8 & 0xFF);
                b = (pel & 0xFF);
                r = (255 * (255 - a) + a * r) / 255;
                g = (255 * (255 - a) + a * g) / 255;
                b = (255 * (255 - a) + a * b) / 255;
                pixels[p++] = ((a << 24 & 0xFF000000) | (r << 16 & 0xFF0000) | (g << 8 & 0xFF00) | (b & 0xFF));
            }
            p += adjust;
        }
    }
    
    public abstract BufferedImage createImage(final int p0, final int p1);
    
    public abstract void writeImage(final BufferedImage p0, final TranscoderOutput p1) throws TranscoderException;
    
    static {
        KEY_BACKGROUND_COLOR = new PaintKey();
        KEY_FORCE_TRANSPARENT_WHITE = new BooleanKey();
    }
}
