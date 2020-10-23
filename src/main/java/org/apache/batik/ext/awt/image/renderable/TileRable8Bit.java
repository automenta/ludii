// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Graphics2D;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.TileRed;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;

public class TileRable8Bit extends AbstractColorInterpolationRable implements TileRable
{
    private Rectangle2D tileRegion;
    private Rectangle2D tiledRegion;
    private boolean overflow;
    
    @Override
    public Rectangle2D getTileRegion() {
        return this.tileRegion;
    }
    
    @Override
    public void setTileRegion(final Rectangle2D tileRegion) {
        if (tileRegion == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.tileRegion = tileRegion;
    }
    
    @Override
    public Rectangle2D getTiledRegion() {
        return this.tiledRegion;
    }
    
    @Override
    public void setTiledRegion(final Rectangle2D tiledRegion) {
        if (tiledRegion == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.tiledRegion = tiledRegion;
    }
    
    @Override
    public boolean isOverflow() {
        return this.overflow;
    }
    
    @Override
    public void setOverflow(final boolean overflow) {
        this.touch();
        this.overflow = overflow;
    }
    
    public TileRable8Bit(final Filter source, final Rectangle2D tiledRegion, final Rectangle2D tileRegion, final boolean overflow) {
        super(source);
        this.setTileRegion(tileRegion);
        this.setTiledRegion(tiledRegion);
        this.setOverflow(overflow);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src);
    }
    
    @Override
    public Filter getSource() {
        return this.srcs.get(0);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.tiledRegion.clone();
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
        final double scaleX = Math.sqrt(sx * sx + shy * shy);
        final double scaleY = Math.sqrt(sy * sy + shx * shx);
        final Rectangle2D tiledRect = this.getBounds2D();
        final Shape aoiShape = rc.getAreaOfInterest();
        Rectangle2D aoiRect;
        if (aoiShape == null) {
            aoiRect = tiledRect;
        }
        else {
            aoiRect = aoiShape.getBounds2D();
            if (!tiledRect.intersects(aoiRect)) {
                return null;
            }
            Rectangle2D.intersect(tiledRect, aoiRect, tiledRect);
        }
        final Rectangle2D tileRect = this.tileRegion;
        final int dw = (int)Math.ceil(tileRect.getWidth() * scaleX);
        final int dh = (int)Math.ceil(tileRect.getHeight() * scaleY);
        final double tileScaleX = dw / tileRect.getWidth();
        final double tileScaleY = dh / tileRect.getHeight();
        final int dx = (int)Math.floor(tileRect.getX() * tileScaleX);
        final int dy = (int)Math.floor(tileRect.getY() * tileScaleY);
        final double ttx = dx - tileRect.getX() * tileScaleX;
        final double tty = dy - tileRect.getY() * tileScaleY;
        final AffineTransform tileAt = AffineTransform.getTranslateInstance(ttx, tty);
        tileAt.scale(tileScaleX, tileScaleY);
        final Filter source = this.getSource();
        Rectangle2D srcRect;
        if (this.overflow) {
            srcRect = source.getBounds2D();
        }
        else {
            srcRect = tileRect;
        }
        final RenderContext tileRc = new RenderContext(tileAt, srcRect, rh);
        RenderedImage tileRed = source.createRendering(tileRc);
        if (tileRed == null) {
            return null;
        }
        Rectangle tiledArea = tileAt.createTransformedShape(aoiRect).getBounds();
        if (tiledArea.width == Integer.MAX_VALUE || tiledArea.height == Integer.MAX_VALUE) {
            tiledArea = new Rectangle(-536870912, -536870912, 1073741823, 1073741823);
        }
        tileRed = this.convertSourceCS(tileRed);
        final TileRed tiledRed = new TileRed(tileRed, tiledArea, dw, dh);
        final AffineTransform shearAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        shearAt.scale(scaleX / tileScaleX, scaleY / tileScaleY);
        shearAt.translate(-ttx, -tty);
        CachableRed cr = tiledRed;
        if (!shearAt.isIdentity()) {
            cr = new AffineRed(tiledRed, shearAt, rh);
        }
        return cr;
    }
    
    public Rectangle2D getActualTileBounds(final Rectangle2D tiledRect) {
        final Rectangle2D tileRect = (Rectangle2D)this.tileRegion.clone();
        if (tileRect.getWidth() <= 0.0 || tileRect.getHeight() <= 0.0 || tiledRect.getWidth() <= 0.0 || tiledRect.getHeight() <= 0.0) {
            return null;
        }
        final double tileWidth = tileRect.getWidth();
        final double tileHeight = tileRect.getHeight();
        final double tiledWidth = tiledRect.getWidth();
        final double tiledHeight = tiledRect.getHeight();
        final double w = Math.min(tileWidth, tiledWidth);
        final double h = Math.min(tileHeight, tiledHeight);
        final Rectangle2D realTileRect = new Rectangle2D.Double(tileRect.getX(), tileRect.getY(), w, h);
        return realTileRect;
    }
    
    public RenderedImage createTile(final RenderContext rc) {
        final AffineTransform usr2dev = rc.getTransform();
        final RenderingHints rcHints = rc.getRenderingHints();
        final RenderingHints hints = new RenderingHints(null);
        if (rcHints != null) {
            hints.add(rcHints);
        }
        final Rectangle2D tiledRect = this.getBounds2D();
        final Shape aoiShape = rc.getAreaOfInterest();
        final Rectangle2D aoiRect = aoiShape.getBounds2D();
        if (!tiledRect.intersects(aoiRect)) {
            return null;
        }
        Rectangle2D.intersect(tiledRect, aoiRect, tiledRect);
        final Rectangle2D tileRect = (Rectangle2D)this.tileRegion.clone();
        if (tileRect.getWidth() <= 0.0 || tileRect.getHeight() <= 0.0 || tiledRect.getWidth() <= 0.0 || tiledRect.getHeight() <= 0.0) {
            return null;
        }
        final double tileX = tileRect.getX();
        final double tileY = tileRect.getY();
        final double tileWidth = tileRect.getWidth();
        final double tileHeight = tileRect.getHeight();
        final double tiledX = tiledRect.getX();
        final double tiledY = tiledRect.getY();
        final double tiledWidth = tiledRect.getWidth();
        final double tiledHeight = tiledRect.getHeight();
        final double w = Math.min(tileWidth, tiledWidth);
        final double h = Math.min(tileHeight, tiledHeight);
        double dx = (tiledX - tileX) % tileWidth;
        double dy = (tiledY - tileY) % tileHeight;
        if (dx > 0.0) {
            dx = tileWidth - dx;
        }
        else {
            dx *= -1.0;
        }
        if (dy > 0.0) {
            dy = tileHeight - dy;
        }
        else {
            dy *= -1.0;
        }
        final double scaleX = usr2dev.getScaleX();
        final double scaleY = usr2dev.getScaleY();
        final double tdx = Math.floor(scaleX * dx);
        final double tdy = Math.floor(scaleY * dy);
        dx = tdx / scaleX;
        dy = tdy / scaleY;
        final Rectangle2D.Double A = new Rectangle2D.Double(tileX + tileWidth - dx, tileY + tileHeight - dy, dx, dy);
        final Rectangle2D.Double B = new Rectangle2D.Double(tileX, tileY + tileHeight - dy, w - dx, dy);
        final Rectangle2D.Double C = new Rectangle2D.Double(tileX + tileWidth - dx, tileY, dx, h - dy);
        final Rectangle2D.Double D = new Rectangle2D.Double(tileX, tileY, w - dx, h - dy);
        final Rectangle2D realTileRect = new Rectangle2D.Double(tiledRect.getX(), tiledRect.getY(), w, h);
        RenderedImage ARed = null;
        RenderedImage BRed = null;
        RenderedImage CRed = null;
        RenderedImage DRed = null;
        final Filter source = this.getSource();
        if (A.getWidth() > 0.0 && A.getHeight() > 0.0) {
            final Rectangle devA = usr2dev.createTransformedShape(A).getBounds();
            if (devA.width > 0 && devA.height > 0) {
                final AffineTransform ATxf = new AffineTransform(usr2dev);
                ATxf.translate(-A.x + tiledX, -A.y + tiledY);
                Shape aoi = A;
                if (this.overflow) {
                    aoi = new Rectangle2D.Double(A.x, A.y, tiledWidth, tiledHeight);
                }
                hints.put(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST, aoi);
                final RenderContext arc = new RenderContext(ATxf, aoi, hints);
                ARed = source.createRendering(arc);
            }
        }
        if (B.getWidth() > 0.0 && B.getHeight() > 0.0) {
            final Rectangle devB = usr2dev.createTransformedShape(B).getBounds();
            if (devB.width > 0 && devB.height > 0) {
                final AffineTransform BTxf = new AffineTransform(usr2dev);
                BTxf.translate(-B.x + (tiledX + dx), -B.y + tiledY);
                Shape aoi = B;
                if (this.overflow) {
                    aoi = new Rectangle2D.Double(B.x - tiledWidth + w - dx, B.y, tiledWidth, tiledHeight);
                }
                hints.put(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST, aoi);
                final RenderContext brc = new RenderContext(BTxf, aoi, hints);
                BRed = source.createRendering(brc);
            }
        }
        if (C.getWidth() > 0.0 && C.getHeight() > 0.0) {
            final Rectangle devC = usr2dev.createTransformedShape(C).getBounds();
            if (devC.width > 0 && devC.height > 0) {
                final AffineTransform CTxf = new AffineTransform(usr2dev);
                CTxf.translate(-C.x + tiledX, -C.y + (tiledY + dy));
                Shape aoi = C;
                if (this.overflow) {
                    aoi = new Rectangle2D.Double(C.x, C.y - tileHeight + h - dy, tiledWidth, tiledHeight);
                }
                hints.put(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST, aoi);
                final RenderContext crc = new RenderContext(CTxf, aoi, hints);
                CRed = source.createRendering(crc);
            }
        }
        if (D.getWidth() > 0.0 && D.getHeight() > 0.0) {
            final Rectangle devD = usr2dev.createTransformedShape(D).getBounds();
            if (devD.width > 0 && devD.height > 0) {
                final AffineTransform DTxf = new AffineTransform(usr2dev);
                DTxf.translate(-D.x + (tiledX + dx), -D.y + (tiledY + dy));
                Shape aoi = D;
                if (this.overflow) {
                    aoi = new Rectangle2D.Double(D.x - tileWidth + w - dx, D.y - tileHeight + h - dy, tiledWidth, tiledHeight);
                }
                hints.put(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST, aoi);
                final RenderContext drc = new RenderContext(DTxf, aoi, hints);
                DRed = source.createRendering(drc);
            }
        }
        final Rectangle realTileRectDev = usr2dev.createTransformedShape(realTileRect).getBounds();
        if (realTileRectDev.width == 0 || realTileRectDev.height == 0) {
            return null;
        }
        final BufferedImage realTileBI = new BufferedImage(realTileRectDev.width, realTileRectDev.height, 2);
        final Graphics2D g = GraphicsUtil.createGraphics(realTileBI, rc.getRenderingHints());
        g.translate(-realTileRectDev.x, -realTileRectDev.y);
        final AffineTransform redTxf = new AffineTransform();
        final Point2D.Double redVec = new Point2D.Double();
        RenderedImage refRed = null;
        if (ARed != null) {
            g.drawRenderedImage(ARed, redTxf);
            refRed = ARed;
        }
        if (BRed != null) {
            if (refRed == null) {
                refRed = BRed;
            }
            redVec.x = dx;
            redVec.y = 0.0;
            usr2dev.deltaTransform(redVec, redVec);
            redVec.x = Math.floor(redVec.x) - (BRed.getMinX() - refRed.getMinX());
            redVec.y = Math.floor(redVec.y) - (BRed.getMinY() - refRed.getMinY());
            g.drawRenderedImage(BRed, redTxf);
        }
        if (CRed != null) {
            if (refRed == null) {
                refRed = CRed;
            }
            redVec.x = 0.0;
            redVec.y = dy;
            usr2dev.deltaTransform(redVec, redVec);
            redVec.x = Math.floor(redVec.x) - (CRed.getMinX() - refRed.getMinX());
            redVec.y = Math.floor(redVec.y) - (CRed.getMinY() - refRed.getMinY());
            g.drawRenderedImage(CRed, redTxf);
        }
        if (DRed != null) {
            if (refRed == null) {
                refRed = DRed;
            }
            redVec.x = dx;
            redVec.y = dy;
            usr2dev.deltaTransform(redVec, redVec);
            redVec.x = Math.floor(redVec.x) - (DRed.getMinX() - refRed.getMinX());
            redVec.y = Math.floor(redVec.y) - (DRed.getMinY() - refRed.getMinY());
            g.drawRenderedImage(DRed, redTxf);
        }
        final CachableRed realTile = new BufferedImageCachableRed(realTileBI, realTileRectDev.x, realTileRectDev.y);
        return realTile;
    }
}
