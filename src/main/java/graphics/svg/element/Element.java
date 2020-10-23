// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element;

import java.awt.*;

public interface Element
{
    String label();
    
    Style style();
    
    int compare(final Element p0);
    
    Element newInstance();
    
    Element newOne();
    
    boolean load(final String p0);
    
    void render(final Graphics2D p0, final double p1, final double p2, final Color p3, final Color p4, final Color p5);
}
