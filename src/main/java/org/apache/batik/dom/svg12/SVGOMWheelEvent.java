// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg12;

import org.w3c.dom.views.AbstractView;
import org.apache.batik.dom.events.DOMUIEvent;

public class SVGOMWheelEvent extends DOMUIEvent
{
    protected int wheelDelta;
    
    public int getWheelDelta() {
        return this.wheelDelta;
    }
    
    public void initWheelEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int wheelDeltaArg) {
        this.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, 0);
        this.wheelDelta = wheelDeltaArg;
    }
    
    public void initWheelEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int wheelDeltaArg) {
        this.initUIEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg, viewArg, 0);
        this.wheelDelta = wheelDeltaArg;
    }
}
