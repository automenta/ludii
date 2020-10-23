// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.Marker;
import org.w3c.dom.Element;

public interface MarkerBridge extends Bridge
{
    Marker createMarker(final BridgeContext p0, final Element p1, final Element p2);
}
