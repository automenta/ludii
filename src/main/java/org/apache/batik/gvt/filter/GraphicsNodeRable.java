// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface GraphicsNodeRable extends Filter
{
    GraphicsNode getGraphicsNode();
    
    void setGraphicsNode(final GraphicsNode p0);
    
    boolean getUsePrimitivePaint();
    
    void setUsePrimitivePaint(final boolean p0);
}
