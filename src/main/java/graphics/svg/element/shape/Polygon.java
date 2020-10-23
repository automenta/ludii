// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.element.Element;

import java.awt.*;

public class Polygon extends Polyline
{
    public Polygon() {
        super("polygon");
    }
    
    @Override
    public Element newInstance() {
        return new Polygon();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
}
