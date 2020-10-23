// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg12;

import org.apache.batik.dom.xbl.XBLShadowTreeElement;
import org.apache.batik.dom.xbl.ShadowTreeEvent;
import org.apache.batik.dom.events.AbstractEvent;

public class XBLOMShadowTreeEvent extends AbstractEvent implements ShadowTreeEvent
{
    protected XBLShadowTreeElement xblShadowTree;
    
    @Override
    public XBLShadowTreeElement getXblShadowTree() {
        return this.xblShadowTree;
    }
    
    @Override
    public void initShadowTreeEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final XBLShadowTreeElement xblShadowTreeArg) {
        this.initEvent(typeArg, canBubbleArg, cancelableArg);
        this.xblShadowTree = xblShadowTreeArg;
    }
    
    @Override
    public void initShadowTreeEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final XBLShadowTreeElement xblShadowTreeArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.xblShadowTree = xblShadowTreeArg;
    }
}
