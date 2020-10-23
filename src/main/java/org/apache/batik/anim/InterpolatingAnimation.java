// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.ext.awt.geom.Cubic;

public abstract class InterpolatingAnimation extends AbstractAnimation
{
    protected int calcMode;
    protected float[] keyTimes;
    protected float[] keySplines;
    protected Cubic[] keySplineCubics;
    protected boolean additive;
    protected boolean cumulative;
    
    public InterpolatingAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final int calcMode, final float[] keyTimes, final float[] keySplines, final boolean additive, final boolean cumulative) {
        super(timedElement, animatableElement);
        this.calcMode = calcMode;
        this.keyTimes = keyTimes;
        this.keySplines = keySplines;
        this.additive = additive;
        this.cumulative = cumulative;
        if (calcMode == 3) {
            if (keySplines == null || keySplines.length % 4 != 0) {
                throw timedElement.createException("attribute.malformed", new Object[] { null, "keySplines" });
            }
            this.keySplineCubics = new Cubic[keySplines.length / 4];
            for (int i = 0; i < keySplines.length / 4; ++i) {
                this.keySplineCubics[i] = new Cubic(0.0, 0.0, keySplines[i * 4], keySplines[i * 4 + 1], keySplines[i * 4 + 2], keySplines[i * 4 + 3], 1.0, 1.0);
            }
        }
        if (keyTimes != null) {
            boolean invalidKeyTimes = false;
            if (((calcMode == 1 || calcMode == 3 || calcMode == 2) && (keyTimes.length < 2 || keyTimes[0] != 0.0f || keyTimes[keyTimes.length - 1] != 1.0f)) || (calcMode == 0 && (keyTimes.length == 0 || keyTimes[0] != 0.0f))) {
                invalidKeyTimes = true;
            }
            if (!invalidKeyTimes) {
                for (int j = 1; j < keyTimes.length; ++j) {
                    if (keyTimes[j] < 0.0f || keyTimes[1] > 1.0f || keyTimes[j] < keyTimes[j - 1]) {
                        invalidKeyTimes = true;
                        break;
                    }
                }
            }
            if (invalidKeyTimes) {
                throw timedElement.createException("attribute.malformed", new Object[] { null, "keyTimes" });
            }
        }
    }
    
    @Override
    protected boolean willReplace() {
        return !this.additive;
    }
    
    @Override
    protected void sampledLastValue(final int repeatIteration) {
        this.sampledAtUnitTime(1.0f, repeatIteration);
    }
    
    @Override
    protected void sampledAt(final float simpleTime, final float simpleDur, final int repeatIteration) {
        float unitTime;
        if (simpleDur == Float.POSITIVE_INFINITY) {
            unitTime = 0.0f;
        }
        else {
            unitTime = simpleTime / simpleDur;
        }
        this.sampledAtUnitTime(unitTime, repeatIteration);
    }
    
    protected abstract void sampledAtUnitTime(final float p0, final int p1);
}
