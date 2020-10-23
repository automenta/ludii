// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface FilterPrimitiveBridge extends Bridge
{
    Filter createFilter(final BridgeContext p0, final Element p1, final Element p2, final GraphicsNode p3, final Filter p4, final Rectangle2D p5, final Map p6);
}
