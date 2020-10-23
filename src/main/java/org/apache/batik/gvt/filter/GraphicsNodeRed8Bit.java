// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.util.Platform;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.util.Map;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;

public class GraphicsNodeRed8Bit extends AbstractRed
{
    private GraphicsNode node;
    private AffineTransform node2dev;
    private RenderingHints hints;
    private boolean usePrimitivePaint;
    
    public GraphicsNodeRed8Bit(final GraphicsNode node, final AffineTransform node2dev, final boolean usePrimitivePaint, final RenderingHints hints) {
        this.node = node;
        this.node2dev = node2dev;
        this.hints = hints;
        this.usePrimitivePaint = usePrimitivePaint;
        AffineTransform at = node2dev;
        Rectangle2D bounds2D = node.getPrimitiveBounds();
        if (bounds2D == null) {
            bounds2D = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 1.0f);
        }
        if (!usePrimitivePaint) {
            final AffineTransform nodeAt = node.getTransform();
            if (nodeAt != null) {
                at = (AffineTransform)at.clone();
                at.concatenate(nodeAt);
            }
        }
        final Rectangle bounds = at.createTransformedShape(bounds2D).getBounds();
        final ColorModel cm = this.createColorModel();
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        final int tgX = defSz * (int)Math.floor(bounds.x / defSz);
        final int tgY = defSz * (int)Math.floor(bounds.y / defSz);
        int tw = bounds.x + bounds.width - tgX;
        if (tw > defSz) {
            tw = defSz;
        }
        int th = bounds.y + bounds.height - tgY;
        if (th > defSz) {
            th = defSz;
        }
        if (tw <= 0 || th <= 0) {
            tw = 1;
            th = 1;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(tw, th);
        this.init((CachableRed)null, bounds, cm, sm, tgX, tgY, null);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        this.genRect(wr);
        return wr;
    }
    
    public void genRect(final WritableRaster wr) {
        final BufferedImage offScreen = new BufferedImage(this.cm, wr.createWritableTranslatedChild(0, 0), this.cm.isAlphaPremultiplied(), null);
        final Graphics2D g = GraphicsUtil.createGraphics(offScreen, this.hints);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, wr.getWidth(), wr.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(-wr.getMinX(), -wr.getMinY());
        g.transform(this.node2dev);
        if (this.usePrimitivePaint) {
            this.node.primitivePaint(g);
        }
        else {
            this.node.paint(g);
        }
        g.dispose();
    }
    
    public ColorModel createColorModel() {
        if (Platform.isOSX) {
            return GraphicsUtil.sRGB_Pre;
        }
        return GraphicsUtil.sRGB_Unpre;
    }
}
