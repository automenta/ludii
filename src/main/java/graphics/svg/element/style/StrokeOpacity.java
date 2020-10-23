// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.style;

import graphics.svg.element.Element;

import java.awt.*;

public class StrokeOpacity extends Style
{
    private final double opacity = 1.0;
    
    public StrokeOpacity() {
        super("stroke-opacity");
    }
    
    public double opacity() {
        return 1.0;
    }
    
    @Override
    public Element newOne() {
        return new StrokeOpacity();
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        return true;
    }
    
    @Override
    public Element newInstance() {
        return null;
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public void setBounds() {
    }
}
