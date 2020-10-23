// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import java.awt.image.BufferedImage;
import java.util.EventObject;

public class UpdateManagerEvent extends EventObject
{
    protected BufferedImage image;
    protected List dirtyAreas;
    protected boolean clearPaintingTransform;
    
    public UpdateManagerEvent(final Object source, final BufferedImage bi, final List das) {
        super(source);
        this.image = bi;
        this.dirtyAreas = das;
        this.clearPaintingTransform = false;
    }
    
    public UpdateManagerEvent(final Object source, final BufferedImage bi, final List das, final boolean cpt) {
        super(source);
        this.image = bi;
        this.dirtyAreas = das;
        this.clearPaintingTransform = cpt;
    }
    
    public BufferedImage getImage() {
        return this.image;
    }
    
    public List getDirtyAreas() {
        return this.dirtyAreas;
    }
    
    public boolean getClearPaintingTransform() {
        return this.clearPaintingTransform;
    }
}
