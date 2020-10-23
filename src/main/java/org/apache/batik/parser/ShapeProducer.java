// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.awt.Shape;

public interface ShapeProducer
{
    Shape getShape();
    
    void setWindingRule(final int p0);
    
    int getWindingRule();
}
