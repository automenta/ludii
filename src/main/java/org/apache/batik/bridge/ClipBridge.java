// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface ClipBridge extends Bridge
{
    ClipRable createClip(final BridgeContext p0, final Element p1, final Element p2, final GraphicsNode p3);
}
