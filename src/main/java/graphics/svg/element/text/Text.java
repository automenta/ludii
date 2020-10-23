// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.text;

import graphics.svg.element.BaseElement;
import graphics.svg.element.Element;

import java.awt.*;

public class Text extends BaseElement
{
    public Text() {
        super("Text");
    }
    
    @Override
    public Element newInstance() {
        return new Text();
    }
    
    @Override
    public boolean load(final String expr) {
        try {
            throw new Exception("SVG text loading not implemented yet.");
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public void setBounds() {
        System.out.println("Text.setBounds() not implemented yet.");
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Text();
    }
}
