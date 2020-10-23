// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.style;

import graphics.svg.element.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrokeDashArray extends Style
{
    private final List<Integer> dash;
    
    public StrokeDashArray() {
        super("stroke-dasharray");
        this.dash = new ArrayList<>();
    }
    
    public List<Integer> dash() {
        return Collections.unmodifiableList(this.dash);
    }
    
    @Override
    public Element newOne() {
        return new StrokeDashArray();
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
