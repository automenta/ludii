// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.w3c.dom.Element;
import java.util.EventObject;

public class CSSEngineEvent extends EventObject
{
    protected Element element;
    protected int[] properties;
    
    public CSSEngineEvent(final CSSEngine source, final Element elt, final int[] props) {
        super(source);
        this.element = elt;
        this.properties = props;
    }
    
    public Element getElement() {
        return this.element;
    }
    
    public int[] getProperties() {
        return this.properties;
    }
}
