// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeUpdateHandler;

public interface SVG12BridgeUpdateHandler extends BridgeUpdateHandler
{
    void handleBindingEvent(final Element p0, final Element p1);
    
    void handleContentSelectionChangedEvent(final ContentSelectionChangedEvent p0);
}
