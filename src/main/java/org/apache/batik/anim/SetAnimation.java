// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;

public class SetAnimation extends AbstractAnimation
{
    protected AnimatableValue to;
    
    public SetAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final AnimatableValue to) {
        super(timedElement, animatableElement);
        this.to = to;
    }
    
    @Override
    protected void sampledAt(final float simpleTime, final float simpleDur, final int repeatIteration) {
        if (this.value == null) {
            this.value = this.to;
            this.markDirty();
        }
    }
    
    @Override
    protected void sampledLastValue(final int repeatIteration) {
        if (this.value == null) {
            this.value = this.to;
            this.markDirty();
        }
    }
}
