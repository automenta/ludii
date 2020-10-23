// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.w3c.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public interface MutationNameEvent extends MutationEvent
{
    String getPrevNamespaceURI();
    
    String getPrevNodeName();
    
    void initMutationNameEvent(final String p0, final boolean p1, final boolean p2, final Node p3, final String p4, final String p5);
    
    void initMutationNameEventNS(final String p0, final String p1, final boolean p2, final boolean p3, final Node p4, final String p5, final String p6);
}
