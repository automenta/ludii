// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.RootGraphicsNode;
import org.w3c.dom.Document;

public interface DocumentBridge extends Bridge
{
    RootGraphicsNode createGraphicsNode(final BridgeContext p0, final Document p1);
    
    void buildGraphicsNode(final BridgeContext p0, final Document p1, final RootGraphicsNode p2);
}
