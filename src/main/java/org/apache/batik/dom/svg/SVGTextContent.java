// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface SVGTextContent
{
    int getNumberOfChars();
    
    Rectangle2D getExtentOfChar(final int p0);
    
    Point2D getStartPositionOfChar(final int p0);
    
    Point2D getEndPositionOfChar(final int p0);
    
    float getRotationOfChar(final int p0);
    
    void selectSubString(final int p0, final int p1);
    
    float getComputedTextLength();
    
    float getSubStringLength(final int p0, final int p1);
    
    int getCharNumAtPosition(final float p0, final float p1);
}
