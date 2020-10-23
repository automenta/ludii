// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;

public class RasterImageNode extends AbstractGraphicsNode
{
    protected Filter image;
    
    public void setImage(final Filter newImage) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.image = newImage;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public Filter getImage() {
        return this.image;
    }
    
    public Rectangle2D getImageBounds() {
        if (this.image == null) {
            return null;
        }
        return (Rectangle2D)this.image.getBounds2D().clone();
    }
    
    public Filter getGraphicsNodeRable() {
        return this.image;
    }
    
    @Override
    public void primitivePaint(final Graphics2D g2d) {
        if (this.image == null) {
            return;
        }
        GraphicsUtil.drawImage(g2d, this.image);
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (this.image == null) {
            return null;
        }
        return this.image.getBounds2D();
    }
    
    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.image == null) {
            return null;
        }
        return this.image.getBounds2D();
    }
    
    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.image == null) {
            return null;
        }
        return this.image.getBounds2D();
    }
    
    @Override
    public Shape getOutline() {
        if (this.image == null) {
            return null;
        }
        return this.image.getBounds2D();
    }
}
