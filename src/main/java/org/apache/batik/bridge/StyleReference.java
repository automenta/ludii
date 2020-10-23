// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

public class StyleReference
{
    private GraphicsNode node;
    private String styleAttribute;
    
    public StyleReference(final GraphicsNode node, final String styleAttribute) {
        this.node = node;
        this.styleAttribute = styleAttribute;
    }
    
    public GraphicsNode getGraphicsNode() {
        return this.node;
    }
    
    public String getStyleAttribute() {
        return this.styleAttribute;
    }
}
