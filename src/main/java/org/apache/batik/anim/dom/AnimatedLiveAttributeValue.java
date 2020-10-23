// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.LiveAttributeValue;

public interface AnimatedLiveAttributeValue extends LiveAttributeValue
{
    String getNamespaceURI();
    
    String getLocalName();
    
    AnimatableValue getUnderlyingValue(final AnimationTarget p0);
    
    void addAnimatedAttributeListener(final AnimatedAttributeListener p0);
    
    void removeAnimatedAttributeListener(final AnimatedAttributeListener p0);
}
