// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import java.awt.*;

public class EdgeInfoGUI
{
    private LineStyle style;
    private Color colour;
    
    public EdgeInfoGUI(final LineStyle style, final Color colour) {
        this.setStyle(style);
        this.setColour(colour);
    }
    
    public LineStyle getStyle() {
        return this.style;
    }
    
    public void setStyle(final LineStyle style) {
        this.style = style;
    }
    
    public Color getColour() {
        return this.colour;
    }
    
    public void setColour(final Color colour) {
        this.colour = colour;
    }
}
