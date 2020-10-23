// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;

public class TransformAnimation extends SimpleAnimation
{
    protected short type;
    protected float[] keyTimes2;
    protected float[] keyTimes3;
    
    public TransformAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final int calcMode, final float[] keyTimes, final float[] keySplines, final boolean additive, final boolean cumulative, final AnimatableValue[] values, final AnimatableValue from, final AnimatableValue to, final AnimatableValue by, final short type) {
        super(timedElement, animatableElement, (calcMode == 2) ? 1 : calcMode, (float[])((calcMode == 2) ? null : keyTimes), keySplines, additive, cumulative, values, from, to, by);
        this.calcMode = calcMode;
        this.type = type;
        if (calcMode != 2) {
            return;
        }
        final int count = this.values.length;
        float[] cumulativeDistances2 = null;
        float[] cumulativeDistances3 = null;
        switch (type) {
            case 4: {
                cumulativeDistances3 = new float[count];
                cumulativeDistances3[0] = 0.0f;
            }
            case 2:
            case 3: {
                cumulativeDistances2 = new float[count];
                cumulativeDistances2[0] = 0.0f;
                break;
            }
        }
        final float[] cumulativeDistances4 = new float[count];
        cumulativeDistances4[0] = 0.0f;
        for (int i = 1; i < this.values.length; ++i) {
            switch (type) {
                case 4: {
                    cumulativeDistances3[i] = cumulativeDistances3[i - 1] + ((AnimatableTransformListValue)this.values[i - 1]).distanceTo3(this.values[i]);
                }
                case 2:
                case 3: {
                    cumulativeDistances2[i] = cumulativeDistances2[i - 1] + ((AnimatableTransformListValue)this.values[i - 1]).distanceTo2(this.values[i]);
                    break;
                }
            }
            cumulativeDistances4[i] = cumulativeDistances4[i - 1] + ((AnimatableTransformListValue)this.values[i - 1]).distanceTo1(this.values[i]);
        }
        switch (type) {
            case 4: {
                final float totalLength = cumulativeDistances3[count - 1];
                (this.keyTimes3 = new float[count])[0] = 0.0f;
                for (int j = 1; j < count - 1; ++j) {
                    this.keyTimes3[j] = cumulativeDistances3[j] / totalLength;
                }
                this.keyTimes3[count - 1] = 1.0f;
            }
            case 2:
            case 3: {
                final float totalLength = cumulativeDistances2[count - 1];
                (this.keyTimes2 = new float[count])[0] = 0.0f;
                for (int j = 1; j < count - 1; ++j) {
                    this.keyTimes2[j] = cumulativeDistances2[j] / totalLength;
                }
                this.keyTimes2[count - 1] = 1.0f;
                break;
            }
        }
        final float totalLength = cumulativeDistances4[count - 1];
        (this.keyTimes = new float[count])[0] = 0.0f;
        for (int j = 1; j < count - 1; ++j) {
            this.keyTimes[j] = cumulativeDistances4[j] / totalLength;
        }
        this.keyTimes[count - 1] = 1.0f;
    }
    
    @Override
    protected void sampledAtUnitTime(final float unitTime, final int repeatIteration) {
        if (this.calcMode != 2 || this.type == 5 || this.type == 6) {
            super.sampledAtUnitTime(unitTime, repeatIteration);
            return;
        }
        AnimatableTransformListValue value3 = null;
        AnimatableTransformListValue nextValue3 = null;
        float interpolation1 = 0.0f;
        float interpolation2 = 0.0f;
        float interpolation3 = 0.0f;
        AnimatableTransformListValue value4;
        AnimatableTransformListValue nextValue4;
        AnimatableTransformListValue value5;
        AnimatableTransformListValue nextValue5;
        if (unitTime != 1.0f) {
            switch (this.type) {
                case 4: {
                    int keyTimeIndex;
                    for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes3.length - 1 && unitTime >= this.keyTimes3[keyTimeIndex + 1]; ++keyTimeIndex) {}
                    value3 = (AnimatableTransformListValue)this.values[keyTimeIndex];
                    nextValue3 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
                    interpolation3 = (unitTime - this.keyTimes3[keyTimeIndex]) / (this.keyTimes3[keyTimeIndex + 1] - this.keyTimes3[keyTimeIndex]);
                    break;
                }
            }
            int keyTimeIndex;
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes2.length - 1 && unitTime >= this.keyTimes2[keyTimeIndex + 1]; ++keyTimeIndex) {}
            value4 = (AnimatableTransformListValue)this.values[keyTimeIndex];
            nextValue4 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
            interpolation2 = (unitTime - this.keyTimes2[keyTimeIndex]) / (this.keyTimes2[keyTimeIndex + 1] - this.keyTimes2[keyTimeIndex]);
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {}
            value5 = (AnimatableTransformListValue)this.values[keyTimeIndex];
            nextValue5 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
            interpolation1 = (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
        }
        else {
            value4 = (value5 = (value3 = (AnimatableTransformListValue)this.values[this.values.length - 1]));
            nextValue4 = (nextValue5 = (nextValue3 = null));
            interpolation2 = (interpolation1 = (interpolation3 = 1.0f));
        }
        AnimatableTransformListValue accumulation;
        if (this.cumulative) {
            accumulation = (AnimatableTransformListValue)this.values[this.values.length - 1];
        }
        else {
            accumulation = null;
        }
        switch (this.type) {
            case 4: {
                this.value = AnimatableTransformListValue.interpolate((AnimatableTransformListValue)this.value, value5, value4, value3, nextValue5, nextValue4, nextValue3, interpolation1, interpolation2, interpolation3, accumulation, repeatIteration);
                break;
            }
            default: {
                this.value = AnimatableTransformListValue.interpolate((AnimatableTransformListValue)this.value, value5, value4, nextValue5, nextValue4, interpolation1, interpolation2, accumulation, repeatIteration);
                break;
            }
        }
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}
