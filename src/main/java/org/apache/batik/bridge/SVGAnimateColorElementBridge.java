// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.values.AnimatableColorValue;
import org.apache.batik.anim.values.AnimatablePaintValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.ColorAnimation;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;

public class SVGAnimateColorElementBridge extends SVGAnimateElementBridge
{
    @Override
    public String getLocalName() {
        return "animateColor";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGAnimateColorElementBridge();
    }
    
    @Override
    protected AbstractAnimation createAnimation(final AnimationTarget target) {
        final AnimatableValue from = this.parseAnimatableValue("from");
        final AnimatableValue to = this.parseAnimatableValue("to");
        final AnimatableValue by = this.parseAnimatableValue("by");
        return new ColorAnimation(this.timedElement, this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by);
    }
    
    @Override
    protected boolean canAnimateType(final int type) {
        return type == 6 || type == 7;
    }
    
    @Override
    protected boolean checkValueType(final AnimatableValue v) {
        if (v instanceof AnimatablePaintValue) {
            return ((AnimatablePaintValue)v).getPaintType() == 2;
        }
        return v instanceof AnimatableColorValue;
    }
}
