// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.apache.batik.w3c.dom.events.CustomEvent;

public class DOMCustomEvent extends DOMEvent implements CustomEvent
{
    protected Object detail;
    
    @Override
    public Object getDetail() {
        return this.detail;
    }
    
    @Override
    public void initCustomEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final Object detailArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.detail = detailArg;
    }
}
