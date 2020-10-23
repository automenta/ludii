// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImageOp;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.DirectColorModel;
import java.awt.color.ColorSpace;
import java.util.Hashtable;
import java.awt.image.ConvolveOp;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.Point;
import java.awt.image.Kernel;

public class ConvolveMatrixRable8Bit extends AbstractColorInterpolationRable implements ConvolveMatrixRable
{
    Kernel kernel;
    Point target;
    float bias;
    boolean kernelHasNegValues;
    PadMode edgeMode;
    float[] kernelUnitLength;
    boolean preserveAlpha;
    
    public ConvolveMatrixRable8Bit(final Filter source) {
        super(source);
        this.kernelUnitLength = new float[2];
        this.preserveAlpha = false;
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src);
    }
    
    @Override
    public Kernel getKernel() {
        return this.kernel;
    }
    
    @Override
    public void setKernel(final Kernel k) {
        this.touch();
        this.kernel = k;
        this.kernelHasNegValues = false;
        final float[] arr$;
        final float[] kv = arr$ = k.getKernelData(null);
        for (final float aKv : arr$) {
            if (aKv < 0.0f) {
                this.kernelHasNegValues = true;
                break;
            }
        }
    }
    
    @Override
    public Point getTarget() {
        return (Point)this.target.clone();
    }
    
    @Override
    public void setTarget(final Point pt) {
        this.touch();
        this.target = (Point)pt.clone();
    }
    
    @Override
    public double getBias() {
        return this.bias;
    }
    
    @Override
    public void setBias(final double bias) {
        this.touch();
        this.bias = (float)bias;
    }
    
    @Override
    public PadMode getEdgeMode() {
        return this.edgeMode;
    }
    
    @Override
    public void setEdgeMode(final PadMode edgeMode) {
        this.touch();
        this.edgeMode = edgeMode;
    }
    
    @Override
    public double[] getKernelUnitLength() {
        if (this.kernelUnitLength == null) {
            return null;
        }
        final double[] ret = { this.kernelUnitLength[0], this.kernelUnitLength[1] };
        return ret;
    }
    
    @Override
    public void setKernelUnitLength(final double[] kernelUnitLength) {
        this.touch();
        if (kernelUnitLength == null) {
            this.kernelUnitLength = null;
            return;
        }
        if (this.kernelUnitLength == null) {
            this.kernelUnitLength = new float[2];
        }
        this.kernelUnitLength[0] = (float)kernelUnitLength[0];
        this.kernelUnitLength[1] = (float)kernelUnitLength[1];
    }
    
    @Override
    public boolean getPreserveAlpha() {
        return this.preserveAlpha;
    }
    
    @Override
    public void setPreserveAlpha(final boolean preserveAlpha) {
        this.touch();
        this.preserveAlpha = preserveAlpha;
    }
    
    public void fixAlpha(final BufferedImage bi) {
        if (!bi.getColorModel().hasAlpha() || !bi.isAlphaPremultiplied()) {
            return;
        }
        if (GraphicsUtil.is_INT_PACK_Data(bi.getSampleModel(), true)) {
            this.fixAlpha_INT_PACK(bi.getRaster());
        }
        else {
            this.fixAlpha_FALLBACK(bi.getRaster());
        }
    }
    
    public void fixAlpha_INT_PACK(final WritableRaster wr) {
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int width = wr.getWidth();
        final int scanStride = sppsm.getScanlineStride();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base = db.getOffset() + sppsm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            for (int sp = base + y * scanStride, end = sp + width; sp < end; ++sp) {
                final int pixel = pixels[sp];
                int a = pixel >>> 24;
                int v = pixel >> 16 & 0xFF;
                if (a < v) {
                    a = v;
                }
                v = (pixel >> 8 & 0xFF);
                if (a < v) {
                    a = v;
                }
                v = (pixel & 0xFF);
                if (a < v) {
                    a = v;
                }
                pixels[sp] = ((pixel & 0xFFFFFF) | a << 24);
            }
        }
    }
    
    public void fixAlpha_FALLBACK(final WritableRaster wr) {
        final int x0 = wr.getMinX();
        final int w = wr.getWidth();
        final int y0 = wr.getMinY();
        final int y2 = y0 + wr.getHeight() - 1;
        final int bands = wr.getNumBands();
        int[] pixel = null;
        for (int y3 = y0; y3 <= y2; ++y3) {
            pixel = wr.getPixels(x0, y3, w, 1, pixel);
            int i = 0;
            for (int x2 = 0; x2 < w; ++x2) {
                int a = pixel[i];
                for (int b = 1; b < bands; ++b) {
                    if (pixel[i + b] > a) {
                        a = pixel[i + b];
                    }
                }
                pixel[i + bands - 1] = a;
                i += bands;
            }
            wr.setPixels(x0, y3, w, 1, pixel);
        }
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        final AffineTransform at = rc.getTransform();
        final double sx = at.getScaleX();
        final double sy = at.getScaleY();
        final double shx = at.getShearX();
        final double shy = at.getShearY();
        final double tx = at.getTranslateX();
        final double ty = at.getTranslateY();
        double scaleX = Math.sqrt(sx * sx + shy * shy);
        double scaleY = Math.sqrt(sy * sy + shx * shx);
        if (this.kernelUnitLength != null) {
            if (this.kernelUnitLength[0] > 0.0) {
                scaleX = 1.0f / this.kernelUnitLength[0];
            }
            if (this.kernelUnitLength[1] > 0.0) {
                scaleY = 1.0f / this.kernelUnitLength[1];
            }
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        Rectangle2D r = aoi.getBounds2D();
        final int kw = this.kernel.getWidth();
        final int kh = this.kernel.getHeight();
        final int kx = this.target.x;
        final int ky = this.target.y;
        final double rx0 = r.getX() - kx / scaleX;
        final double ry0 = r.getY() - ky / scaleY;
        final double rx2 = rx0 + r.getWidth() + (kw - 1) / scaleX;
        final double ry2 = ry0 + r.getHeight() + (kh - 1) / scaleY;
        r = new Rectangle2D.Double(Math.floor(rx0), Math.floor(ry0), Math.ceil(rx2 - Math.floor(rx0)), Math.ceil(ry2 - Math.floor(ry0)));
        final AffineTransform srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);
        final AffineTransform resAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        final RenderedImage ri = this.getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = this.convertSourceCS(ri);
        final Shape devShape = srcAt.createTransformedShape(aoi);
        final Rectangle2D devRect = r = devShape.getBounds2D();
        r = new Rectangle2D.Double(Math.floor(r.getX() - kx), Math.floor(r.getY() - ky), Math.ceil(r.getX() + r.getWidth()) - Math.floor(r.getX()) + (kw - 1), Math.ceil(r.getY() + r.getHeight()) - Math.floor(r.getY()) + (kh - 1));
        if (!r.getBounds().equals(cr.getBounds())) {
            if (this.edgeMode == PadMode.WRAP) {
                throw new IllegalArgumentException("edgeMode=\"wrap\" is not supported by ConvolveMatrix.");
            }
            cr = new PadRed(cr, r.getBounds(), this.edgeMode, rh);
        }
        if (this.bias != 0.0) {
            throw new IllegalArgumentException("Only bias equal to zero is supported in ConvolveMatrix.");
        }
        final BufferedImageOp op = new ConvolveOp(this.kernel, 1, rh);
        ColorModel cm = cr.getColorModel();
        final Raster rr = cr.getData();
        final WritableRaster wr = GraphicsUtil.makeRasterWritable(rr, 0, 0);
        final int phaseShiftX = this.target.x - this.kernel.getXOrigin();
        final int phaseShiftY = this.target.y - this.kernel.getYOrigin();
        final int destX = (int)(r.getX() + phaseShiftX);
        final int destY = (int)(r.getY() + phaseShiftY);
        BufferedImage destBI;
        if (!this.preserveAlpha) {
            cm = GraphicsUtil.coerceData(wr, cm, true);
            final BufferedImage srcBI = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
            destBI = op.filter(srcBI, null);
            if (this.kernelHasNegValues) {
                this.fixAlpha(destBI);
            }
        }
        else {
            final BufferedImage srcBI = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
            cm = new DirectColorModel(ColorSpace.getInstance(1004), 24, 16711680, 65280, 255, 0, false, 3);
            final BufferedImage tmpSrcBI = new BufferedImage(cm, cm.createCompatibleWritableRaster(wr.getWidth(), wr.getHeight()), cm.isAlphaPremultiplied(), null);
            GraphicsUtil.copyData(srcBI, tmpSrcBI);
            final ColorModel dstCM = GraphicsUtil.Linear_sRGB_Unpre;
            destBI = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(wr.getWidth(), wr.getHeight()), dstCM.isAlphaPremultiplied(), null);
            final WritableRaster dstWR = Raster.createWritableRaster(cm.createCompatibleSampleModel(wr.getWidth(), wr.getHeight()), destBI.getRaster().getDataBuffer(), new Point(0, 0));
            BufferedImage tmpDstBI = new BufferedImage(cm, dstWR, cm.isAlphaPremultiplied(), null);
            tmpDstBI = op.filter(tmpSrcBI, tmpDstBI);
            final Rectangle srcRect = wr.getBounds();
            final Rectangle dstRect = new Rectangle(srcRect.x - phaseShiftX, srcRect.y - phaseShiftY, srcRect.width, srcRect.height);
            GraphicsUtil.copyBand(wr, srcRect, wr.getNumBands() - 1, destBI.getRaster(), dstRect, destBI.getRaster().getNumBands() - 1);
        }
        cr = new BufferedImageCachableRed(destBI, destX, destY);
        cr = new PadRed(cr, devRect.getBounds(), PadMode.ZERO_PAD, rh);
        if (!resAt.isIdentity()) {
            cr = new AffineRed(cr, resAt, null);
        }
        return cr;
    }
}
