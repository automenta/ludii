// 
// Decompiled by Procyon v0.5.36
// 

package view.component;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface ComponentStyle
{
    void renderImageSVG(final Context p0, final int p1, final int p2, final boolean p3, final int p4);
    
    void setColour(final Color p0);
    
    void setImageSVG(final int p0, final SVGGraphics2D p1);
    
    void setName(final String p0);
    
    SVGGraphics2D getImageSVG(final int p0);
    
    ArrayList<SVGGraphics2D> getAllImageSVGs();
    
    double scale();
    
    boolean flipHorizontal();
    
    boolean flipVertical();
    
    int rotationDegrees();
    
    ArrayList<Point> origin();
    
    Point largePieceSize();
    
    ArrayList<Point2D> getLargeOffsets();
    
    Color getSecondaryColour();
}
