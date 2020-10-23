// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface GraphicsNodeBridge extends Bridge
{
    GraphicsNode createGraphicsNode(final BridgeContext p0, final Element p1);
    
    void buildGraphicsNode(final BridgeContext p0, final Element p1, final GraphicsNode p2);
    
    boolean isComposite();
    
    boolean getDisplay(final Element p0);
    
    Bridge getInstance();
}
