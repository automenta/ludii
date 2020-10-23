// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface Mask extends Filter
{
    Rectangle2D getFilterRegion();
    
    void setFilterRegion(final Rectangle2D p0);
    
    void setSource(final Filter p0);
    
    Filter getSource();
    
    void setMaskNode(final GraphicsNode p0);
    
    GraphicsNode getMaskNode();
}
