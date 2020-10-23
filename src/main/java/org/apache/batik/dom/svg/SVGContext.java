// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public interface SVGContext
{
    public static final int PERCENTAGE_FONT_SIZE = 0;
    public static final int PERCENTAGE_VIEWPORT_WIDTH = 1;
    public static final int PERCENTAGE_VIEWPORT_HEIGHT = 2;
    public static final int PERCENTAGE_VIEWPORT_SIZE = 3;
    
    float getPixelUnitToMillimeter();
    
    float getPixelToMM();
    
    Rectangle2D getBBox();
    
    AffineTransform getScreenTransform();
    
    void setScreenTransform(final AffineTransform p0);
    
    AffineTransform getCTM();
    
    AffineTransform getGlobalTransform();
    
    float getViewportWidth();
    
    float getViewportHeight();
    
    float getFontSize();
}
