// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.Node;
import org.apache.batik.w3c.dom.events.MutationNameEvent;

public class DOMMutationNameEvent extends DOMMutationEvent implements MutationNameEvent
{
    protected String prevNamespaceURI;
    protected String prevNodeName;
    
    @Override
    public void initMutationNameEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final Node relatedNodeArg, final String prevNamespaceURIArg, final String prevNodeNameArg) {
        this.initMutationEvent(typeArg, canBubbleArg, cancelableArg, relatedNodeArg, null, null, null, (short)0);
        this.prevNamespaceURI = prevNamespaceURIArg;
        this.prevNodeName = prevNodeNameArg;
    }
    
    @Override
    public void initMutationNameEventNS(final String namespaceURI, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final Node relatedNodeArg, final String prevNamespaceURIArg, final String prevNodeNameArg) {
        this.initMutationEventNS(namespaceURI, typeArg, canBubbleArg, cancelableArg, relatedNodeArg, null, null, null, (short)0);
        this.prevNamespaceURI = prevNamespaceURIArg;
        this.prevNodeName = prevNodeNameArg;
    }
    
    @Override
    public String getPrevNamespaceURI() {
        return this.prevNamespaceURI;
    }
    
    @Override
    public String getPrevNodeName() {
        return this.prevNodeName;
    }
}
