// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;

public interface BridgeUpdateHandler
{
    void handleDOMAttrModifiedEvent(final MutationEvent p0);
    
    void handleDOMNodeInsertedEvent(final MutationEvent p0);
    
    void handleDOMNodeRemovedEvent(final MutationEvent p0);
    
    void handleDOMCharacterDataModified(final MutationEvent p0);
    
    void handleCSSEngineEvent(final CSSEngineEvent p0);
    
    void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue p0);
    
    void handleOtherAnimationChanged(final String p0);
    
    void dispose();
}
