// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.PadMode;
import java.awt.geom.Rectangle2D;

public interface PadRable extends Filter
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    void setPadRect(final Rectangle2D p0);
    
    Rectangle2D getPadRect();
    
    void setPadMode(final PadMode p0);
    
    PadMode getPadMode();
}
