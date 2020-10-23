// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.geom.Cubic;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;

public class SimpleAnimation extends InterpolatingAnimation
{
    protected AnimatableValue[] values;
    protected AnimatableValue from;
    protected AnimatableValue to;
    protected AnimatableValue by;
    
    public SimpleAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final int calcMode, final float[] keyTimes, final float[] keySplines, final boolean additive, final boolean cumulative, AnimatableValue[] values, final AnimatableValue from, final AnimatableValue to, final AnimatableValue by) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines, additive, cumulative);
        this.from = from;
        this.to = to;
        this.by = by;
        if (values == null) {
            if (from != null) {
                values = new AnimatableValue[] { from, null };
                if (to != null) {
                    values[1] = to;
                }
                else {
                    if (by == null) {
                        throw timedElement.createException("values.to.by.missing", new Object[] { null });
                    }
                    values[1] = from.interpolate(null, null, 0.0f, by, 1);
                }
            }
            else if (to != null) {
                values = new AnimatableValue[] { animatableElement.getUnderlyingValue(), to };
                this.cumulative = false;
                this.toAnimation = true;
            }
            else {
                if (by == null) {
                    throw timedElement.createException("values.to.by.missing", new Object[] { null });
                }
                this.additive = true;
                values = new AnimatableValue[] { by.getZeroValue(), by };
            }
        }
        this.values = values;
        if (this.keyTimes != null && calcMode != 2) {
            if (this.keyTimes.length != values.length) {
                throw timedElement.createException("attribute.malformed", new Object[] { null, "keyTimes" });
            }
        }
        else if (calcMode == 1 || calcMode == 3 || (calcMode == 2 && !values[0].canPace())) {
            final int count = (values.length == 1) ? 2 : values.length;
            this.keyTimes = new float[count];
            for (int i = 0; i < count; ++i) {
                this.keyTimes[i] = i / (float)(count - 1);
            }
        }
        else if (calcMode == 0) {
            final int count = values.length;
            this.keyTimes = new float[count];
            for (int i = 0; i < count; ++i) {
                this.keyTimes[i] = i / (float)count;
            }
        }
        else {
            final int count = values.length;
            final float[] cumulativeDistances = new float[count];
            cumulativeDistances[0] = 0.0f;
            for (int j = 1; j < count; ++j) {
                cumulativeDistances[j] = cumulativeDistances[j - 1] + values[j - 1].distanceTo(values[j]);
            }
            final float totalLength = cumulativeDistances[count - 1];
            (this.keyTimes = new float[count])[0] = 0.0f;
            for (int k = 1; k < count - 1; ++k) {
                this.keyTimes[k] = cumulativeDistances[k] / totalLength;
            }
            this.keyTimes[count - 1] = 1.0f;
        }
        if (calcMode == 3 && keySplines.length != (this.keyTimes.length - 1) * 4) {
            throw timedElement.createException("attribute.malformed", new Object[] { null, "keySplines" });
        }
    }
    
    @Override
    protected void sampledAtUnitTime(final float unitTime, final int repeatIteration) {
        float interpolation = 0.0f;
        AnimatableValue value;
        AnimatableValue nextValue;
        if (unitTime != 1.0f) {
            int keyTimeIndex;
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {}
            value = this.values[keyTimeIndex];
            if (this.calcMode == 1 || this.calcMode == 2 || this.calcMode == 3) {
                nextValue = this.values[keyTimeIndex + 1];
                interpolation = (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
                if (this.calcMode == 3 && unitTime != 0.0f) {
                    final Cubic c = this.keySplineCubics[keyTimeIndex];
                    final float tolerance = 0.001f;
                    float min = 0.0f;
                    float max = 1.0f;
                    Point2D.Double p;
                    while (true) {
                        final float t = (min + max) / 2.0f;
                        p = c.eval(t);
                        final double x = p.getX();
                        if (Math.abs(x - interpolation) < tolerance) {
                            break;
                        }
                        if (x < interpolation) {
                            min = t;
                        }
                        else {
                            max = t;
                        }
                    }
                    interpolation = (float)p.getY();
                }
            }
            else {
                nextValue = null;
            }
        }
        else {
            value = this.values[this.values.length - 1];
            nextValue = null;
        }
        AnimatableValue accumulation;
        if (this.cumulative) {
            accumulation = this.values[this.values.length - 1];
        }
        else {
            accumulation = null;
        }
        this.value = value.interpolate(this.value, nextValue, interpolation, accumulation, repeatIteration);
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}
