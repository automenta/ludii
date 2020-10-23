// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.views.AbstractView;
import org.apache.batik.w3c.dom.events.TextEvent;

public class DOMTextEvent extends DOMUIEvent implements TextEvent
{
    protected String data;
    
    @Override
    public String getData() {
        return this.data;
    }
    
    @Override
    public void initTextEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final String dataArg) {
        this.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, 0);
        this.data = dataArg;
    }
    
    @Override
    public void initTextEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final String dataArg) {
        this.initUIEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg, viewArg, 0);
        this.data = dataArg;
    }
}
