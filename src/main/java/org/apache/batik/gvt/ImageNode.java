// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class ImageNode extends CompositeGraphicsNode
{
    protected boolean hitCheckChildren;
    
    public ImageNode() {
        this.hitCheckChildren = false;
    }
    
    @Override
    public void setVisible(final boolean isVisible) {
        this.fireGraphicsNodeChangeStarted();
        this.isVisible = isVisible;
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (!this.isVisible) {
            return null;
        }
        return super.getPrimitiveBounds();
    }
    
    public void setHitCheckChildren(final boolean hitCheckChildren) {
        this.hitCheckChildren = hitCheckChildren;
    }
    
    public boolean getHitCheckChildren() {
        return this.hitCheckChildren;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.isVisible) {
            super.paint(g2d);
        }
    }
    
    @Override
    public boolean contains(final Point2D p) {
        switch (this.pointerEventType) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return this.isVisible && super.contains(p);
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                return super.contains(p);
            }
            case 8: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public GraphicsNode nodeHitAt(final Point2D p) {
        if (this.hitCheckChildren) {
            return super.nodeHitAt(p);
        }
        return this.contains(p) ? this : null;
    }
    
    public void setImage(final GraphicsNode newImage) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        if (this.count == 0) {
            this.ensureCapacity(1);
        }
        this.children[0] = newImage;
        ((AbstractGraphicsNode)newImage).setParent(this);
        ((AbstractGraphicsNode)newImage).setRoot(this.getRoot());
        this.count = 1;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public GraphicsNode getImage() {
        if (this.count > 0) {
            return this.children[0];
        }
        return null;
    }
}
