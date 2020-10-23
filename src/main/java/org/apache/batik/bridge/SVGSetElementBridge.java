// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.SetAnimation;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;

public class SVGSetElementBridge extends SVGAnimationElementBridge
{
    @Override
    public String getLocalName() {
        return "set";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGSetElementBridge();
    }
    
    @Override
    protected AbstractAnimation createAnimation(final AnimationTarget target) {
        final AnimatableValue to = this.parseAnimatableValue("to");
        return new SetAnimation(this.timedElement, this, to);
    }
    
    @Override
    protected boolean canAnimateType(final int type) {
        return true;
    }
    
    @Override
    protected boolean isConstantAnimation() {
        return true;
    }
}
