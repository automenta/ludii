// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.style;

import graphics.svg.element.BaseElement;
import graphics.svg.element.Element;

public abstract class Style extends BaseElement
{
    public Style(final String label) {
        super(label);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        return true;
    }
    
    @Override
    public Element newOne() {
        return new StrokeDashArray();
    }
}
