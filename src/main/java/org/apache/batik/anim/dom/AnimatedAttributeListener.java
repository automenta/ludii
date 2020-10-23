// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Element;

public interface AnimatedAttributeListener
{
    void animatedAttributeChanged(final Element p0, final AnimatedLiveAttributeValue p1);
    
    void otherAnimationChanged(final Element p0, final String p1);
}
