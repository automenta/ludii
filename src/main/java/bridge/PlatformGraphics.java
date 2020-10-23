// 
// Decompiled by Procyon v0.5.36
// 

package bridge;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import util.ImageInfo;
import util.locations.Location;
import view.component.BaseComponentStyle;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface PlatformGraphics
{
    Location locationOfClickedImage(final Point p0);
    
    void drawComponent(final Graphics2D p0, final Context p1, final ImageInfo p2);
    
    void drawBoard(final Graphics2D p0, final Rectangle2D p1);
    
    void drawGraph(final Graphics2D p0, final Rectangle2D p1);
    
    void drawConnections(final Graphics2D p0, final Rectangle2D p1);
    
    int getSingleHumanMover(final int p0, final Context p1);
    
    void drawSVG(final Graphics2D p0, final SVGGraphics2D p1, final ImageInfo p2, final BaseComponentStyle p3);
}
