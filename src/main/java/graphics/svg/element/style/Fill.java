// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.style;

import graphics.svg.element.Element;

import java.awt.*;

public class Fill extends Style
{
    private final Color colour;
    
    public Fill() {
        super("fill");
        this.colour = new Color(0, 0, 0);
    }
    
    public Color colour() {
        return this.colour;
    }
    
    @Override
    public Element newOne() {
        return new Fill();
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
