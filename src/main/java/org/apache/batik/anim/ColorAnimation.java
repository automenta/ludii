// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;

public class ColorAnimation extends SimpleAnimation
{
    public ColorAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final int calcMode, final float[] keyTimes, final float[] keySplines, final boolean additive, final boolean cumulative, final AnimatableValue[] values, final AnimatableValue from, final AnimatableValue to, final AnimatableValue by) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines, additive, cumulative, values, from, to, by);
    }
}
