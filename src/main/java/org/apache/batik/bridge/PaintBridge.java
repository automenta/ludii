// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Paint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface PaintBridge extends Bridge
{
    Paint createPaint(final BridgeContext p0, final Element p1, final Element p2, final GraphicsNode p3, final float p4);
}
