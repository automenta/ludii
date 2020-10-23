// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface MaskBridge extends Bridge
{
    Mask createMask(final BridgeContext p0, final Element p1, final Element p2, final GraphicsNode p3);
}
