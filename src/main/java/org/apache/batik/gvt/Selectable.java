// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.Shape;

public interface Selectable
{
    boolean selectAt(final double p0, final double p1);
    
    boolean selectTo(final double p0, final double p1);
    
    boolean selectAll(final double p0, final double p1);
    
    Object getSelection();
    
    Shape getHighlightShape();
}
